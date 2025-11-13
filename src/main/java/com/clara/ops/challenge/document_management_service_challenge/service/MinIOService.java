package com.clara.ops.challenge.document_management_service_challenge.service;

import java.io.InputStream;

public interface MinIOService {

    Boolean bucketExists(String bucket);

    String createBucket(String bucket);

    String getDocumentURL(String bucket, String documentPath);

    String uploadDocument(String bucket, String documentName, InputStream fileStream, Long partSize);
}
