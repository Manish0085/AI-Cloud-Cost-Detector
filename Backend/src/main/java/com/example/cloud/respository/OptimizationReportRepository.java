package com.example.cloud.respository;

import com.example.cloud.entity.OptimizationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OptimizationReportRepository
        extends JpaRepository<OptimizationReport, UUID> {

    Page<OptimizationReport>
    findByCloudAccountIdOrderByCreatedAtDesc(
            UUID cloudAccountId,
            Pageable pageable
    );

    List<OptimizationReport> findByCloudAccountId(UUID accountId);
}