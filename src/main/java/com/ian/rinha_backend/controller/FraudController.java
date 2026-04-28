package com.ian.rinha_backend.controller;

import com.ian.rinha_backend.dto.FraudResponse;
import com.ian.rinha_backend.dto.TransactionPayload;
import com.ian.rinha_backend.service.VectorSearch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudController {
    private final VectorSearch vectorSearch;

    public FraudController(VectorSearch vectorSearch) {
        this.vectorSearch = vectorSearch;
    }

    @GetMapping("ready")
    public ResponseEntity<Void> check() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("fraud-score")
    public ResponseEntity<FraudResponse> fraudSearch(@RequestBody TransactionPayload payload) {
        return new ResponseEntity<>(vectorSearch.vectorSearch(payload), HttpStatus.OK);
    }
}
