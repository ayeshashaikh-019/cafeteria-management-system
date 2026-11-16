package com.cafeteria.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Formatters {
    private Formatters() { }

    public static String money(BigDecimal amount, String symbol) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return symbol + " " + format.format(amount == null ? BigDecimal.ZERO : amount);
    }

    public static String shortId(String id) {
        if (id == null || id.length() < 8) return id == null ? "" : id;
        return "#" + id.substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
