package com.ian.rinha_backend.dto;

import java.time.Instant;

public record Transaction(
        double amount,
        int installments,
        Instant requested_at
) { }
