package com.ian.rinha_backend.dto;

public record TransactionPayload(
        String id,
        Transaction transaction,
        Customer customer,
        Merchant merchant,
        Terminal terminal,
        LastTransaction last_transaction
) {}
