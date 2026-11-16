package com.cafeteria.model;

import java.math.BigDecimal;

public record DashboardStats(long ordersToday, BigDecimal salesToday,
                             long menuItems, long unavailableItems, long customers) { }
