package com.clara.ops.challenge.document_management_service_challenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Tag;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.User;
import com.clara.ops.challenge.document_management_service_challenge.domain.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.domain.repository.TagRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

  @Mock private UserService userService;
  @Mock private MinIOService minIOService;
  @Mock private DocumentRepository documentRepository;
  @Mock private TagRepository tagRepository;

  @Mock private MultipartFile multipartFile;

  @InjectMocks private DocumentServiceImpl documentService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setName("test-user");
  }

  @Nested
  @DisplayName("uploadDocument")
  class UploadDocumentTests {

    @Test
    @DisplayName("should upload document successfully")
    void uploadDocument_success() {
      String username = "  test-user  ";
      String name = "  my-doc.pdf  ";
      List<String> tags = Arrays.asList("  tag1 ", "tag2  ");

      when(multipartFile.isEmpty()).thenReturn(false);
      when(multipartFile.getSize()).thenReturn(1024L);
      when(multipartFile.getContentType()).thenReturn("application/pdf");

      when(userService.validateAndCreateUser(anyString())).thenReturn(user);
      when(minIOService.uploadDocument(eq(user.getName()), anyString(), eq(multipartFile)))
          .thenReturn("bucket/path/my-doc.pdf");

      Document saved = new Document();
      saved.setId(1);
      saved.setName("my-doc.pdf");
      saved.setUser(user);
      when(documentRepository.save(any(Document.class))).thenReturn(saved);

      Document result = documentService.uploadDocument(username, name, tags, multipartFile);

      assertNotNull(result);
      assertEquals(saved.getId(), result.getId());
      assertEquals("my-doc.pdf", result.getName());
      assertEquals(user, result.getUser());

      // Verify document persisted with expected values
      ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
      verify(documentRepository).save(docCaptor.capture());
      Document persisted = docCaptor.getValue();
      assertEquals("my-doc.pdf", persisted.getName());
      assertEquals(user, persisted.getUser());
      assertEquals(multipartFile.getSize(), persisted.getFileSize());
      assertEquals(multipartFile.getContentType(), persisted.getFileType());
      assertEquals("bucket/path/my-doc.pdf", persisted.getMinioPath());

      // Verify tags persisted
      verify(tagRepository, atLeast(0)).save(any(Tag.class));
    }

    @Test
    @DisplayName("should throw BAD_REQUEST when username or name is blank")
    void uploadDocument_missingNames() {

      ResponseStatusException ex1 =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  documentService.uploadDocument(
                      " ", "my-doc.pdf", Collections.emptyList(), multipartFile));
      assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

      ResponseStatusException ex2 =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  documentService.uploadDocument(
                      "user", " ", Collections.emptyList(), multipartFile));
      assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());

      verifyNoInteractions(userService, minIOService, documentRepository, tagRepository);
    }

    @Test
    @DisplayName("should throw BAD_REQUEST when file is empty")
    void uploadDocument_emptyFile() {
      when(multipartFile.isEmpty()).thenReturn(true);

      ResponseStatusException ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  documentService.uploadDocument(
                      "user", "my-doc.pdf", Collections.emptyList(), multipartFile));

      assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
      assertEquals("File is empty", ex.getReason());
      verifyNoInteractions(userService, minIOService, documentRepository, tagRepository);
    }

    @Test
    @DisplayName("should throw PAYLOAD_TOO_LARGE when file exceeds maximum size")
    void uploadDocument_tooLargeFile() {
      when(multipartFile.isEmpty()).thenReturn(false);
      when(multipartFile.getSize()).thenReturn(600L * 1024 * 1024); // > 500MB

      ResponseStatusException ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  documentService.uploadDocument(
                      "user", "my-doc.pdf", Collections.emptyList(), multipartFile));

      assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, ex.getStatusCode());
      assertEquals("File exceeds maximum size", ex.getReason());
      verifyNoInteractions(userService, minIOService, documentRepository, tagRepository);
    }

    @Test
    @DisplayName("should throw BAD_REQUEST when file type is not PDF")
    void uploadDocument_invalidFileType() {
      when(multipartFile.isEmpty()).thenReturn(false);
      when(multipartFile.getSize()).thenReturn(1024L);
      when(multipartFile.getContentType()).thenReturn("image/png");

      ResponseStatusException ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  documentService.uploadDocument(
                      "user", "my-doc.pdf", Collections.emptyList(), multipartFile));

      assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
      assertEquals("File type not allowed", ex.getReason());
      verifyNoInteractions(userService, minIOService, documentRepository, tagRepository);
    }
  }

  @Nested
  @DisplayName("searchDocuments")
  class SearchDocumentsTests {

    @Test
    @DisplayName(
        "should call findAllByUsernameAndNameAndTagName when username, name and tags are provided")
    void searchDocuments_usernameNameTags() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser("user");
      request.setName("doc.pdf");
      request.setTags(Arrays.asList("tag1", "tag2"));

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByUsernameAndNameAndTagName(
              anyString(), anyString(), anyList(), any(Pageable.class)))
          .thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository)
          .findAllByUsernameAndNameAndTagName(eq("user"), eq("doc.pdf"), anyList(), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByUsernameAndName when username and name are provided")
    void searchDocuments_usernameName() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser("user");
      request.setName("doc.pdf");
      request.setTags(Collections.emptyList());

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByUsernameAndName(
              anyString(), anyString(), any(Pageable.class)))
          .thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByUsernameAndName(eq("user"), eq("doc.pdf"), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByUsernameAndTags when only username and tags are provided")
    void searchDocuments_usernameTags() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser("user");
      request.setName(null);
      request.setTags(Arrays.asList("tag1"));

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByUsernameAndTags(anyString(), anyList(), any(Pageable.class)))
          .thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByUsernameAndTags(eq("user"), anyList(), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByUsername when only username is provided")
    void searchDocuments_usernameOnly() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser("user");
      request.setName(null);
      request.setTags(Collections.emptyList());

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByUsername(anyString(), any(Pageable.class))).thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByUsername(eq("user"), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByNameAndTags when only name is provided and tags are empty")
    void searchDocuments_nameOnly() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser(null);
      request.setName("doc.pdf");
      request.setTags(Collections.emptyList());

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByNameAndTags(anyString(), anyList(), any(Pageable.class)))
          .thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByNameAndTags(eq("doc.pdf"), anyList(), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByName when only name and tags are provided")
    void searchDocuments_nameAndTags() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser(null);
      request.setName("doc.pdf");
      request.setTags(Arrays.asList("tag1"));

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByName(anyString(), any(Pageable.class))).thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByName(eq("doc.pdf"), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAllByTags when only tags are provided")
    void searchDocuments_tagsOnly() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser(null);
      request.setName(null);
      request.setTags(Arrays.asList("tag1"));

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAllByTags(anyList(), any(Pageable.class))).thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAllByTags(anyList(), any());
      verifyNoMoreInteractions(documentRepository);
    }

    @Test
    @DisplayName("should call findAll when no filters are provided")
    void searchDocuments_noFilters() {
      DocumentSearchRequest request = new DocumentSearchRequest();
      request.setUser(null);
      request.setName(null);
      request.setTags(Collections.emptyList());

      Page<Document> page = new PageImpl<>(Collections.emptyList());
      when(documentRepository.findAll(any(Pageable.class))).thenReturn(page);

      Page<Document> result = documentService.searchDocuments(request, 0, 10);

      assertSame(page, result);
      verify(documentRepository).findAll((any(Pageable.class)));
      verifyNoMoreInteractions(documentRepository);
    }
  }

  @Nested
  @DisplayName("getDocumentDownloadURL")
  class GetDocumentDownloadURLTests {

    @Test
    @DisplayName("should return download response when document exists")
    void getDocumentDownloadURL_success() {
      Document document = new Document();
      document.setId(1);
      document.setName("doc.pdf");
      document.setMinioPath("bucket/path/doc.pdf");
      document.setUser(user);

      when(documentRepository.findById(1)).thenReturn(Optional.of(document));
      when(minIOService.getDocumentURL("bucket/path/doc.pdf")).thenReturn("http://download-url");

      DocumentDownloadResponse response = documentService.getDocumentDownloadURL(1);

      assertNotNull(response);
      assertEquals("http://download-url", response.getDownloadURL());
      assertEquals("doc.pdf", response.getDocumentName());
      assertEquals(user.getName(), response.getUser());
    }

    @Test
    @DisplayName("should throw NOT_FOUND when document does not exist")
    void getDocumentDownloadURL_notFound() {
      when(documentRepository.findById(1)).thenReturn(Optional.empty());

      ResponseStatusException ex =
          assertThrows(
              ResponseStatusException.class, () -> documentService.getDocumentDownloadURL(1));

      assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
      assertEquals("Document was not found", ex.getReason());
      verifyNoInteractions(minIOService);
    }
  }
}
