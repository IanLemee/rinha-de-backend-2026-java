package com.ian.rinha_backend.dto;

import java.time.Instant;

public record LastTransaction(
        Instant timestamp,
        double km_from_current
) { }
