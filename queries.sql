-- ============================================================
-- UGA Parking Spot Tracker -- All Application Queries
-- Format: brief purpose comment + URL path above each query
-- ============================================================

-- -----------------------------------------------
-- AUTH (Person 1 -- /login, /register, /profile)
-- -----------------------------------------------

-- Authenticate a user by username; password hash checked in Java via BCrypt.
-- URL: POST /login
SELECT * FROM user WHERE username = ?;

-- Register a new user account with a BCrypt-hashed password.
-- URL: POST /register
INSERT INTO user (username, password, firstName, lastName)
VALUES (?, ?, ?, ?);

-- [Person 1: add profile view/update queries here]

-- Fetch user details to display on the profile management page.
-- URL: GET /profile
SELECT userId, username, firstName, lastName FROM user WHERE userId = ?;

-- Update the first and last name of a registered user.
-- URL: POST /profile
UPDATE user SET firstName = ?, lastName = ? WHERE userId = ?;

-- -----------------------------------------------
-- PARKING LOTS (Person 2 -- /lots, /lots/{id})
-- -----------------------------------------------

-- Displays all parking lots on the main lots page, including the most recent
-- crowd-sourced report for each lot and whether the logged-in user favorited it.
-- Optional filters search by name/address and payment type.
-- The last two WHERE conditions are added only when the user enters a
-- search value or chooses a payment type filter.
-- URL: GET /lots
SELECT l.lotId, l.name, l.address, l.totalCapacity, l.paymentType, l.latitude, l.longitude,
       sr.packedLevel, sr.hasOpenSpots, sr.reportedAt AS lastReported,
       IF(f.userId IS NOT NULL, TRUE, FALSE) AS isFavorited
FROM lot l
LEFT JOIN spot_report sr ON sr.reportId = (
    SELECT reportId
    FROM spot_report
    WHERE lotId = l.lotId
    ORDER BY reportedAt DESC
    LIMIT 1
)
LEFT JOIN favorite f ON f.lotId = l.lotId AND f.userId = ?
WHERE 1 = 1
  AND (l.name LIKE ? OR l.address LIKE ?)
  AND l.paymentType = ?
ORDER BY l.name ASC;

-- Fetches one parking lot and its latest report for the lot detail page.
-- URL: GET /lots/{lotId}
SELECT l.lotId, l.name, l.address, l.totalCapacity, l.paymentType, l.latitude, l.longitude,
       sr.packedLevel, sr.hasOpenSpots, sr.reportedAt AS lastReported
FROM lot l
LEFT JOIN spot_report sr ON sr.reportId = (
    SELECT reportId
    FROM spot_report
    WHERE lotId = l.lotId
    ORDER BY reportedAt DESC
    LIMIT 1
)
WHERE l.lotId = ?;

-- Inserts a new parking lot from the add lot form.
-- URL: POST /lots/add
INSERT INTO lot (name, address, totalCapacity, paymentType, latitude, longitude)
VALUES (?, ?, ?, ?, ?, ?);

-- Updates an existing parking lot from the edit lot form.
-- URL: POST /lots/{lotId}/edit
UPDATE lot
SET name = ?, address = ?, totalCapacity = ?, paymentType = ?, latitude = ?, longitude = ?
WHERE lotId = ?;

-- Deletes all reports connected to a lot before removing the lot itself.
-- URL: POST /lots/{lotId}/delete
DELETE FROM spot_report
WHERE lotId = ?;

-- Deletes the selected parking lot after its dependent reports are removed.
-- URL: POST /lots/{lotId}/delete
DELETE FROM lot
WHERE lotId = ?;


-- -----------------------------------------------
-- SPOT REPORTING (Person 3 -- /report, /lots/{id}/reports)
-- -----------------------------------------------

-- Fetches all lot names and IDs for the report form dropdown.
-- Simple scan of the lot table (15 rows), ordered alphabetically for UX.
-- URL: GET /report
SELECT lotId, name
FROM lot
ORDER BY name ASC;

-- Inserts a new crowd-sourced spot report for a given lot.
-- reportedAt defaults to CURRENT_TIMESTAMP per DDL (not set here).
-- notes is NULL when the user leaves the textarea empty.
-- URL: POST /report
INSERT INTO spot_report (userId, lotId, packedLevel, hasOpenSpots, notes)
VALUES (?, ?, ?, ?, ?);

-- Returns the 50 most recent reports for a specific lot, newest first.
-- JOIN with user to retrieve the reporter's username for display.
-- DATE_FORMAT produces a human-readable timestamp matching the analytics style.
-- Uses idx_report_lot index on lotId for efficient filtered scan.
-- URL: GET /lots/{id}/reports
SELECT u.username, sr.packedLevel, sr.hasOpenSpots,
       DATE_FORMAT(CONVERT_TZ(sr.reportedAt, '+00:00', '-04:00'), '%b %d %h:%i %p') AS reportedAt,
       sr.notes
FROM spot_report sr
JOIN user u ON sr.userId = u.userId
WHERE sr.lotId = ?
ORDER BY sr.reportedAt DESC
LIMIT 50;

