package com.cafeteria;

import com.cafeteria.config.AppConfig;
import com.cafeteria.db.CafeteriaRepository;
import com.cafeteria.db.Database;
import com.cafeteria.ui.AppFrame;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        try {
            // FlatLaf gives Swing a clean, modern appearance. The app still opens with the system look if unavailable.
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ignored) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignoredAgain) { }
        }
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);

        AppConfig config = AppConfig.load();
        Database database = new Database(config);
        try {
            database.initialize();
            CafeteriaRepository repository = new CafeteriaRepository(database);
            repository.seedMenuIfEmpty();
            SwingUtilities.invokeLater(() -> {
                AppFrame frame = new AppFrame(config, repository);
                frame.setVisible(true);
            });
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> showStartupError(config, ex));
        }
    }

    private static void showStartupError(AppConfig config, Exception ex) {
        String help = "The application could not connect to MySQL.\n\n" +
                "Please check these items:\n" +
                "1. MySQL Server is installed and running.\n" +
                "2. You ran database/00_create_database.sql.\n" +
                "3. Your config/app.properties has the correct db.url, db.username and db.password.\n\n" +
                "Configured URL: " + config.dbUrl() + "\n\n" +
                "Technical detail: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        JOptionPane.showMessageDialog(null, help, "Database connection needed", JOptionPane.ERROR_MESSAGE);
    }
}
