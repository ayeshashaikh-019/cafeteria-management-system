package com.cafeteria.db;

import com.cafeteria.model.*;
import com.cafeteria.service.OrderCalculator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** All persistence operations are kept in one small JDBC repository. */
public class CafeteriaRepository {
    private final Database database;

    public CafeteriaRepository(Database database) { this.database = database; }

    public void seedMenuIfEmpty() throws SQLException {
        try (Connection c = database.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM menu_items")) {
            rs.next();
            if (rs.getLong(1) > 0) return;
        }
        List<MenuItem> samples = List.of(
                sample("Classic Zinger Burger", "Fast Food", "Crispy chicken, lettuce and house sauce", "650.00"),
                sample("Club Sandwich", "Snacks", "Triple-layer grilled sandwich", "480.00"),
                sample("Chicken Biryani", "Meals", "Aromatic rice with chicken", "420.00"),
                sample("French Fries", "Sides", "Golden and crispy", "220.00"),
                sample("Fresh Lime", "Drinks", "Fresh lime with a cool twist", "160.00"),
                sample("Cold Coffee", "Drinks", "Chilled coffee with milk", "290.00"),
                sample("Chocolate Brownie", "Desserts", "Warm chocolate brownie", "250.00")
        );
        for (MenuItem item : samples) addMenuItem(item);
    }

    private MenuItem sample(String name, String category, String description, String price) {
        return new MenuItem(UUID.randomUUID().toString(), name, category, description, new BigDecimal(price), true);
    }

