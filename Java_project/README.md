# Student Management System

Java Swing university final project using OOP, JDBC, SQL Server, DAO, service, utility, and GUI layers.

## Run Steps

1. Open SQL Server Management Studio.
2. Run `01_student_management_database.sql`.
3. If needed, edit database credentials in `config/DatabaseConnection.java`, or set:
   - `SMS_DB_URL`
   - `SMS_DB_USERNAME`
   - `SMS_DB_PASSWORD`
4. Open this folder in VS Code.
5. Run `main/Main.java` or use the launch config named `Run Student Management System`.

Default SQL Server connection user in Java:

- Username: `yumiena`
- Password: empty by default

If your SQL Server account `yumiena` has a password, update `DEFAULT_PASSWORD` in
`config/DatabaseConnection.java` or set the `SMS_DB_PASSWORD` environment variable.

Default login:

- Username: `admin`
- Password: `admin123`

The SQL Server JDBC driver is included in `lib/mssql-jdbc-12.8.1.jre11.jar` and referenced by VS Code settings.
