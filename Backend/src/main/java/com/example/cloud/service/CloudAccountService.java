package com.example.cloud.service;

import com.example.cloud.dto.CloudAccountResponse;
import com.example.cloud.dto.ConnectCloudRequest;

import java.util.List;

public interface CloudAccountService {

    void connectCloudAccount(
            ConnectCloudRequest request,
            String userEmail
    );


    List<CloudAccountResponse> getAccounts(
            String email
    );
}