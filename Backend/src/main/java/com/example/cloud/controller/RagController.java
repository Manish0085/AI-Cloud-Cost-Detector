package com.example.cloud.controller;

import com.example.cloud.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @GetMapping("/search")
    public String search(
            @RequestParam String query
    ) {

        return ragService.retrieveContext(query);
    }
}