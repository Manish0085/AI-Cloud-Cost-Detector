package com.example.cloud.entity;

import com.example.cloud.enums.CloudProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cloud_accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudAccount extends BaseEntity {

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CloudProvider provider;

    @Column(nullable = false)
    private String accessKey;

    @Column(nullable = false)
    private String secretKey;

    @Column(nullable = false)
    private String cloudAccountId;

    @Column(nullable = false)
    private String arn;

    @Builder.Default
    private boolean connected = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}