package com.cafeteria.model;

import java.math.BigDecimal;

public record OrderRecord(String id, String customerName, BigDecimal subtotal,
                          BigDecimal tax, BigDecimal total, String createdAt) { }
