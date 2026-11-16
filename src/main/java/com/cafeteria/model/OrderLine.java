package com.cafeteria.model;

import java.math.BigDecimal;

public record OrderLine(String itemName, int quantity, BigDecimal unitPrice,
                        BigDecimal lineTotal) { }
