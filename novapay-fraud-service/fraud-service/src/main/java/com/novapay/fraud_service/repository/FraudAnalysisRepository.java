package com.novapay.fraud_service.repository;

import com.novapay.fraud_service.model.FraudAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudAnalysisRepository extends JpaRepository<FraudAnalysis, Long> {

Optional<FraudAnalysis> findByTransactionId(Long transactionId);

}

