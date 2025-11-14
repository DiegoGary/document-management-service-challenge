package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Document;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

  Document uploadDocument(String user, String name, List<String> tags, MultipartFile file);

  Page<Document> searchDocuments(
      DocumentSearchRequest documentSearchRequest, Integer page, Integer size);

  DocumentDownloadResponse getDocumentDownloadURL(Integer documentId);
}
