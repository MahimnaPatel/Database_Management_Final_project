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

-- [Person 2: add lot listing, lot detail, and admin CRUD queries here]


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

-- [Person 4: add favorite add/remove and dashboard queries here]


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
