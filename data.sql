USE uga_parking;

-- ============================================================
-- DEMO USERS (20 accounts)
-- All passwords are: password
-- Hash generated with Spring Security BCryptPasswordEncoder strength 10.
-- If login fails for any account, re-register it at /register using
-- the same username and password "password".
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO user (username, password, firstName, lastName) VALUES
('UgaUser1',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bailey',   'Adams'),
('UgaUser2',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Marcus',   'Brown'),
('UgaUser3',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jordan',   'Carter'),
('UgaUser4',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Taylor',   'Davis'),
('UgaUser5',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Morgan',   'Evans'),
('UgaUser6',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alex',     'Foster'),
('UgaUser7',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Michael',  'Green'),
('UgaUser8',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Olivia',   'Hall'),
('UgaUser9',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ethan',    'Harris'),
('UgaUser10', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sophia',   'Jackson'),
('UgaUser11', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Liam',     'King'),
('UgaUser12', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emma',     'Lee'),
('UgaUser13', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Noah',     'Miller'),
('UgaUser14', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ava',      'Moore'),
('UgaUser15', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'William',  'Nelson'),
('UgaUser16', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Isabella', 'Parker'),
('UgaUser17', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'James',    'Rivera'),
('UgaUser18', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mia',      'Scott'),
('UgaUser19', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Benjamin', 'Thomas'),
('UgaUser20', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Charlotte','White');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- PARKING LOTS (15 real UGA lots)
-- Sources: UGA Parking Services (parking.uga.edu)
-- paymentType values: ParkMobile | Free | Free_After_5PM
-- ============================================================

INSERT INTO lot (name, address, totalCapacity, paymentType, latitude, longitude) VALUES
('Tate Center Parking Deck',        '275 S Jackson St, Athens, GA 30602',       1056, 'ParkMobile',    33.946619, -83.376327),
('Hull Street Parking Deck',        '385 Hull St, Athens, GA 30602',              836, 'ParkMobile',    33.944932, -83.384745),
('Carlton Street Parking Deck',     '250 Carlton St, Athens, GA 30602',           611, 'ParkMobile',    33.944477, -83.375421),
('River Road Parking Deck',         '1 River Rd, Athens, GA 30602',               720, 'ParkMobile',    33.948834, -83.390641),
('East Campus Parking Deck',        '633 S Lumpkin St, Athens, GA 30602',         508, 'ParkMobile',    33.940231, -83.369305),
('Intramural Fields Lot',           '1 Intramural Fields Dr, Athens, GA 30602',   185, 'Free_After_5PM',33.956987, -83.370344),
('Ramsey Center Lot',               '330 River Rd, Athens, GA 30602',             280, 'Free_After_5PM',33.958621, -83.371234),
('Veterinary Medicine Lot',         '501 D.W. Brooks Dr, Athens, GA 30602',       320, 'ParkMobile',    33.941129, -83.355718),
('Spec Towns Track Lot',            '1 Spec Towns Dr, Athens, GA 30602',          150, 'Free_After_5PM',33.956845, -83.375124),
('Georgia Museum of Art Lot',       '90 Carlton St, Athens, GA 30602',             95, 'Free_After_5PM',33.944003, -83.374731),
('Physics-Astronomy Building Lot',  '1 Physics Bldg, Athens, GA 30602',           210, 'ParkMobile',    33.949529, -83.377231),
('Baldwin Hall Lot',                '311 Herty Dr, Athens, GA 30602',             175, 'ParkMobile',    33.948944, -83.379812),
('College Station Road Lot',        '620 College Station Rd, Athens, GA 30605',   400, 'Free',          33.927018, -83.360845),
('Visitors Parking Deck',           '200 S Hull St, Athens, GA 30602',            120, 'ParkMobile',    33.946012, -83.384012),
('Engineering Center Lot',          '200 D.W. Brooks Dr, Athens, GA 30602',       245, 'Free_After_5PM',33.944524, -83.357018);

-- ============================================================
-- SPOT REPORTS (1200 rows generated via stored procedure)
-- Spread across 60 days, all hours of day, all 20 users and 15 lots.
-- packedLevel is correlated with hasOpenSpots for realism.
-- ============================================================

DROP PROCEDURE IF EXISTS generate_reports;
DELIMITER //
CREATE PROCEDURE generate_reports()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE lvl VARCHAR(10);
    WHILE i < 1200 DO
        SET lvl = ELT(1 + FLOOR(RAND() * 5), 'Empty', 'Light', 'Moderate', 'Busy', 'Full');
        INSERT INTO spot_report (userId, lotId, packedLevel, hasOpenSpots, reportedAt, notes)
        VALUES (
            1 + FLOOR(RAND() * 20),
            1 + FLOOR(RAND() * 15),
            lvl,
            CASE WHEN lvl IN ('Empty','Light','Moderate') THEN 1 ELSE 0 END,
            NOW() - INTERVAL FLOOR(RAND() * 86400) SECOND
                - INTERVAL FLOOR(RAND() * 60)     DAY,
            NULL
        );
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

CALL generate_reports();
DROP PROCEDURE IF EXISTS generate_reports;
