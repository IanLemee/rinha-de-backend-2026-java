package com.ian.rinha_backend.dto;

public record FraudResponse(
        boolean approved,
        double fraud_score
) {
}
