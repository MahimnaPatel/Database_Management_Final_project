UGA Parking Spot Tracker
========================
Group Name: UGA Parking Spot Tracker

TEAM MEMBERS & CONTRIBUTIONS
-----------------------------
Aidan Nash      — Auth & user management: login, register, profile, session handling
Cole Greer      — Favorites & dashboard: add/remove favorites, dashboard, aggregate queries
Mahimna Patel   — Parking lots: lots listing/detail pages, admin CRUD, lot seed data
Pavan Indukuri  — Spot reporting: report form, submission, report history, ER diagram & normalization
Mohammad Khan   — Analytics, indexes & docs: analytics page, perf.txt, ddl.sql, queries.sql,
                  security.txt, datasource.txt, readme.txt, 1200+ row data seed

TECHNOLOGIES USED
------------------
- Java 17
- Spring Boot 3.1.4
- Spring Boot Starter Web
- Spring Boot Starter Mustache (HTML templating)
- Spring Boot Starter JDBC
- Spring Security Crypto (BCrypt password hashing)
- MySQL 8 (Docker instance on port 33306)
- MySQL Connector/Java 8.0.33
- Maven (build tool)
- Font Awesome 5.15.3 (icon CDN, frontend only)
- HTML / CSS / JavaScript (frontend)

DATABASE CONNECTION
--------------------
Database name: uga_parking
Username:      root
Password:      mysqlpass
Port:          33306
JDBC URL:      jdbc:mysql://localhost:33306/uga_parking

TEST ACCOUNTS
--------------
Username: test       Password: password
Username: UgaUser1   Password: password
Username: UgaUser2   Password: password
(All 20 seeded demo accounts use password: password)

HOW TO RUN
-----------
1. Start the Docker MySQL instance (port 33306)
2. Run ddl.sql to create the database and tables
3. Run data.sql to seed all data (lots, users, 1200+ reports)
4. From the project root, run: mvn spring-boot:run
5. Open browser at: http://localhost:8080
6. Register a new account at /register or use test account to log in
