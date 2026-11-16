package com.cafeteria.service;

import com.cafeteria.model.CartLine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Pure billing logic kept separate so it can be unit-tested. */
public final class OrderCalculator {
    private OrderCalculator() { }

    public static Totals calculate(List<CartLine> lines, BigDecimal taxRate) {
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("Add at least one item to the order.");
        if (taxRate == null || taxRate.signum() < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1.");
        }
        BigDecimal subtotal = lines.stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        return new Totals(subtotal, tax, subtotal.add(tax).setScale(2, RoundingMode.HALF_UP));
    }

    public record Totals(BigDecimal subtotal, BigDecimal tax, BigDecimal total) { }
}
