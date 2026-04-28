package com.ian.rinha_backend.dto;

public record Merchant(
        String id,
        String mcc,
        double avg_amount
) {}
