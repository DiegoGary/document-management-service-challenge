package com.clara.ops.challenge.document_management_service_challenge.controller;


import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentUploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.PaginatedDocumentSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface IDocumentController {

    ResponseEntity<HttpStatus> uploadDocument(DocumentUploadRequest documentUpload);

    ResponseEntity<PaginatedDocumentSearchResponse> searchDocument(DocumentSearchRequest searchRequest);

    ResponseEntity<DocumentDownloadResponse> downloadDocument(long documentId);
}
