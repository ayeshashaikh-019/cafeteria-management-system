package com.cafeteria.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Loads local settings, with environment variables taking priority. */
public final class AppConfig {
    private final Properties properties = new Properties();

    private AppConfig() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/cafeteria_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("cafeteria.name", "Campus Cafe");
        properties.setProperty("currency.symbol", "Rs.");
        properties.setProperty("tax.rate", "0.05");

        Path localConfig = Path.of("config", "app.properties");
        if (Files.exists(localConfig)) {
            try (InputStream input = Files.newInputStream(localConfig)) {
                properties.load(input);
            } catch (IOException ex) {
                System.err.println("Could not read config/app.properties: " + ex.getMessage());
            }
        }

        applyEnvironment("DB_URL", "db.url");
        applyEnvironment("DB_USERNAME", "db.username");
        applyEnvironment("DB_PASSWORD", "db.password");
        applyEnvironment("CAFETERIA_NAME", "cafeteria.name");
        applyEnvironment("CURRENCY_SYMBOL", "currency.symbol");
        applyEnvironment("TAX_RATE", "tax.rate");
    }

    public static AppConfig load() { return new AppConfig(); }

    private void applyEnvironment(String environmentKey, String propertyKey) {
        String value = System.getenv(environmentKey);
        if (value != null && !value.isBlank()) properties.setProperty(propertyKey, value);
    }

    public String get(String key) { return properties.getProperty(key, ""); }
    public String get(String key, String fallback) { return properties.getProperty(key, fallback); }
    public String dbUrl() { return get("db.url"); }
    public String dbUsername() { return get("db.username"); }
    public String dbPassword() { return get("db.password"); }
    public String cafeteriaName() { return get("cafeteria.name", "Campus Cafe"); }
    public String currencySymbol() { return get("currency.symbol", "Rs."); }

    public BigDecimal taxRate() {
        try {
            BigDecimal rate = new BigDecimal(get("tax.rate", "0.05"));
            if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) return new BigDecimal("0.05");
            return rate;
        } catch (NumberFormatException ex) {
            return new BigDecimal("0.05");
        }
    }
}
