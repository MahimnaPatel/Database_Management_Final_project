CREATE DATABASE IF NOT EXISTS uga_parking;
USE uga_parking;

CREATE TABLE IF NOT EXISTS user (
    userId    INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  UNIQUE NOT NULL,
    password  VARCHAR(255) NOT NULL,
    firstName VARCHAR(50)  NOT NULL,
    lastName  VARCHAR(50)  NOT NULL
);

-- paymentType: ParkMobile    = pay via app all day
--              Free           = always free
--              Free_After_5PM = ParkMobile during day, free after 5 PM
CREATE TABLE IF NOT EXISTS lot (
    lotId         INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    address       VARCHAR(200) NOT NULL,
    totalCapacity INT          NOT NULL,
    paymentType   ENUM('ParkMobile','Free','Free_After_5PM') NOT NULL,
    latitude      DECIMAL(9,6),
    longitude     DECIMAL(9,6)
);

-- Central crowd-sourced table: students report how packed a lot is and
-- whether any open spots remain. High-volume table (1000+ rows requirement).
-- packedLevel: student's eyeball estimate of crowdedness
-- hasOpenSpots: quick yes/no on whether a spot is visibly available
CREATE TABLE IF NOT EXISTS spot_report (
    reportId     INT AUTO_INCREMENT PRIMARY KEY,
    userId       INT      NOT NULL,
    lotId        INT      NOT NULL,
    packedLevel  ENUM('Empty','Light','Moderate','Busy','Full') NOT NULL,
    hasOpenSpots BOOLEAN  NOT NULL,
    reportedAt   DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes        TEXT,
    FOREIGN KEY (userId) REFERENCES user(userId),
    FOREIGN KEY (lotId)  REFERENCES lot(lotId)
);

CREATE TABLE IF NOT EXISTS favorite (
    userId    INT      NOT NULL,
    lotId     INT      NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userId, lotId),
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (lotId)  REFERENCES lot(lotId)  ON DELETE CASCADE
);

-- Indexes (see perf.txt for before/after benchmarks)
CREATE INDEX idx_report_lot  ON spot_report(lotId);
CREATE INDEX idx_report_time ON spot_report(reportedAt);
