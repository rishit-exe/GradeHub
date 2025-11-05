<#
.SYNOPSIS
  Bootstrap the faculty database for GradeHub.

.DESCRIPTION
  This PowerShell script helps create the faculty database, create or grant a faculty DB user,
  and import the SQL schema. It's intended to be run from the repository root. The script
  will try to locate a mysql client on PATH or in common Program Files locations so you don't
  need to type the full path.

  The script intentionally runs mysql commands as external processes and will prompt for
  the MySQL root/user password when required.

.EXAMPLES
  # Create DB + new user + import schema (prompts for root password and faculty user password)
  .\scripts\setup-faculty-db.ps1 -CreateDb -CreateUser -DbName cgpa_faculty_db -UserName cgpa_faculty_user -Password "S3cure!Pass" -SchemaPath .\sql\schema.sql

  # Create DB only (prompts for root password)
  .\scripts\setup-faculty-db.ps1 -CreateDb -DbName cgpa_faculty_db

  # Grant privileges to an existing user (useful if you reuse cgpa_user)
  .\scripts\setup-faculty-db.ps1 -CreateDb -GrantToExistingUser -ExistingUser cgpa_user

  # Import schema using existing user (prompts for that user's password)
  .\scripts\setup-faculty-db.ps1 -ImportSchema -DbName cgpa_faculty_db -UserName cgpa_faculty_user -SchemaPath .\sql\schema.sql

# Parameters
param(
    [switch] $CreateDb,
    [switch] $CreateUser,
    [switch] $GrantToExistingUser,
    [switch] $ImportSchema,
    [string] $DbName = "cgpa_faculty_db",
    [string] $UserName = "cgpa_faculty_user",
    [string] $Password = "",
    [string] $ExistingUser = "",
    [string] $SchemaPath = ".\sql\schema.sql"
)

function Find-MySqlExe {
    # Try PATH first
    $cmd = Get-Command mysql -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Path }

    # Common Program Files locations
    $candidates = @(
        "C:\Program Files\MySQL",
        "C:\Program Files (x86)\MySQL",
        "C:\Program Files\MariaDB",
        "C:\Program Files (x86)\MariaDB"
    )
    foreach ($base in $candidates) {
        if (Test-Path $base) {
            Get-ChildItem -Path $base -Directory -ErrorAction SilentlyContinue | ForEach-Object {
                $exe = Join-Path $_.FullName 'bin\mysql.exe'
                if (Test-Path $exe) { return $exe }
            }
        }
    }
    return $null
}

function Run-MySqlCommandAsRoot {
    param($mysqlExe, $sql)
    Write-Host "Running as root: $sql"
    & $mysqlExe -u root -p -e $sql
    if ($LASTEXITCODE -ne 0) { throw "mysql exited with code $LASTEXITCODE" }
}

function Import-SchemaWithUser {
    param($mysqlExe, $db, $user, $schemaPath)
    if (-not (Test-Path $schemaPath)) { throw "Schema file not found: $schemaPath" }
    Write-Host "Importing schema into $db as $user (you will be prompted for the user's password)..."
    # Use cmd.exe redirection so '<' works reliably from PowerShell
    $exeQuoted = "`"$mysqlExe`""
    $schemaFull = (Resolve-Path $schemaPath).Path
    $cmd = "`"$mysqlExe`" -u $user -p $db < `"$schemaFull`""
    cmd /c $cmd
    if ($LASTEXITCODE -ne 0) { throw "Import failed with exit code $LASTEXITCODE" }
}

try {
    $mysqlExe = Find-MySqlExe
    if (-not $mysqlExe) {
        Write-Error "mysql client not found. Please install MySQL or add mysql.exe to your PATH."
        exit 2
    }
    Write-Host "Using mysql client: $mysqlExe"

    if ($CreateDb) {
        $sql = "CREATE DATABASE IF NOT EXISTS `$DbName`;"
        Run-MySqlCommandAsRoot -mysqlExe $mysqlExe -sql $sql
        Write-Host "Database '$DbName' ensured."
    }

    if ($CreateUser) {
        if (-not $Password) {
            Write-Host "No -Password provided. You will be prompted interactively for the new user's password in the MySQL client." -ForegroundColor Yellow
        }
        $sql = "CREATE USER IF NOT EXISTS '$UserName'@'localhost' IDENTIFIED BY '$Password'; GRANT ALL PRIVILEGES ON `$DbName`.* TO '$UserName'@'localhost'; FLUSH PRIVILEGES;"
        Run-MySqlCommandAsRoot -mysqlExe $mysqlExe -sql $sql
        Write-Host "User '$UserName' created/granted on '$DbName'."
    }

    if ($GrantToExistingUser) {
        if (-not $ExistingUser) { throw "Provide -ExistingUser when using -GrantToExistingUser" }
        $sql = "GRANT ALL PRIVILEGES ON `$DbName`.* TO '$ExistingUser'@'localhost'; FLUSH PRIVILEGES;"
        Run-MySqlCommandAsRoot -mysqlExe $mysqlExe -sql $sql
        Write-Host "Granted privileges to $ExistingUser on $DbName"
    }

    if ($ImportSchema) {
        Import-SchemaWithUser -mysqlExe $mysqlExe -db $DbName -user $UserName -schemaPath $SchemaPath
        Write-Host "Schema imported successfully."
    }

    Write-Host "Done. Update src/main/resources/db.faculty.properties to point to the faculty DB and user."
} catch {
    Write-Error "Setup failed: $_"
    exit 1
}

# End of script
