package com.example.cloud.service.impl;

import com.example.cloud.service.RagService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagServiceImpl implements RagService {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;


    private boolean knowledgeAlreadyLoaded() {

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM vector_store",
                        Integer.class
                );

        return count != null && count > 0;
    }

    @PostConstruct
    public void loadKnowledge() {

        try {

            if (knowledgeAlreadyLoaded()) {

                log.info("Knowledge base already loaded");

                return;
            }

            PagePdfDocumentReader pdfReader =
                    new PagePdfDocumentReader(
                            new ClassPathResource(
                                    "knowledge/AWS_Cloud_Cost_Optimization_Handbook.pdf"
                            )
                    );

            List<Document> pages =
                    pdfReader.get();

            log.info(
                    "PDF pages loaded: {}",
                    pages.size()
            );

            TokenTextSplitter splitter =
                    new TokenTextSplitter();

            List<Document> chunks =
                    splitter.apply(pages);

            vectorStore.add(chunks);

            log.info(
                    "Stored {} chunks in vector store",
                    chunks.size()
            );

            log.info("Knowledge loaded successfully");

        } catch (Exception ex) {

            log.error(
                    "Failed to load knowledge base",
                    ex
            );
        }
    }

    @Override
    public void ingestDocument(
            String documentContent
    ) {

        Document document =
                new Document(
                        documentContent,
                        Map.of(
                                "source", "aws-cost-handbook",
                                "version", "v1"
                        )
                );

        TokenTextSplitter splitter =
                new TokenTextSplitter();

        List<Document> chunks =
                splitter.apply(
                        List.of(document)
                );

        vectorStore.add(chunks);

        log.info(
                "Stored {} chunks in vector store",
                chunks.size()
        );
    }

    @Override
    public String retrieveContext(
            String query
    ) {

        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .build()
                );

        if (documents == null || documents.isEmpty()) {

            return "No relevant context found.";
        }

        return documents.stream()
                .map(Document::getText)
                .distinct()
                .collect(
                        java.util.stream.Collectors.joining(
                                "\n\n"
                        )
                );
    }
}