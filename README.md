# Cafeteria Management System

A desktop Cafeteria Management System built with **Java 17+, Swing, FlatLaf, Maven, MySQL, and JDBC**. It is designed as a friendly, visual staff dashboard that can be opened and run directly in IntelliJ IDEA.

## What is included

- Dashboard with today's sales, completed orders, menu count, and customer count.
- Menu management: add, edit, delete, search, set price, and mark an item available/unavailable.
- Customer directory: add, edit, and delete basic customer profiles.
- Point of Sale (new order): select products, set quantities, optionally attach a customer, and see subtotal/tax/total update automatically.
- Order history with saved order lines and a readable order-details view.
- Receipt generation as a text file in the `receipts/` folder after checkout.
- Sales overview showing total revenue and the completed-order ledger.
- JDBC persistence in MySQL. Tables are initialized automatically when the app starts.
- Seven starter menu items are inserted automatically only when the menu table is empty.
- JUnit tests for order arithmetic and tax rounding.

## Tech stack

| Technology | Use |
|---|---|
| Java 17+ | Application language |
| Swing | Desktop UI |
| FlatLaf 3.8 | Modern Swing look and feel |
| Maven | Build and dependency management |
| MySQL 8.0+ | Persistent data storage |
| MySQL Connector/J 26.10.0 | JDBC driver |
| JUnit Jupiter 6.1.3 | Unit tests |

Maven downloads the libraries declared in `pom.xml` the first time the project is built. The ZIP contains the complete source project and dependency configuration; Maven dependencies are not copied as binary JARs into the ZIP.

## Quick setup in IntelliJ IDEA

### 1. Install the prerequisites

Install:

1. **JDK 17 or newer** (JDK 21 is a good choice).
2. **IntelliJ IDEA** (the Community Edition is sufficient).
3. **MySQL Server 8.0 or newer**. MySQL Workbench is optional but makes running SQL scripts easier.

Make sure the MySQL service is running before starting the app.

### 2. Extract and open the project

1. Extract `Cafeteria-Management-System.zip` to a normal folder, for example `Documents/Cafeteria-Management-System`.
2. Open IntelliJ IDEA.
3. Choose **File → Open** and select the extracted project folder (the folder containing `pom.xml`).
4. When IntelliJ asks, trust/open the project and choose **Load Maven Project** if prompted.
5. Open **File → Project Structure → Project** and set the Project SDK to JDK 17 or newer.
6. Open the Maven tool window and click **Reload All Maven Projects**. Wait for the dependencies to download.

### 3. Create the MySQL database

Using MySQL Workbench:

1. Connect to your local MySQL Server.
2. Open `database/00_create_database.sql` and execute it. This creates the `cafeteria_db` database.
3. Optionally execute `database/01_schema.sql` as well. The Java application also creates its required tables automatically on first launch.

Using the MySQL command line, you can run:

```bash
mysql -u root -p < database/00_create_database.sql
mysql -u root -p cafeteria_db < database/01_schema.sql
```

### 4. Configure the database connection

1. Open the project's `config` folder.
2. Make a copy of `app.properties.example` and name the copy `app.properties`.
3. Open `config/app.properties` and enter the username/password you use for MySQL.

Example:

```properties
db.url=jdbc:mysql://localhost:3306/cafeteria_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=your_actual_mysql_password

cafeteria.name=Campus Cafe
currency.symbol=Rs.
tax.rate=0.05
```

Do not share or commit your real `app.properties` file. It is excluded by `.gitignore`. You can alternatively set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables; these override the file settings.

### 5. Run the application

1. In IntelliJ, open `src/main/java/com/cafeteria/App.java`.
2. Click the green run icon beside `main`, then choose **Run 'App.main()'**.
3. On the first successful run, the app creates its tables and inserts the starter menu if the menu is empty.
4. The **Dashboard** should open. Use the left navigation to move between New Order, Menu, Customers, Orders, and Reports.

### 6. Try the main workflow

1. Open **Menu** and add or update a food/drink item. Toggle availability to hide an item from the order screen without deleting it.
2. Open **Customers** and optionally create a customer profile.
3. Open **New Order**, choose one or more available items, set quantities, and optionally choose a customer.
4. Check the subtotal, configured tax, and total, then select **Complete order & create receipt**.
5. The order is saved in MySQL, and a receipt text file is created in the project's `receipts/` folder.
6. Open **Orders** to view the saved order and its line items. Open **Reports** for the sales ledger.

## Build, test, and package

Run these commands from the project root (the folder containing `pom.xml`):

```bash
# Run the unit tests
mvn test

# Build the executable, dependency-inclusive JAR
mvn clean package

# Run the packaged JAR (MySQL must still be configured and running)
java -jar target/cafeteria-management-system-1.0.0.jar
```

You can also run the application from IntelliJ without installing Maven separately; IntelliJ's bundled Maven integration can resolve the `pom.xml` dependencies.

## Project structure

```text
cafeteria-management-system/
├── config/
│   └── app.properties.example
├── database/
│   ├── 00_create_database.sql
│   └── 01_schema.sql
├── docs/
│   ├── INTELLIJ_SETUP.md
│   └── TEST_CASES.md
├── src/
│   ├── main/java/com/cafeteria/
│   │   ├── App.java
│   │   ├── config/       # application settings
│   │   ├── db/           # JDBC database and repository
│   │   ├── model/        # menu, customer, order and cart models
│   │   ├── service/      # order calculation
│   │   ├── ui/           # desktop screens and UI styling
│   │   └── util/         # currency formatting and receipts
│   └── test/java/        # JUnit tests
├── pom.xml
└── README.md
```

## Configuration notes

- The default currency is **PKR / Rs.**; change `currency.symbol` in `config/app.properties` if required.
- The default tax rate is **5%**. Change `tax.rate=0.05` to the value required by your demonstration or institution. Set `0` for no tax.
- Customer details are optional at checkout.
- The program stores order item names and prices as a snapshot, so previous order details remain readable if a menu item is later edited or deleted.

## Troubleshooting

**"Access denied for user"** — Check `db.username` and `db.password` in `config/app.properties`.

**"Unknown database 'cafeteria_db'"** — Run `database/00_create_database.sql` in MySQL Workbench or the MySQL command line.

**"Communications link failure" / connection refused** — Start the MySQL Server service and check the host/port in `db.url` (default is `localhost:3306`).

**Maven dependencies are not resolving** — Check your internet connection, then use Maven → Reload All Projects in IntelliJ. The first dependency download requires internet access.

**The app opens but the menu is empty** — The sample items are added only when `menu_items` contains zero rows. Add items through Menu management, or clear the menu table only if you intend to reset your demo data.

## Scope and limitations

This version implements the core functions stated in the proposal. It is a local desktop application intended for cafeteria staff and project demonstration. It does not include user login/roles, online ordering, payment-gateway integration, ingredient-level stock deduction, cloud hosting, or multi-terminal synchronization. These were not specified as required features in the proposal and can be treated as future enhancements.
