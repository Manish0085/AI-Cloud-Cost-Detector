package com.example.cloud.service.impl;


import com.example.cloud.dto.AwsIdentity;
import com.example.cloud.dto.CloudAccountResponse;
import com.example.cloud.dto.ConnectCloudRequest;
import com.example.cloud.entity.CloudAccount;
import com.example.cloud.entity.User;
import com.example.cloud.respository.CloudAccountRepository;
import com.example.cloud.respository.UserRepo;
import com.example.cloud.service.AwsValidationService;
import com.example.cloud.service.CloudAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CloudAccountServiceImpl
        implements CloudAccountService {

    private final CloudAccountRepository cloudAccountRepository;
    private final UserRepo userRepository;
    private final AwsValidationService awsValidationService;

    @Override
    public void connectCloudAccount(
            ConnectCloudRequest request,
            String userEmail
    ) {

        AwsIdentity identity =
                awsValidationService.validateCredentials(
                        request.accessKey(),
                        request.secretKey()
                );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        CloudAccount account =
                CloudAccount.builder()
                        .accountName(request.accountName())
                        .provider(request.provider())
                        .accessKey(request.accessKey())
                        .secretKey(request.secretKey())
                        .cloudAccountId(identity.accountId())
                        .arn(identity.arn())
                        .connected(true)
                        .user(user)
                        .build();

        cloudAccountRepository.save(account);
    }

    @Override
    public List<CloudAccountResponse> getAccounts(
            String email
    ) {

        return cloudAccountRepository
                .findByUserEmail(email)
                .stream()
                .map(account ->
                        new CloudAccountResponse(
                                account.getId(),
                                account.getAccountName(),
                                account.getProvider().name(),
                                account.isConnected()
                        ))
                .toList();
    }



}