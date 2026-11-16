package com.cafeteria.db;

import com.cafeteria.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Simple JDBC connection factory and schema initializer. */
public final class Database {
    private final AppConfig config;

    public Database(AppConfig config) { this.config = config; }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.dbUrl(), config.dbUsername(), config.dbPassword());
    }

    public void initialize() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS menu_items (
                    id VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    category VARCHAR(60) NOT NULL,
                    description VARCHAR(500) DEFAULT '',
                    price DECIMAL(10,2) NOT NULL,
                    available BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS customers (
                    id VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    phone VARCHAR(40) DEFAULT '',
                    email VARCHAR(160) DEFAULT '',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS orders (
                    id VARCHAR(36) PRIMARY KEY,
                    customer_id VARCHAR(36) NULL,
                    customer_name VARCHAR(120) NOT NULL DEFAULT 'Walk-in Customer',
                    subtotal DECIMAL(10,2) NOT NULL,
                    tax DECIMAL(10,2) NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id VARCHAR(36) PRIMARY KEY,
                    order_id VARCHAR(36) NOT NULL,
                    item_id VARCHAR(36) NULL,
                    item_name VARCHAR(120) NOT NULL,
                    quantity INT NOT NULL,
                    unit_price DECIMAL(10,2) NOT NULL,
                    line_total DECIMAL(10,2) NOT NULL,
                    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
                        REFERENCES orders(id) ON DELETE CASCADE
                )
                """);
        }
    }
}
