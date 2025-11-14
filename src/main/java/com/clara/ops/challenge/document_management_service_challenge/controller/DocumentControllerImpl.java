package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.PaginatedDocumentSearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class DocumentControllerImpl implements DocumentController {

  private final DocumentService documentService;
  private final ModelMapper modelMapper;

  @Autowired
  public DocumentControllerImpl(DocumentService documentService, ModelMapper modelMapper) {
    this.documentService = documentService;
    this.modelMapper = modelMapper;
  }

  @Override
  public ResponseEntity<DocumentDTO> uploadDocument(
      String user, String name, List<String> tags, MultipartFile file) {
    log.info("Upload document");
    Document doc = documentService.uploadDocument(user, name, tags, file);
    DocumentDTO dto = modelMapper.map(doc, DocumentDTO.class);
    return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
  }

  @Override
  public ResponseEntity<PaginatedDocumentSearchResponse> searchDocuments(
      DocumentSearchRequest searchRequest, Integer page, Integer size) {
    log.info("Search documents endpoint page:{}, size:{}", page, size);
    Page<Document> documentPage = documentService.searchDocuments(searchRequest, page, size);
    if (documentPage.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No documents were found");
    }
    log.info(
        "Found {} documents in {} pages",
        documentPage.getTotalElements(),
        documentPage.getTotalPages());
    List<DocumentDTO> documentDTOS =
        documentPage.stream().map(p -> modelMapper.map(p, DocumentDTO.class)).toList();
    PaginatedDocumentSearchResponse response = new PaginatedDocumentSearchResponse();
    response.setDocuments(documentDTOS);
    response.setPage(page);
    response.setSize(size);
    response.setNumberOfDocuments(documentPage.getNumberOfElements());
    response.setTotalPages(documentPage.getTotalPages());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<DocumentDownloadResponse> downloadDocument(Integer documentId) {
    log.info("Received request to download document with Id: {}", documentId);
    return new ResponseEntity<>(documentService.getDocumentDownloadURL(documentId), HttpStatus.OK);
  }
}
