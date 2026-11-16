package com.cafeteria.util;

import com.cafeteria.model.CartLine;
import com.cafeteria.model.OrderRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ReceiptWriter {
    private ReceiptWriter() { }

    public static Path write(OrderRecord order, List<CartLine> items, String currencySymbol,
                             String cafeteriaName) throws IOException {
        Path folder = Path.of("receipts");
        Files.createDirectories(folder);
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path file = folder.resolve("receipt-" + stamp + "-" + order.id().substring(0, 8) + ".txt");
        StringBuilder text = new StringBuilder();
        text.append("========================================\n");
        text.append("             ").append(cafeteriaName).append("\n");
        text.append("              CUSTOMER RECEIPT\n");
        text.append("========================================\n");
        text.append("Order: ").append(Formatters.shortId(order.id())).append('\n');
        text.append("Date:  ").append(order.createdAt()).append('\n');
        text.append("Customer: ").append(order.customerName()).append("\n\n");
        text.append(String.format("%-21s %4s %12s%n", "ITEM", "QTY", "AMOUNT"));
        text.append("----------------------------------------\n");
        for (CartLine line : items) {
            String name = line.item().name().length() > 20 ? line.item().name().substring(0, 20) : line.item().name();
            text.append(String.format("%-21s %4d %12s%n", name, line.quantity(),
                    Formatters.money(line.lineTotal(), currencySymbol)));
        }
        text.append("----------------------------------------\n");
        text.append(String.format("%-26s %14s%n", "Subtotal", Formatters.money(order.subtotal(), currencySymbol)));
        text.append(String.format("%-26s %14s%n", "Tax", Formatters.money(order.tax(), currencySymbol)));
        text.append(String.format("%-26s %14s%n", "TOTAL", Formatters.money(order.total(), currencySymbol)));
        text.append("\n        Thank you! Please visit again.\n");
        text.append("========================================\n");
        Files.writeString(file, text, StandardCharsets.UTF_8);
        return file;
    }
}
