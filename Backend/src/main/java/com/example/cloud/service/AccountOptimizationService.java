package com.example.cloud.service;

import com.example.cloud.dto.AccountOptimizationResponse;

import java.util.UUID;

public interface AccountOptimizationService {

    AccountOptimizationResponse optimizeAccount(

            UUID cloudAccountId,

            String email
    );
}