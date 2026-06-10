package com.example.cloud.service;

import java.util.List;

public interface RagService {

    void ingestDocument(
            String documentContent
    );

    String retrieveContext(
            String query
    );
}