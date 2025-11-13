package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.PaginatedDocumentSearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import com.clara.ops.challenge.document_management_service_challenge.service.MinIOService;
import com.clara.ops.challenge.document_management_service_challenge.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class DocumentControllerImpl implements DocumentController {

    private final DocumentService documentService;
    private final ModelMapper modelMapper;

    @Autowired
    public DocumentControllerImpl(DocumentService documentService, ModelMapper modelMapper){
        this.documentService = documentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<DocumentDTO> uploadDocument(String user, String name, List<String> tags, MultipartFile file) {
        Document doc = documentService.uploadDocument(user, name, tags, file);
        DocumentDTO dto = modelMapper.map(doc, DocumentDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<PaginatedDocumentSearchResponse> searchDocuments(DocumentSearchRequest searchRequest, Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<DocumentDownloadResponse> downloadDocument(Integer documentId) {
        return null;
    }
}
