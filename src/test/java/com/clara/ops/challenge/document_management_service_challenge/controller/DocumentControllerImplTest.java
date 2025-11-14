package com.clara.ops.challenge.document_management_service_challenge.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerImplTest {

  @InjectMocks private DocumentControllerImpl documentController;

  @Mock private DocumentService documentService;

  @Mock private ModelMapper modelMapper;

  // Normal file
  private MockMultipartFile regularFile;
  // Invalid extension file
  private MockMultipartFile invalidTypeFile;
  // File too large
  private MockMultipartFile largeFile;

  private static final long MB = 1024 * 1024;

  private final String testUser = "diego";
  private final String testUser2 = "clara";
  private final List<String> testTags = Arrays.asList(new String[] {"tag1", "tag2", "tag3"});
  private final List<String> testTags2 = Arrays.asList(new String[] {"tag2", "tag3", "tag4"});

  private final String timestamp = LocalDateTime.now().toString();

  @BeforeEach
  void setUp() {
    regularFile =
        new MockMultipartFile(
            "regular-file",
            "regular file.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "this".getBytes()) {
          public long getSize() {
            return 12 * MB;
          }
        };

    invalidTypeFile =
        new MockMultipartFile(
            "regular-file",
            "regular file.yml",
            String.valueOf(MediaType.APPLICATION_YAML),
            "this".getBytes()) {
          public long getSize() {
            return 12 * MB;
          }
        };

    largeFile =
        new MockMultipartFile(
            "large-file", "large file.pdf", MediaType.APPLICATION_PDF_VALUE, "this".getBytes()) {
          public long getSize() {
            return 501 * MB;
          }
        };
  }

  @Test
  void uploadDocument_successfullyUpload() {
    DocumentDTO responseDTO = new DocumentDTO();
    responseDTO.setId("1");
    responseDTO.setType(MediaType.APPLICATION_PDF_VALUE);
    responseDTO.setUser(testUser);
    responseDTO.setTags(testTags);
    responseDTO.setSize((int) regularFile.getSize());
    when(modelMapper.map(any(), any())).thenReturn(responseDTO);
    when(documentService.uploadDocument(anyString(), anyString(), anyList(), any()))
        .thenReturn(new Document());
    ResponseEntity<DocumentDTO> response =
        documentController.uploadDocument(testUser, "regular-file", testTags, invalidTypeFile);

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(responseDTO, response.getBody());
    verify(documentService, times(1)).uploadDocument(anyString(), anyString(), anyList(), any());
  }

  @Test
  void uploadDocument_invalidFileExtension() {
    when(documentService.uploadDocument(anyString(), anyString(), anyList(), any()))
        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type not allowed"));
    ResponseStatusException exception =
        Assertions.assertThrows(
            ResponseStatusException.class,
            () ->
                documentController.uploadDocument(
                    testUser, "regular-file", testTags, invalidTypeFile));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    verify(documentService, times(1)).uploadDocument(anyString(), anyString(), anyList(), any());
  }

  @Test
  void uploadDocument_fileTooLarge() {
    when(documentService.uploadDocument(anyString(), anyString(), anyList(), any()))
        .thenThrow(
            new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds maximum size"));
    ResponseStatusException exception =
        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> documentController.uploadDocument(testUser, "large-file", testTags, largeFile));

    assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getStatusCode());
    verify(documentService, times(1)).uploadDocument(anyString(), anyString(), anyList(), any());
  }
}
