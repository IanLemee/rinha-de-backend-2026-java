package com.ian.rinha_backend.dto;

import java.util.List;

public record Customer(
        double avg_amount,
        int tx_count_24h,
        List<String> known_merchants
) {}
