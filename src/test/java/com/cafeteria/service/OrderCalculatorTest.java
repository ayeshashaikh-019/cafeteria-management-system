package com.cafeteria.service;

import com.cafeteria.model.CartLine;
import com.cafeteria.model.MenuItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderCalculatorTest {
    @Test
    void calculatesSubtotalTaxAndTotal() {
        MenuItem burger = item("650.00");
        MenuItem drink = item("160.00");
        var totals = OrderCalculator.calculate(List.of(new CartLine(burger, 2), new CartLine(drink, 1)), new BigDecimal("0.05"));

        assertEquals(new BigDecimal("1460.00"), totals.subtotal());
        assertEquals(new BigDecimal("73.00"), totals.tax());
        assertEquals(new BigDecimal("1533.00"), totals.total());
    }

    @Test
    void roundsTaxToTwoDecimalPlaces() {
        var totals = OrderCalculator.calculate(List.of(new CartLine(item("99.99"), 1)), new BigDecimal("0.05"));
        assertEquals(new BigDecimal("5.00"), totals.tax());
        assertEquals(new BigDecimal("104.99"), totals.total());
    }

    @Test
    void rejectsAnEmptyOrder() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderCalculator.calculate(List.of(), new BigDecimal("0.05")));
    }

    @Test
    void rejectsAnInvalidTaxRate() {
        assertThrows(IllegalArgumentException.class,
                () -> OrderCalculator.calculate(List.of(new CartLine(item("10.00"), 1)), new BigDecimal("1.5")));
    }

    private MenuItem item(String price) {
        return new MenuItem(UUID.randomUUID().toString(), "Test item", "Test", "", new BigDecimal(price), true);
    }
}
