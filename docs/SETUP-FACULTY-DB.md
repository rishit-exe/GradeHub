# Setup Faculty Database (GradeHub)

This document explains how to create a separate database for the Faculty Portal and import the schema. A small PowerShell helper script is included at `scripts/setup-faculty-db.ps1` so you can run everything from the repository root without typing the full mysql path.

Prerequisites
- MySQL server installed on the machine where you run the script.
- Access to a MySQL administrative account (for example `root`) to create the database and grant privileges.
- The repository's SQL schema file exists at `sql/schema.sql` (this repo includes that file).

Quick start (recommended)

1. From the repo root open PowerShell (run as administrator if needed).

2. Run the script to create the DB, user, and import the schema (replace passwords):

```powershell
.\scripts\setup-faculty-db.ps1 -CreateDb -CreateUser -DbName cgpa_faculty_db -UserName cgpa_faculty_user -Password "ChangeThisNow!" -ImportSchema -SchemaPath .\sql\schema.sql
```

3. Update `src/main/resources/db.faculty.properties` with the faculty DB connection details, for example:

```
db.url=jdbc:mysql://localhost:3306/cgpa_faculty_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.user=cgpa_faculty_user
db.password=ChangeThisNow!
```

Manual steps (if you prefer to run commands manually)

Create database and user (run as root):

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p -e "CREATE DATABASE IF NOT EXISTS cgpa_faculty_db;"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p -e "CREATE USER IF NOT EXISTS 'cgpa_faculty_user'@'localhost' IDENTIFIED BY 'YourPassword'; GRANT ALL PRIVILEGES ON cgpa_faculty_db.* TO 'cgpa_faculty_user'@'localhost'; FLUSH PRIVILEGES;"
```

Import schema:

```powershell
# recommended: log in then run SOURCE inside the client
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u cgpa_faculty_user -p cgpa_faculty_db
# at the mysql> prompt:
# SOURCE C:/path/to/GradeHub/sql/schema.sql;
```

OR, to run in one line from PowerShell (uses cmd.exe for redirection):

```powershell
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u cgpa_faculty_user -p cgpa_faculty_db < "C:\Path\To\GradeHub\sql\schema.sql"'
```

Security notes
- Avoid running your app connecting as `root`. Create a non-root user (`cgpa_faculty_user`) and grant it only the privileges it needs.
- Keep `db.faculty.properties` out of public repositories if it contains secrets. The repo includes `db.faculty.properties.example` — copy and edit locally instead.

Troubleshooting
- If the script cannot find `mysql.exe`, add your MySQL `bin` folder to PATH or specify the full path in the manual commands above.
- If you get `Access denied` when the app connects, check `db.faculty.properties` credentials and that the specified user has privileges for the faculty DB.
