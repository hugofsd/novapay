package com.novapay.fraud_service.controller;

import com.novapay.fraud_service.dto.response.FraudAnalysisResponse;
import com.novapay.fraud_service.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudAnalysisService fraudAnalysisService;

    // GET /fraud/{transactionId}

    @GetMapping("/{transactionId}")
    public ResponseEntity<FraudAnalysisResponse> getFraudAnalysis(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(
                FraudAnalysisResponse.from(
                        fraudAnalysisService.findByTransactionId(transactionId)
                )
        );
    }
}