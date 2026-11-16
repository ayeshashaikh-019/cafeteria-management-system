package com.cafeteria.model;

import java.math.BigDecimal;

public record MenuItem(String id, String name, String category, String description,
                       BigDecimal price, boolean available) { }
