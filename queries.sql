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


-- -----------------------------------------------
-- PARKING LOTS (Person 2 -- /lots, /lots/{id})
-- -----------------------------------------------

-- [Person 2: add lot listing, lot detail, and admin CRUD queries here]


-- -----------------------------------------------
-- SPOT REPORTING (Person 3 -- /report, /lots/{id}/reports)
-- -----------------------------------------------

-- [Person 3: add report insertion and report history queries here]


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
