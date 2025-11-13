package com.clara.ops.challenge.document_management_service_challenge.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinIOServiceImpl implements  MinIOService {

    @Override
    public Boolean bucketExists(String bucket) {
        return null;
    }

    @Override
    public String createBucket(String bucket) {
        return "";
    }

    @Override
    public String getDocumentURL(String bucket, String documentPath) {
        return "";
    }

    @Override
    public String uploadDocument(String bucket, String documentName, InputStream fileStream, Long partSize) {
        return "";
    }
}
