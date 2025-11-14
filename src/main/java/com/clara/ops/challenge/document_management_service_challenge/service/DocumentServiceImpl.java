package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDownloadResponse;
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.domain.Document;

import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import com.clara.ops.challenge.document_management_service_challenge.domain.User;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.clara.ops.challenge.document_management_service_challenge.utils.InputSanitizer.*;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024;
    private final UserService userService;
    private final MinIOService minIOService;
    private final DocumentRepository documentRepository;
    private final TagRepository tagRepository;

    @Autowired
    public DocumentServiceImpl(UserService userService, MinIOService minIOService,
                               DocumentRepository documentRepository, TagRepository tagRepository){
        this.userService = userService;
        this.minIOService = minIOService;
        this.documentRepository = documentRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Document uploadDocument(String username, String name, List<String> tags, MultipartFile file) {
        log.info("Upload document with filename {}", name);
        if(!StringUtils.hasText(username) || !StringUtils.hasText(name)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Names cannot be null");
        }
        String filename = sanitizeFileName(name);
        validateDocument(file);
        List<Tag> tagList = createAndSanitizeTags(tags);
        User user = userService.validateAndCreateUser(sanitizeString(username));
        String filepath = minIOService.uploadDocument(user.getName(), filename, file);
        Document doc = new Document();
        doc.setUser(user);
        doc.setName(filename);
        doc.setTags(tagList);
        doc.setFileSize(file.getSize());
        doc.setFileType(file.getContentType());
        doc.setMinioPath(filepath);
        Document savedDocument = documentRepository.save(doc);
        tagList.forEach(t -> {
            t.setDocument(savedDocument);
            tagRepository.save(t);
        });
        return savedDocument;
    }

    @Override
    public Page<Document> searchDocuments(DocumentSearchRequest documentSearchRequest, Integer page, Integer size) {
        String username = sanitizeString(documentSearchRequest.getUser());
        String filename = sanitizeSearchFileName(documentSearchRequest.getName());
        List<String> tags = createAndSanitizeStringTags(documentSearchRequest.getTags());
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if(username != null && filename != null && !tags.isEmpty()){
            return documentRepository.findAllByUsernameAndNameAndTagName(username, filename, tags, pageable);
        } else if(username != null){
            if(filename != null) {
                return documentRepository.findAllByUsernameAndName(username, filename, pageable);
            } else if (!tags.isEmpty()) {
                return documentRepository.findAllByUsernameAndTags(username, tags, pageable);
            } else {
                return documentRepository.findAllByUsername(username, pageable);
            }
        } else if(filename != null){
           if (tags.isEmpty()) {
                return documentRepository.findAllByNameAndTags(filename, tags, pageable);
           } else {
                return documentRepository.findAllByName(filename, pageable);
           }
        } else if (!tags.isEmpty()){
            return documentRepository.findAllByTags(tags, pageable);
        }
        return documentRepository.findAll(pageable);
    }

    @Override
    public DocumentDownloadResponse getDocumentDownloadURL(Integer documentId) {
        Optional<Document> d = documentRepository.findById(documentId);
        if(d.isPresent()){
            Document doc = d.get();
            String path = minIOService.getDocumentURL(doc.getMinioPath());
            DocumentDownloadResponse response = new DocumentDownloadResponse();
            response.setDownloadURL(path);
            response.setDocumentName(doc.getName());
            response.setUser(doc.getUser().getName());
            return response;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document was not found");
    }

    private void validateDocument(MultipartFile file){
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        } else if(file.getSize() > MAX_FILE_SIZE){
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds maximum size");
        } else if(!Objects.equals(file.getContentType(), MediaType.APPLICATION_PDF_VALUE)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type not allowed");
        }
    }


}
