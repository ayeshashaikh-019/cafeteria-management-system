# IntelliJ IDEA setup guide

1. Install JDK 17+ and MySQL Server 8.0+.
2. Extract the ZIP and open the folder containing `pom.xml` in IntelliJ IDEA.
3. Set the project SDK to JDK 17+ under **File → Project Structure → Project**.
4. Load/reload the Maven project. Wait until all external dependencies finish downloading.
5. Run `database/00_create_database.sql` in MySQL to create `cafeteria_db`; optionally run `database/01_schema.sql` to create the tables before the first launch.
6. Copy `config/app.properties.example` to `config/app.properties` and set your MySQL credentials.
7. Open `src/main/java/com/cafeteria/App.java` and click the green Run icon next to `main`.
8. Use the left navigation to manage the menu, customers, orders, receipts, and reports.

The first application launch automatically initializes tables and inserts sample menu items when the menu is empty.
