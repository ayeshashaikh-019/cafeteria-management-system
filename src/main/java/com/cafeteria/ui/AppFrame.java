package com.cafeteria.ui;

import com.cafeteria.config.AppConfig;
import com.cafeteria.db.CafeteriaRepository;
import com.cafeteria.model.*;
import com.cafeteria.model.MenuItem;
import com.cafeteria.service.OrderCalculator;
import com.cafeteria.util.Formatters;
import com.cafeteria.util.ReceiptWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class AppFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final AppConfig config;
    private final CafeteriaRepository repository;
    private final JPanel content = new JPanel(new BorderLayout());
    private final JLabel pageTitle = new JLabel("Overview");
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String currentPage = "Dashboard";

    public AppFrame(AppConfig config, CafeteriaRepository repository) {
        this.config = config;
        this.repository = repository;
        setTitle(config.cafeteriaName() + " | Cafeteria Management System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1360, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildWorkspace(), BorderLayout.CENTER);
        navigate("Dashboard");
    }

    private JPanel buildWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(Ui.BG);
        workspace.add(buildTopBar(), BorderLayout.NORTH);
        content.setBackground(Ui.BG);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(Ui.BG);
        workspace.add(scroll, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Ui.NAVY);
        sidebar.setPreferredSize(new Dimension(238, 0));
        sidebar.setBorder(new EmptyBorder(22, 16, 18, 16));

        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);
        JLabel mark = new JLabel("✦");
        mark.setHorizontalAlignment(SwingConstants.CENTER);
        mark.setFont(new Font("SansSerif", Font.BOLD, 22));
        mark.setForeground(Color.WHITE);
        mark.setOpaque(true);
        mark.setBackground(Ui.TEAL);
        mark.setPreferredSize(new Dimension(42, 42));
        JLabel name = new JLabel(config.cafeteriaName());
        name.setFont(new Font("SansSerif", Font.BOLD, 17));
        name.setForeground(Color.WHITE);
        JLabel sub = new JLabel("MANAGEMENT SUITE");
        sub.setFont(new Font("SansSerif", Font.BOLD, 9));
        sub.setForeground(new Color(157, 174, 194));
        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.add(name); brandText.add(Box.createVerticalStrut(3)); brandText.add(sub);
        brand.add(mark, BorderLayout.WEST); brand.add(brandText, BorderLayout.CENTER);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(34));
        sidebar.add(sectionLabel("WORKSPACE"));
        sidebar.add(Box.createVerticalStrut(10));

        addNav(sidebar, "Dashboard", "◫");
        addNav(sidebar, "New Order", "＋");
        addNav(sidebar, "Menu", "☷");
        addNav(sidebar, "Customers", "♙");
        addNav(sidebar, "Orders", "▤");
        addNav(sidebar, "Reports", "▥");

        sidebar.add(Box.createVerticalGlue());
        JPanel tip = new JPanel();
        tip.setLayout(new BoxLayout(tip, BoxLayout.Y_AXIS));
        tip.setBackground(Ui.NAVY_LIGHT);
        tip.setBorder(new EmptyBorder(14, 14, 14, 14));
        JLabel tipTitle = new JLabel("A smoother service day");
        tipTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        tipTitle.setForeground(Color.WHITE);
        JLabel tipText = new JLabel("Keep orders moving and\nyour menu up to date.");
        tipText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tipText.setForeground(new Color(189, 201, 216));
        tip.add(tipTitle); tip.add(Box.createVerticalStrut(7)); tip.add(tipText);
        tip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        sidebar.add(tip);
        sidebar.add(Box.createVerticalStrut(14));
        JLabel version = new JLabel("CAFETERIA SYSTEM  •  v1.0");
        version.setFont(new Font("SansSerif", Font.PLAIN, 9));
        version.setForeground(new Color(130, 148, 170));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(version);
        return sidebar;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(new Color(126, 146, 169));
        label.setBorder(new EmptyBorder(0, 10, 0, 0));
        return label;
    }

    private void addNav(JPanel sidebar, String label, String icon) {
        JButton button = new JButton("  " + icon + "     " + label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(new Color(194, 207, 222));
        button.setBackground(Ui.NAVY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 12, 12, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.addActionListener(e -> navigate(label));
        navButtons.put(label, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Ui.LINE), new EmptyBorder(17, 28, 17, 28)));
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        pageTitle.setForeground(Ui.TEXT);
        JLabel context = new JLabel("Manage your cafeteria with confidence");
        context.setFont(new Font("SansSerif", Font.PLAIN, 11));
        context.setForeground(Ui.MUTED);
        labels.add(pageTitle); labels.add(Box.createVerticalStrut(3)); labels.add(context);
        top.add(labels, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel statusDot = new JLabel("●  SYSTEM READY");
        statusDot.setFont(new Font("SansSerif", Font.BOLD, 10));
        statusDot.setForeground(Ui.GREEN);
        JLabel avatar = new JLabel("CC");
        avatar.setOpaque(true);
        avatar.setBackground(new Color(230, 244, 241));
        avatar.setForeground(Ui.TEAL_DARK);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(34, 34));
        right.add(statusDot); right.add(avatar);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private void navigate(String page) {
        currentPage = page;
        pageTitle.setText(page.equals("New Order") ? "Create a new order" : page);
        navButtons.forEach((key, button) -> {
            boolean active = key.equals(page);
            button.setBackground(active ? Ui.NAVY_LIGHT : Ui.NAVY);
            button.setForeground(active ? Color.WHITE : new Color(194, 207, 222));
        });
        content.removeAll();
        JPanel view = switch (page) {
            case "New Order" -> buildPosPage();
            case "Menu" -> buildMenuPage();
            case "Customers" -> buildCustomersPage();
            case "Orders" -> buildOrdersPage();
            case "Reports" -> buildReportsPage();
            default -> buildDashboardPage();
        };
        content.add(view, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JPanel buildDashboardPage() {
        JPanel page = Ui.page();
        page.add(intro("Welcome back", "Here is what is happening in your cafeteria today."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        try {
            DashboardStats stats = repository.getDashboardStats();
            JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
            cards.setOpaque(false);
            cards.add(statCard("TODAY'S SALES", Formatters.money(stats.salesToday(), config.currencySymbol()), "Revenue recorded today", "↗", Ui.TEAL));
            cards.add(statCard("ORDERS TODAY", String.valueOf(stats.ordersToday()), "Completed orders", "▤", new Color(82, 126, 194)));
            cards.add(statCard("MENU ITEMS", String.valueOf(stats.menuItems()), stats.unavailableItems() + " unavailable", "☷", Ui.ORANGE));
            cards.add(statCard("CUSTOMERS", String.valueOf(stats.customers()), "Saved customer profiles", "♙", new Color(143, 112, 184)));
            body.add(cards, BorderLayout.NORTH);
        } catch (Exception ex) {
            body.add(errorCard("Dashboard data is unavailable", ex.getMessage()), BorderLayout.NORTH);
        }

        JPanel recent = Ui.card();
        recent.setLayout(new BorderLayout(0, 14));
        JPanel recentHeader = new JPanel(new BorderLayout()); recentHeader.setOpaque(false);
        JLabel recentTitle = new JLabel("Recent orders"); recentTitle.setFont(new Font("SansSerif", Font.BOLD, 16)); recentTitle.setForeground(Ui.TEXT);
        JButton viewAll = Ui.secondaryButton("View all orders  →"); viewAll.addActionListener(e -> navigate("Orders"));
        recentHeader.add(recentTitle, BorderLayout.WEST); recentHeader.add(viewAll, BorderLayout.EAST);
        recent.add(recentHeader, BorderLayout.NORTH);
        try {
            List<OrderRecord> orders = repository.getOrders();
            DefaultTableModel model = model("Order ID", "Customer", "Date", "Total", "Status");
            for (OrderRecord order : orders.stream().limit(6).toList()) {
                model.addRow(new Object[]{Formatters.shortId(order.id()), order.customerName(), order.createdAt(),
                        Formatters.money(order.total(), config.currencySymbol()), "Completed"});
            }
            JTable table = new JTable(model); Ui.styleTable(table);
            recent.add(Ui.scroll(table), BorderLayout.CENTER);
        } catch (Exception ex) { recent.add(new JLabel("Could not load recent orders: " + ex.getMessage()), BorderLayout.CENTER); }
        body.add(recent, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);

        JPanel quick = Ui.card();
        quick.setLayout(new BoxLayout(quick, BoxLayout.Y_AXIS));
        JLabel quickTitle = new JLabel("Quick actions"); quickTitle.setFont(new Font("SansSerif", Font.BOLD, 15)); quickTitle.setForeground(Ui.TEXT);
        quick.add(quickTitle); quick.add(Box.createVerticalStrut(12));
        JPanel quickButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); quickButtons.setOpaque(false);
        JButton newOrder = Ui.primaryButton("＋  Start a new order"); newOrder.addActionListener(e -> navigate("New Order"));
        JButton addItem = Ui.secondaryButton("＋  Add menu item"); addItem.addActionListener(e -> navigate("Menu"));
        JButton addCustomer = Ui.secondaryButton("＋  Add customer"); addCustomer.addActionListener(e -> navigate("Customers"));
        quickButtons.add(newOrder); quickButtons.add(addItem); quickButtons.add(addCustomer);
        quick.add(quickButtons);
        page.add(quick, BorderLayout.SOUTH);
        return page;
    }

    private JPanel intro(String title, String subtitle) {
        JPanel panel = new JPanel(); panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Ui.title(title)); panel.add(Box.createVerticalStrut(5)); panel.add(Ui.subtitle(subtitle));
        return panel;
    }

    private JPanel statCard(String heading, String value, String foot, String icon, Color accent) {
        JPanel card = Ui.card();
        card.setLayout(new BorderLayout(0, 10));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        JLabel caption = new JLabel(heading); caption.setFont(new Font("SansSerif", Font.BOLD, 10)); caption.setForeground(Ui.MUTED);
        JLabel symbol = new JLabel(icon); symbol.setOpaque(true); symbol.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
        symbol.setForeground(accent); symbol.setFont(new Font("SansSerif", Font.BOLD, 16)); symbol.setHorizontalAlignment(SwingConstants.CENTER);
        symbol.setPreferredSize(new Dimension(34, 34));
        top.add(caption, BorderLayout.WEST); top.add(symbol, BorderLayout.EAST);
        JLabel number = new JLabel(value); number.setFont(new Font("SansSerif", Font.BOLD, 23)); number.setForeground(Ui.TEXT);
        JLabel detail = new JLabel(foot); detail.setFont(new Font("SansSerif", Font.PLAIN, 11)); detail.setForeground(Ui.MUTED);
        card.add(top, BorderLayout.NORTH); card.add(number, BorderLayout.CENTER); card.add(detail, BorderLayout.SOUTH);
        return card;
    }

    private JPanel errorCard(String title, String message) {
        JPanel panel = Ui.card(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif", Font.BOLD, 15)); t.setForeground(Ui.RED);
        panel.add(t); panel.add(Box.createVerticalStrut(6)); panel.add(Ui.subtitle(message == null ? "Unknown database error" : message));
        return panel;
    }

    private JPanel buildMenuPage() {
        JPanel page = Ui.page();
        JPanel heading = new JPanel(new BorderLayout(12, 8)); heading.setOpaque(false);
        heading.add(intro("Menu management", "Add, update, price, and control the availability of your food and drinks."), BorderLayout.WEST);
        JButton add = Ui.primaryButton("＋  Add menu item");
        heading.add(add, BorderLayout.EAST);
        page.add(heading, BorderLayout.NORTH);

        JPanel card = Ui.card(); card.setLayout(new BorderLayout(0, 14));
        JPanel toolbar = new JPanel(new BorderLayout()); toolbar.setOpaque(false);
        JTextField search = Ui.textField("Search by item, category or description", 24);
        toolbar.add(search, BorderLayout.WEST);
        JLabel hint = Ui.subtitle("Select a row to edit or remove an item"); toolbar.add(hint, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);
        DefaultTableModel model = model("ID", "Item", "Category", "Description", "Price", "Availability");
        JTable table = new JTable(model); Ui.styleTable(table); hideFirstColumn(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() { String value = search.getText().trim(); sorter.setRowFilter(value.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(value))); }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        card.add(Ui.scroll(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0)); actions.setOpaque(false);
        JButton edit = Ui.secondaryButton("Edit selected"); JButton delete = Ui.dangerButton("Delete selected");
        actions.add(delete); actions.add(edit); card.add(actions, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);

        Runnable reload = () -> loadMenuRows(model);
        reload.run();
        add.addActionListener(e -> showMenuDialog(null, reload));
        edit.addActionListener(e -> {
            String id = selectedHiddenId(table, model);
            if (id == null) { info("Choose a menu item first."); return; }
            try { MenuItem item = repository.getMenuItems().stream().filter(x -> x.id().equals(id)).findFirst().orElse(null); if (item != null) showMenuDialog(item, reload); }
            catch (Exception ex) { Ui.showError(this, ex); }
        });
        delete.addActionListener(e -> {
            String id = selectedHiddenId(table, model);
            if (id == null) { info("Choose a menu item first."); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete this menu item? Previous order records will remain.", "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { repository.deleteMenuItem(id); reload.run(); } catch (Exception ex) { Ui.showError(this, ex); }
            }
        });
        return page;
    }

    private void loadMenuRows(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            for (MenuItem item : repository.getMenuItems()) model.addRow(new Object[]{item.id(), item.name(), item.category(), item.description(),
                    Formatters.money(item.price(), config.currencySymbol()), item.available() ? "Available" : "Unavailable"});
        } catch (Exception ex) { Ui.showError(this, ex); }
    }

    private void showMenuDialog(MenuItem existing, Runnable afterSave) {
        JTextField name = Ui.textField("e.g. Chicken burger", 20);
        JComboBox<String> category = new JComboBox<>(new String[]{"Fast Food", "Meals", "Snacks", "Sides", "Drinks", "Desserts", "Other"});
        JTextField description = Ui.textField("Short description", 20);
        JTextField price = Ui.textField("e.g. 450.00", 20);
        JCheckBox available = new JCheckBox("Available for ordering", true); available.setOpaque(false);
        if (existing != null) { name.setText(existing.name()); category.setSelectedItem(existing.category()); description.setText(existing.description()); price.setText(existing.price().toPlainString()); available.setSelected(existing.available()); }
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 7)); form.setBorder(new EmptyBorder(5, 5, 5, 5));
        form.add(labelled("Item name", name)); form.add(labelled("Category", category));
        form.add(labelled("Description", description)); form.add(labelled("Price (PKR)", price)); form.add(available);
        int result = JOptionPane.showConfirmDialog(this, form, existing == null ? "Add menu item" : "Edit menu item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            if (name.getText().isBlank()) throw new IllegalArgumentException("Item name is required.");
            BigDecimal amount = new BigDecimal(price.getText().trim());
            if (amount.signum() < 0) throw new IllegalArgumentException("Price cannot be negative.");
            MenuItem item = new MenuItem(existing == null ? UUID.randomUUID().toString() : existing.id(), name.getText().trim(),
                    String.valueOf(category.getSelectedItem()), description.getText().trim(), amount, available.isSelected());
            if (existing == null) repository.addMenuItem(item); else repository.updateMenuItem(item);
            afterSave.run();
        } catch (NumberFormatException ex) { info("Enter a valid price, such as 450.00."); }
        catch (Exception ex) { Ui.showError(this, ex); }
    }

    private JPanel buildCustomersPage() {
        JPanel page = Ui.page();
        JPanel heading = new JPanel(new BorderLayout()); heading.setOpaque(false);
        heading.add(intro("Customer directory", "Keep basic customer details together for faster checkout."), BorderLayout.WEST);
        JButton add = Ui.primaryButton("＋  Add customer"); heading.add(add, BorderLayout.EAST);
        page.add(heading, BorderLayout.NORTH);
        JPanel card = Ui.card(); card.setLayout(new BorderLayout(0, 14));
        JLabel hint = Ui.subtitle("Customer details are optional when creating an order."); card.add(hint, BorderLayout.NORTH);
        DefaultTableModel model = model("ID", "Name", "Phone", "Email");
        JTable table = new JTable(model); Ui.styleTable(table); hideFirstColumn(table);
        card.add(Ui.scroll(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0)); actions.setOpaque(false);
        JButton edit = Ui.secondaryButton("Edit selected"); JButton delete = Ui.dangerButton("Delete selected");
        actions.add(delete); actions.add(edit); card.add(actions, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);
        Runnable reload = () -> loadCustomerRows(model);
        reload.run();
        add.addActionListener(e -> showCustomerDialog(null, reload));
        edit.addActionListener(e -> {
            String id = selectedHiddenId(table, model); if (id == null) { info("Choose a customer first."); return; }
            try { Customer c = repository.getCustomers().stream().filter(x -> x.id().equals(id)).findFirst().orElse(null); if (c != null) showCustomerDialog(c, reload); }
            catch (Exception ex) { Ui.showError(this, ex); }
        });
        delete.addActionListener(e -> {
            String id = selectedHiddenId(table, model); if (id == null) { info("Choose a customer first."); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete this customer profile? Existing orders are kept.", "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { repository.deleteCustomer(id); reload.run(); } catch (Exception ex) { Ui.showError(this, ex); }
            }
        });
        return page;
    }

    private void loadCustomerRows(DefaultTableModel model) {
        model.setRowCount(0);
        try { for (Customer c : repository.getCustomers()) model.addRow(new Object[]{c.id(), c.name(), c.phone(), c.email()}); }
        catch (Exception ex) { Ui.showError(this, ex); }
    }

    private void showCustomerDialog(Customer existing, Runnable afterSave) {
        JTextField name = Ui.textField("Customer name", 20); JTextField phone = Ui.textField("Phone number", 20); JTextField email = Ui.textField("Email address", 20);
        if (existing != null) { name.setText(existing.name()); phone.setText(existing.phone()); email.setText(existing.email()); }
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 7)); form.setBorder(new EmptyBorder(5, 5, 5, 5));
        form.add(labelled("Full name", name)); form.add(labelled("Phone", phone)); form.add(labelled("Email", email));
        int result = JOptionPane.showConfirmDialog(this, form, existing == null ? "Add customer" : "Edit customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (name.getText().isBlank()) { info("Customer name is required."); return; }
        try {
            repository.saveCustomer(new Customer(existing == null ? UUID.randomUUID().toString() : existing.id(), name.getText().trim(), phone.getText().trim(), email.getText().trim()));
            afterSave.run();
        } catch (Exception ex) { Ui.showError(this, ex); }
    }

    private JPanel buildPosPage() {
        JPanel page = Ui.page();
        page.add(intro("New order", "Choose menu items, add quantities, and complete the bill."), BorderLayout.NORTH);
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0)); body.setOpaque(false);

        JPanel selection = Ui.card(); selection.setLayout(new BorderLayout(0, 14));
        JLabel chooseTitle = new JLabel("1. Select items"); chooseTitle.setFont(new Font("SansSerif", Font.BOLD, 16)); chooseTitle.setForeground(Ui.TEXT);
        JPanel selectTop = new JPanel(new BorderLayout()); selectTop.setOpaque(false); selectTop.add(chooseTitle, BorderLayout.WEST);
        selection.add(selectTop, BorderLayout.NORTH);
        DefaultComboBoxModel<MenuItem> itemModel = new DefaultComboBoxModel<>();
        JComboBox<MenuItem> itemCombo = new JComboBox<>(itemModel);
        itemCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof MenuItem item) setText(item.name() + "   ·   " + item.category() + "   ·   " + Formatters.money(item.price(), config.currencySymbol()));
                return this;
            }
        });
        try { for (MenuItem item : repository.getMenuItems()) if (item.available()) itemModel.addElement(item); }
        catch (Exception ex) { Ui.showError(this, ex); }
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JPanel addRow = new JPanel(new BorderLayout(8, 0)); addRow.setOpaque(false);
        JPanel qtyPanel = new JPanel(new BorderLayout(5, 0)); qtyPanel.setOpaque(false); qtyPanel.add(new JLabel("Qty"), BorderLayout.WEST); qtyPanel.add(quantity, BorderLayout.CENTER);
        JButton addToCart = Ui.primaryButton("Add to cart  →");
        addRow.add(qtyPanel, BorderLayout.WEST); addRow.add(addToCart, BorderLayout.EAST);
        JPanel picker = new JPanel(new BorderLayout(0, 10)); picker.setOpaque(false); picker.add(itemCombo, BorderLayout.CENTER); picker.add(addRow, BorderLayout.SOUTH);
        selection.add(picker, BorderLayout.CENTER);
        JLabel menuHint = Ui.subtitle("Only available menu items are shown here."); selection.add(menuHint, BorderLayout.SOUTH);

        JPanel cartPanel = Ui.card(); cartPanel.setLayout(new BorderLayout(0, 12));
        JPanel cartHeader = new JPanel(new BorderLayout()); cartHeader.setOpaque(false);
        JLabel cartTitle = new JLabel("2. Your order"); cartTitle.setFont(new Font("SansSerif", Font.BOLD, 16)); cartTitle.setForeground(Ui.TEXT);
        JButton remove = Ui.dangerButton("Remove selected"); cartHeader.add(cartTitle, BorderLayout.WEST); cartHeader.add(remove, BorderLayout.EAST);
        cartPanel.add(cartHeader, BorderLayout.NORTH);
        DefaultTableModel cartModel = model("ID", "Item", "Qty", "Amount");
        JTable cartTable = new JTable(cartModel); Ui.styleTable(cartTable); hideFirstColumn(cartTable);
        cartPanel.add(Ui.scroll(cartTable), BorderLayout.CENTER);
        JPanel checkout = new JPanel(); checkout.setOpaque(false); checkout.setLayout(new BoxLayout(checkout, BoxLayout.Y_AXIS));
        JComboBox<CustomerOption> customerCombo = new JComboBox<>();
        customerCombo.addItem(new CustomerOption(null, "Walk-in Customer"));
        try { for (Customer c : repository.getCustomers()) customerCombo.addItem(new CustomerOption(c, c.name() + (c.phone().isBlank() ? "" : " · " + c.phone()))); }
        catch (Exception ex) { Ui.showError(this, ex); }
        JPanel customerRow = new JPanel(new BorderLayout(8, 0)); customerRow.setOpaque(false);
        JLabel customerLabel = new JLabel("Customer"); customerLabel.setFont(new Font("SansSerif", Font.BOLD, 12)); customerLabel.setForeground(Ui.TEXT);
        customerRow.add(customerLabel, BorderLayout.WEST); customerRow.add(customerCombo, BorderLayout.CENTER);
        JLabel subtotalLabel = summaryLine("Subtotal", Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
        JLabel taxLabel = summaryLine("Tax (" + config.taxRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + "%)", Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
        JLabel totalLabel = summaryLine("TOTAL", Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
        JButton complete = Ui.primaryButton("Complete order & create receipt"); complete.setAlignmentX(Component.RIGHT_ALIGNMENT);
        checkout.add(customerRow); checkout.add(Box.createVerticalStrut(12)); checkout.add(subtotalLabel); checkout.add(Box.createVerticalStrut(5)); checkout.add(taxLabel); checkout.add(Box.createVerticalStrut(5)); checkout.add(totalLabel); checkout.add(Box.createVerticalStrut(13)); checkout.add(complete);
        cartPanel.add(checkout, BorderLayout.SOUTH);
        body.add(selection); body.add(cartPanel); page.add(body, BorderLayout.CENTER);

        List<CartLine> cart = new ArrayList<>();
        Runnable refreshCart = () -> {
            cartModel.setRowCount(0);
            for (CartLine line : cart) cartModel.addRow(new Object[]{line.item().id(), line.item().name(), line.quantity(), Formatters.money(line.lineTotal(), config.currencySymbol())});
            if (cart.isEmpty()) {
                subtotalLabel.setText("Subtotal     " + Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
                taxLabel.setText("Tax     " + Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
                totalLabel.setText("TOTAL     " + Formatters.money(BigDecimal.ZERO, config.currencySymbol()));
            } else {
                OrderCalculator.Totals totals = OrderCalculator.calculate(cart, config.taxRate());
                subtotalLabel.setText("Subtotal     " + Formatters.money(totals.subtotal(), config.currencySymbol()));
                taxLabel.setText("Tax     " + Formatters.money(totals.tax(), config.currencySymbol()));
                totalLabel.setText("TOTAL     " + Formatters.money(totals.total(), config.currencySymbol()));
            }
        };
        addToCart.addActionListener(e -> {
            MenuItem item = (MenuItem) itemCombo.getSelectedItem();
            if (item == null) { info("There are no available items. Add or enable an item in Menu management."); return; }
            int qty = (Integer) quantity.getValue();
            int index = -1;
            for (int i = 0; i < cart.size(); i++) if (cart.get(i).item().id().equals(item.id())) { index = i; break; }
            if (index >= 0) { CartLine old = cart.get(index); cart.set(index, new CartLine(item, old.quantity() + qty)); }
            else cart.add(new CartLine(item, qty));
            refreshCart.run();
        });
        remove.addActionListener(e -> {
            int row = cartTable.getSelectedRow(); if (row < 0) { info("Select an item in your order first."); return; }
            String id = String.valueOf(cartModel.getValueAt(cartTable.convertRowIndexToModel(row), 0));
            cart.removeIf(line -> line.item().id().equals(id)); refreshCart.run();
        });
        complete.addActionListener(e -> {
            if (cart.isEmpty()) { info("Add at least one item before completing the order."); return; }
            try {
                CustomerOption choice = (CustomerOption) customerCombo.getSelectedItem();
                Customer customer = choice == null ? null : choice.customer();
                String customerId = customer == null ? null : customer.id();
                String customerName = customer == null ? "Walk-in Customer" : customer.name();
                OrderRecord order = repository.createOrder(customerId, customerName, List.copyOf(cart), config.taxRate());
                Path receipt = ReceiptWriter.write(order, List.copyOf(cart), config.currencySymbol(), config.cafeteriaName());
                JOptionPane.showMessageDialog(this,
                        "Order completed successfully!\n\nOrder: " + Formatters.shortId(order.id()) +
                                "\nTotal: " + Formatters.money(order.total(), config.currencySymbol()) +
                                "\n\nReceipt saved to:\n" + receipt.toAbsolutePath(),
                        "Order completed", JOptionPane.INFORMATION_MESSAGE);
                cart.clear(); refreshCart.run();
            } catch (Exception ex) { Ui.showError(this, ex); }
        });
        return page;
    }

    private JLabel summaryLine(String label, String value) {
        JLabel result = new JLabel(label + "     " + value);
        result.setFont(new Font("SansSerif", label.equals("TOTAL") ? Font.BOLD : Font.PLAIN, label.equals("TOTAL") ? 16 : 12));
        result.setForeground(label.equals("TOTAL") ? Ui.TEAL_DARK : Ui.TEXT);
        return result;
    }

    private record CustomerOption(Customer customer, String label) {
        @Override public String toString() { return label; }
    }

    private JPanel buildOrdersPage() {
        JPanel page = Ui.page();
        page.add(intro("Order history", "Browse completed orders and open a detailed receipt view."), BorderLayout.NORTH);
        JPanel card = Ui.card(); card.setLayout(new BorderLayout(0, 14));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(Ui.subtitle("Every completed order is saved to the database."), BorderLayout.WEST);
        JButton refresh = Ui.secondaryButton("↻  Refresh"); top.add(refresh, BorderLayout.EAST); card.add(top, BorderLayout.NORTH);
        DefaultTableModel model = model("ID", "Order", "Customer", "Date", "Subtotal", "Tax", "Total", "Status");
        JTable table = new JTable(model); Ui.styleTable(table); hideFirstColumn(table);
        card.add(Ui.scroll(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actions.setOpaque(false);
        JButton details = Ui.primaryButton("View order details"); actions.add(details); card.add(actions, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);
        Runnable reload = () -> loadOrderRows(model);
        reload.run(); refresh.addActionListener(e -> reload.run());
        details.addActionListener(e -> {
            String id = selectedHiddenId(table, model); if (id == null) { info("Select an order first."); return; }
            showOrderDetails(id);
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) details.doClick(); }
        });
        return page;
    }

    private void loadOrderRows(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            for (OrderRecord order : repository.getOrders()) model.addRow(new Object[]{order.id(), Formatters.shortId(order.id()), order.customerName(), order.createdAt(),
                    Formatters.money(order.subtotal(), config.currencySymbol()), Formatters.money(order.tax(), config.currencySymbol()),
                    Formatters.money(order.total(), config.currencySymbol()), "Completed"});
        } catch (Exception ex) { Ui.showError(this, ex); }
    }

    private void showOrderDetails(String id) {
        try {
            OrderRecord order = repository.getOrder(id);
            if (order == null) { info("This order could not be found."); return; }
            StringBuilder text = new StringBuilder();
            text.append(config.cafeteriaName()).append("\nORDER ").append(Formatters.shortId(order.id())).append("\n")
                    .append(order.createdAt()).append("\nCustomer: ").append(order.customerName()).append("\n\n");
            for (OrderLine line : repository.getOrderLines(id)) text.append(line.itemName()).append("  × ").append(line.quantity())
                    .append("    ").append(Formatters.money(line.lineTotal(), config.currencySymbol())).append('\n');
            text.append("\nSubtotal: ").append(Formatters.money(order.subtotal(), config.currencySymbol()))
                    .append("\nTax: ").append(Formatters.money(order.tax(), config.currencySymbol()))
                    .append("\nTOTAL: ").append(Formatters.money(order.total(), config.currencySymbol()));
            JTextArea area = new JTextArea(text.toString()); area.setEditable(false); area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setBorder(new EmptyBorder(14, 14, 14, 14));
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Order details", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { Ui.showError(this, ex); }
    }

    private JPanel buildReportsPage() {
        JPanel page = Ui.page();
        page.add(intro("Sales overview", "A quick view of completed sales recorded by the system."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 16)); body.setOpaque(false);
        try {
            List<OrderRecord> orders = repository.getOrders();
            BigDecimal lifetime = orders.stream().map(OrderRecord::total).reduce(BigDecimal.ZERO, BigDecimal::add);
            DashboardStats today = repository.getDashboardStats();
            JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0)); stats.setOpaque(false);
            stats.add(statCard("TOTAL REVENUE", Formatters.money(lifetime, config.currencySymbol()), "All completed orders", "↗", Ui.TEAL));
            stats.add(statCard("TOTAL ORDERS", String.valueOf(orders.size()), "All-time completed orders", "▤", new Color(82, 126, 194)));
            stats.add(statCard("TODAY'S REVENUE", Formatters.money(today.salesToday(), config.currencySymbol()), "Revenue recorded today", "◷", Ui.ORANGE));
            body.add(stats, BorderLayout.NORTH);
            JPanel tableCard = Ui.card(); tableCard.setLayout(new BorderLayout(0, 12));
            JLabel heading = new JLabel("Sales ledger"); heading.setFont(new Font("SansSerif", Font.BOLD, 16)); heading.setForeground(Ui.TEXT); tableCard.add(heading, BorderLayout.NORTH);
            DefaultTableModel model = model("Order", "Customer", "Date", "Amount");
            for (OrderRecord order : orders) model.addRow(new Object[]{Formatters.shortId(order.id()), order.customerName(), order.createdAt(), Formatters.money(order.total(), config.currencySymbol())});
            JTable table = new JTable(model); Ui.styleTable(table); tableCard.add(Ui.scroll(table), BorderLayout.CENTER);
            body.add(tableCard, BorderLayout.CENTER);
        } catch (Exception ex) { body.add(errorCard("Reports could not be loaded", ex.getMessage()), BorderLayout.NORTH); }
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel labelled(String label, JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4)); wrapper.setOpaque(false);
        JLabel title = new JLabel(label); title.setFont(new Font("SansSerif", Font.BOLD, 11)); title.setForeground(Ui.MUTED);
        wrapper.add(title, BorderLayout.NORTH); wrapper.add(component, BorderLayout.CENTER); return wrapper;
    }

    private DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    }

    private void hideFirstColumn(JTable table) {
        if (table.getColumnModel().getColumnCount() > 0) table.removeColumn(table.getColumnModel().getColumn(0));
    }

    private String selectedHiddenId(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        return String.valueOf(model.getValueAt(modelRow, 0));
    }

    private void info(String message) { JOptionPane.showMessageDialog(this, message, "Cafeteria Management System", JOptionPane.INFORMATION_MESSAGE); }
}
