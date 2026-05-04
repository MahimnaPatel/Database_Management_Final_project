UGA Parking Spot Tracker
========================
Group Name: [YOUR GROUP NAME]

TEAM MEMBERS
-------------
Aidan Nash 
Cole Greer
Mahimna Patel
Pavan Indukuri
Mohammad Khan

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
Register new accounts at /register after seeding the database.
The app uses BCrypt (Spring Security BCryptPasswordEncoder, strength 10).

HOW TO RUN
-----------
1. Start the Docker MySQL instance (port 33306)
2. Run ddl.sql to create the database and tables
3. Run data.sql to seed all data (lots, users, 1200+ reports)
4. From the project root, run: mvn spring-boot:run
5. Open browser at: http://localhost:8080
6. Register a new account at /register to log in
