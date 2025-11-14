package com.clara.ops.challenge.document_management_service_challenge.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class MinIOServiceImpl implements  MinIOService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.upload.size}")
    private Long uploadChunkSize;


    @Autowired
    public MinIOServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    @Override
    public Boolean bucketExists() {
        log.info("Checking for bucket {}", bucket);
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
        } catch (MinioException e){
            log.warn("MinioException when checking for bucket {}, e: {}", bucket, e.getMessage());
            throw e;
        } catch (Exception e){
            log.error("Undefined exception when checking for bucket, e: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @SneakyThrows
    @Override
    public void createBucket() {
        log.info("Creating bucket {}", bucket);
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Created bucket {}", bucket);
        } catch (Exception e){
            log.warn("Error creating bucket {} with exception e:{}", bucket, e.getMessage());
            throw e;
        }
    }

    @SneakyThrows
    @Override
    public String getDocumentURL(String documentPath) {
        log.info("Getting document url {} in bucket {}", documentPath, bucket);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET).bucket(bucket)
                    .object(documentPath).build());
        } catch (MinioException e) {
            log.warn("MinioException when getting document URL: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unspecified Exception occurred in method getDocumentURL: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public String uploadDocument(String user, String documentName, MultipartFile file) {
        log.info("Uploading document with name {}", documentName);
        if(!bucketExists()){
            createBucket();
        }
        String path = user + "/" + documentName;
        String fileType = file.getContentType();
        try (InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket).object(path)
                    .stream(inputStream, inputStream.available(), uploadChunkSize)
                    .contentType(fileType).build());
        } catch (IOException e){
            log.warn("Error reading file: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        } catch (MinioException e){
            log.warn("Error uploading file to Minio: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e){
            log.error("Unspecified error ocurred while uploading the file to Minio: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        return path;
    }
}
