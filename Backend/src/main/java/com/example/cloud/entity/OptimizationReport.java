package com.example.cloud.entity;

import com.example.cloud.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "optimization_reports",
        indexes = {
                @Index(
                        name = "idx_report_account",
                        columnList = "cloudAccountId"
                ),
                @Index(
                        name = "idx_report_created_at",
                        columnList = "createdAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationReport
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID cloudAccountId;

    private Integer totalResources;

    private Integer totalFindings;

    @Column(columnDefinition = "TEXT")
    private String executiveSummary;
}