-- Retrieves lot name for the report history page heading.
-- Simple primary key lookup — uses clustered index.
-- URL: GET /lots/{id}/reports
SELECT name
FROM lot
WHERE lotId = ?;


-- -----------------------------------------------
-- FAVORITES & DASHBOARD (Person 4 -- /favorites)
-- -----------------------------------------------

-- Add a lot to favorites tab.
-- URL: POST /favorites/add
INSERT IGNORE INTO favorite (userId, lotId) VALUES (?, ?);

-- Remove a lot from favorites tab.
-- URL: POST /favorites/remove
DELETE FROM favorite WHERE userId = ? AND lotId = ?;

-- Get all favorited lots with most recent packed status.
-- LEFT JOIN to include lots with zero reports.
-- URL: GET /favorites
SELECT l.lotId, l.name, l.address, l.paymentType,
       sr.packedLevel, sr.hasOpenSpots,
       DATE_FORMAT(sr.reportedAt, '%b %d %h:%i %p') AS lastReported
FROM favorite f
JOIN lot l ON f.lotId = l.lotId
LEFT JOIN spot_report sr ON sr.lotId = l.lotId
  AND sr.reportedAt = (
      SELECT MAX(sr2.reportedAt) FROM spot_report sr2 WHERE sr2.lotId = l.lotId
  )
WHERE f.userId = ?
ORDER BY l.name ASC;

-- Fetch the 20 most recent spot reports among the user's favorited lots.
-- JOIN filters to include only user's favorited lots.
-- URL: GET /dashboard
SELECT l.name AS lotName, sr.packedLevel, sr.hasOpenSpots,
       u.username AS reporter,
       DATE_FORMAT(sr.reportedAt, '%b %d %h:%i %p') AS reportedAt
FROM spot_report sr
JOIN lot l ON sr.lotId = l.lotId
JOIN user u ON sr.userId = u.userId
JOIN favorite f ON f.lotId = sr.lotId AND f.userId = ?
ORDER BY sr.reportedAt DESC
LIMIT 20;

-- Aggregate query: aerage packed level per lot per day across all the user's favorited lots.
-- Aggregation: AVG with CASE expression grouped by date.
-- URL: GET /dashboard
SELECT reportDate, dateLabel, AVG(numericLevel) AS avgLevel
FROM (
    SELECT DATE(sr.reportedAt) AS reportDate,
           DATE_FORMAT(DATE(sr.reportedAt), '%b %d') AS dateLabel,
           CASE sr.packedLevel
               WHEN 'Empty'    THEN 1
               WHEN 'Light'    THEN 2
               WHEN 'Moderate' THEN 3
               WHEN 'Busy'     THEN 4
               WHEN 'Full'     THEN 5
           END AS numericLevel
    FROM spot_report sr
    JOIN favorite f ON f.lotId = sr.lotId AND f.userId = ?
    WHERE sr.reportedAt >= NOW() - INTERVAL 7 DAY
) sub
GROUP BY reportDate, dateLabel
ORDER BY reportDate ASC;

-- -----------------------------------------------
-- ANALYTICS (Person 5 -- /analytics)
-- -----------------------------------------------

-- Returns the hours of day with the most Busy/Full reports,
-- showing when UGA lots are hardest to find parking.
-- Aggregation: COUNT grouped by hour. Uses idx_report_time index.
-- URL: GET /analytics
SELECT HOUR(reportedAt) AS hour, COUNT(*) AS reportCount
FROM spot_report
WHERE packedLevel IN ('Busy', 'Full')
GROUP BY HOUR(reportedAt)
ORDER BY reportCount DESC
LIMIT 8;

-- Returns up to 10 lots ordered from most to least available based on their
-- most recent crowd-sourced report. Correlated subquery finds the latest
-- report per lot; CASE expression converts packedLevel to a sort order.
-- Join: lot JOIN spot_report. Uses idx_report_lot index.
-- URL: GET /analytics
SELECT l.name, sr.packedLevel, sr.hasOpenSpots, l.paymentType,
       DATE_FORMAT(sr.reportedAt, '%b %d %h:%i %p') AS lastReported
FROM lot l
JOIN spot_report sr ON l.lotId = sr.lotId
WHERE sr.reportedAt = (
    SELECT MAX(sr2.reportedAt) FROM spot_report sr2 WHERE sr2.lotId = l.lotId
)
ORDER BY CASE sr.packedLevel
    WHEN 'Empty'    THEN 1
    WHEN 'Light'    THEN 2
    WHEN 'Moderate' THEN 3
    WHEN 'Busy'     THEN 4
    WHEN 'Full'     THEN 5
END ASC
LIMIT 10;

-- Returns the top 10 users ranked by number of spot reports submitted.
-- Aggregation: COUNT grouped by user. Join: user JOIN spot_report.
-- URL: GET /analytics
SELECT u.firstName, u.lastName, u.username, COUNT(*) AS reportCount
FROM user u
JOIN spot_report sr ON u.userId = sr.userId
GROUP BY u.userId, u.firstName, u.lastName, u.username
ORDER BY reportCount DESC
LIMIT 10;
