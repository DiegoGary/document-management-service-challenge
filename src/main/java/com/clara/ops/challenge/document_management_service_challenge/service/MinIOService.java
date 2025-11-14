package com.clara.ops.challenge.document_management_service_challenge.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinIOService {

    Boolean bucketExists();

    void createBucket();

    String getDocumentURL(String documentPath);

    String uploadDocument(String user, String documentName, MultipartFile file);
}
