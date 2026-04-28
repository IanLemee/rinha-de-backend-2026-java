package com.ian.rinha_backend.dto;

public record Terminal(
        boolean is_online,
        boolean card_present,
        double km_from_home
) {}
