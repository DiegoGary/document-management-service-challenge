package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document uploadDocument(String user, String name, List<String> tags, MultipartFile file);

    Page<Document> searchDocuments(DocumentSearchRequest documentSearchRequest, Integer page, Integer size);

    Document downloadDocument(Integer documentId);
}
