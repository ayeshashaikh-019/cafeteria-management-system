package com.cafeteria.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CartLine(MenuItem item, int quantity) {
    public CartLine {
        if (item == null) throw new IllegalArgumentException("Menu item is required.");
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1.");
    }

    public BigDecimal lineTotal() {
        return item.price().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
