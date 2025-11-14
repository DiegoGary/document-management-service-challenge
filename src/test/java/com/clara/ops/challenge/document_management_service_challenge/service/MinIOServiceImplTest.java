package com.clara.ops.challenge.document_management_service_challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class MinIOServiceImplTest {

  @Mock private MinioClient minioClient;

  @Mock private MultipartFile multipartFile;

  @InjectMocks private MinIOServiceImpl minioService;

  @BeforeEach
  void setUp() throws Exception {
    setField(minioService, "bucket", "test-bucket");
    setField(minioService, "uploadChunkSize", 5242881L);
  }

  static void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  @Test
  void bucketExists_returnsTrueWhenClientReturnsTrue() throws Exception {
    Mockito.when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

    Boolean result = minioService.bucketExists();

    assertEquals(true, result);
    Mockito.verify(minioClient).bucketExists(any(BucketExistsArgs.class));
  }

  @Test
  void bucketExists_wrapsGenericExceptionInResponseStatusException() throws Exception {
    RuntimeException cause = new RuntimeException("unexpected");
    Mockito.when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenThrow(cause);

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> minioService.bucketExists());

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    assertEquals("unexpected", ex.getReason());
  }

  @Test
  void createBucket_callsMakeBucketOnClient() throws Exception {
    minioService.createBucket();

    Mockito.verify(minioClient).makeBucket(any(MakeBucketArgs.class));
  }

  @Test
  void createBucket_propagatesException() throws Exception {
    RuntimeException cause = new RuntimeException("create-error");
    Mockito.doThrow(cause).when(minioClient).makeBucket(any(MakeBucketArgs.class));

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> minioService.createBucket());

    assertEquals("create-error", thrown.getMessage());
  }

  @Test
  void getDocumentURL_returnsPresignedUrl() throws Exception {
    String path = "user/file.pdf";
    String expectedUrl = "http://minio/presigned-url";

    Mockito.when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenReturn(expectedUrl);

    String result = minioService.getDocumentURL(path);

    assertEquals(expectedUrl, result);
    Mockito.verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
  }

  @Test
  void getDocumentURL_wrapsGenericExceptionInResponseStatusException() throws Exception {
    RuntimeException cause = new RuntimeException("unexpected");
    Mockito.when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenThrow(cause);

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> minioService.getDocumentURL("user/file.pdf"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    assertEquals("unexpected", ex.getReason());
  }

  @Test
  void uploadDocument_whenBucketExists_doesNotCreateBucketAndUploads() throws Exception {
    MinIOServiceImpl spyService = Mockito.spy(new MinIOServiceImpl(minioClient));
    setField(spyService, "bucket", "test-bucket");
    setField(spyService, "uploadChunkSize", 5242881L);

    Mockito.doReturn(true).when(spyService).bucketExists();

    byte[] content = "test-content".getBytes();
    InputStream is = new ByteArrayInputStream(content);

    Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
    Mockito.when(multipartFile.getInputStream()).thenReturn(is);

    String result = spyService.uploadDocument("user", "file.pdf", multipartFile);

    assertEquals("user/file.pdf", result);
    Mockito.verify(spyService).bucketExists();
    Mockito.verify(spyService, Mockito.never()).createBucket();
    Mockito.verify(minioClient).putObject(any(PutObjectArgs.class));
  }

  @Test
  void uploadDocument_whenBucketDoesNotExist_createsBucketAndUploads() throws Exception {
    MinIOServiceImpl spyService = Mockito.spy(new MinIOServiceImpl(minioClient));
    setField(spyService, "bucket", "test-bucket");
    setField(spyService, "uploadChunkSize", 5242881L);

    Mockito.doReturn(false).when(spyService).bucketExists();
    Mockito.doNothing().when(spyService).createBucket();

    byte[] content = "test-content".getBytes();
    InputStream is = new ByteArrayInputStream(content);

    Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
    Mockito.when(multipartFile.getInputStream()).thenReturn(is);

    String result = spyService.uploadDocument("user", "file.pdf", multipartFile);

    assertEquals("user/file.pdf", result);
    InOrder inOrder = Mockito.inOrder(spyService, minioClient);
    inOrder.verify(spyService).bucketExists();
    inOrder.verify(spyService).createBucket();
    inOrder.verify(minioClient).putObject(any(PutObjectArgs.class));
  }

  @Test
  void uploadDocument_whenFileInputStreamThrowsIOException_wrapsInResponseStatusException()
      throws Exception {
    MinIOServiceImpl spyService = Mockito.spy(new MinIOServiceImpl(minioClient));
    setField(spyService, "bucket", "test-bucket");
    setField(spyService, "uploadChunkSize", 5242881L);

    Mockito.doReturn(true).when(spyService).bucketExists();

    Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
    Mockito.when(multipartFile.getInputStream()).thenThrow(new IOException("io-error"));

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> spyService.uploadDocument("user", "file.pdf", multipartFile));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    assertEquals("io-error", ex.getReason());
  }

  @Test
  void uploadDocument_whenUnexpectedException_wrapsInResponseStatusException() throws Exception {
    MinIOServiceImpl spyService = Mockito.spy(new MinIOServiceImpl(minioClient));
    setField(spyService, "bucket", "test-bucket");
    setField(spyService, "uploadChunkSize", 5242881L);

    Mockito.doReturn(true).when(spyService).bucketExists();

    byte[] content = "test-content".getBytes();
    InputStream is = new ByteArrayInputStream(content);

    Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
    Mockito.when(multipartFile.getInputStream()).thenReturn(is);

    RuntimeException cause = new RuntimeException("unexpected-error");
    Mockito.when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(cause);

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> spyService.uploadDocument("user", "file.pdf", multipartFile));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    assertEquals("unexpected-error", ex.getReason());
  }
}