    public List<MenuItem> getMenuItems() throws SQLException {
        List<MenuItem> rows = new ArrayList<>();
        String sql = "SELECT id,name,category,description,price,available FROM menu_items ORDER BY category,name";
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) rows.add(new MenuItem(r.getString("id"), r.getString("name"), r.getString("category"),
                    r.getString("description"), r.getBigDecimal("price"), r.getBoolean("available")));
        }
        return rows;
    }

    public void addMenuItem(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu_items(id,name,category,description,price,available) VALUES(?,?,?,?,?,?)";
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, item.id()); p.setString(2, item.name()); p.setString(3, item.category());
            p.setString(4, item.description()); p.setBigDecimal(5, item.price()); p.setBoolean(6, item.available());
            p.executeUpdate();
        }
    }

    public void updateMenuItem(MenuItem item) throws SQLException {
        String sql = "UPDATE menu_items SET name=?,category=?,description=?,price=?,available=? WHERE id=?";
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, item.name()); p.setString(2, item.category()); p.setString(3, item.description());
            p.setBigDecimal(4, item.price()); p.setBoolean(5, item.available()); p.setString(6, item.id());
            p.executeUpdate();
        }
    }

    public void deleteMenuItem(String id) throws SQLException {
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM menu_items WHERE id=?")) {
            p.setString(1, id); p.executeUpdate();
        }
    }

    public List<Customer> getCustomers() throws SQLException {
        List<Customer> rows = new ArrayList<>();
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement("SELECT id,name,phone,email FROM customers ORDER BY name"); ResultSet r = p.executeQuery()) {
            while (r.next()) rows.add(new Customer(r.getString("id"), r.getString("name"), r.getString("phone"), r.getString("email")));
        }
        return rows;
    }

    public void saveCustomer(Customer customer) throws SQLException {
        boolean exists;
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement("SELECT 1 FROM customers WHERE id=?")) {
            p.setString(1, customer.id());
            try (ResultSet r = p.executeQuery()) { exists = r.next(); }
        }
        String sql = exists
                ? "UPDATE customers SET name=?,phone=?,email=? WHERE id=?"
                : "INSERT INTO customers(name,phone,email,id) VALUES(?,?,?,?)";
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, customer.name()); p.setString(2, customer.phone()); p.setString(3, customer.email()); p.setString(4, customer.id());
            p.executeUpdate();
        }
    }

    public void deleteCustomer(String id) throws SQLException {
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM customers WHERE id=?")) {
            p.setString(1, id); p.executeUpdate();
        }
    }

    public OrderRecord createOrder(String customerId, String customerName, List<CartLine> lines,
                                   BigDecimal taxRate) throws SQLException {
        OrderCalculator.Totals totals = OrderCalculator.calculate(lines, taxRate);
        String orderId = UUID.randomUUID().toString();
        String sqlOrder = "INSERT INTO orders(id,customer_id,customer_name,subtotal,tax,total) VALUES(?,?,?,?,?,?)";
        String sqlLine = "INSERT INTO order_items(id,order_id,item_id,item_name,quantity,unit_price,line_total) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement(sqlOrder)) {
                    p.setString(1, orderId); p.setString(2, blankToNull(customerId));
                    p.setString(3, customerName == null || customerName.isBlank() ? "Walk-in Customer" : customerName);
                    p.setBigDecimal(4, totals.subtotal()); p.setBigDecimal(5, totals.tax()); p.setBigDecimal(6, totals.total());
                    p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement(sqlLine)) {
                    for (CartLine line : lines) {
                        p.setString(1, UUID.randomUUID().toString()); p.setString(2, orderId);
                        p.setString(3, line.item().id()); p.setString(4, line.item().name());
                        p.setInt(5, line.quantity()); p.setBigDecimal(6, line.item().price()); p.setBigDecimal(7, line.lineTotal());
                        p.addBatch();
                    }
                    p.executeBatch();
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
        return getOrder(orderId);
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value; }

    public List<OrderRecord> getOrders() throws SQLException {
        List<OrderRecord> rows = new ArrayList<>();
        String sql = "SELECT id,customer_name,subtotal,tax,total,created_at FROM orders ORDER BY created_at DESC";
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) rows.add(mapOrder(r));
        }
        return rows;
    }

    public OrderRecord getOrder(String id) throws SQLException {
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT id,customer_name,subtotal,tax,total,created_at FROM orders WHERE id=?")) {
            p.setString(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? mapOrder(r) : null; }
        }
    }

    public List<OrderLine> getOrderLines(String orderId) throws SQLException {
        List<OrderLine> rows = new ArrayList<>();
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT item_name,quantity,unit_price,line_total FROM order_items WHERE order_id=? ORDER BY item_name")) {
            p.setString(1, orderId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) rows.add(new OrderLine(r.getString("item_name"), r.getInt("quantity"),
                        r.getBigDecimal("unit_price"), r.getBigDecimal("line_total")));
            }
        }
        return rows;
    }

    public DashboardStats getDashboardStats() throws SQLException {
        long orderCount;
        BigDecimal sales;
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) AS orders_today, COALESCE(SUM(total),0) AS sales_today FROM orders WHERE DATE(created_at)=CURRENT_DATE"); ResultSet r = p.executeQuery()) {
            r.next(); orderCount = r.getLong("orders_today"); sales = r.getBigDecimal("sales_today");
        }
        long menuCount;
        long unavailable;
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) AS item_count, COALESCE(SUM(CASE WHEN available=FALSE THEN 1 ELSE 0 END),0) AS unavailable FROM menu_items"); ResultSet r = p.executeQuery()) {
            r.next(); menuCount = r.getLong("item_count"); unavailable = r.getLong("unavailable");
        }
        long customers;
        try (Connection c = database.getConnection(); PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM customers"); ResultSet r = p.executeQuery()) {
            r.next(); customers = r.getLong(1);
        }
        return new DashboardStats(orderCount, sales == null ? BigDecimal.ZERO : sales, menuCount, unavailable, customers);
    }

    private OrderRecord mapOrder(ResultSet r) throws SQLException {
        Timestamp timestamp = r.getTimestamp("created_at");
        return new OrderRecord(r.getString("id"), r.getString("customer_name"), r.getBigDecimal("subtotal"),
                r.getBigDecimal("tax"), r.getBigDecimal("total"), timestamp == null ? "" : timestamp.toLocalDateTime().toString().replace('T', ' '));
    }
}
