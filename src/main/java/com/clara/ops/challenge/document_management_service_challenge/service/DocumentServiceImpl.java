package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentSearchRequest;
import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import com.clara.ops.challenge.document_management_service_challenge.domain.User;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.ErrorResponse;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
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
import java.util.stream.Collectors;

import static com.clara.ops.challenge.document_management_service_challenge.utils.InputSanitizer.*;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024;
    private final UserService userService;
    private final MinIOService minIOService;
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(UserService userService, MinIOService minIOService, DocumentRepository documentRepository){
        this.userService = userService;
        this.minIOService = minIOService;
        this.documentRepository = documentRepository;
    }

    @Override
    public Document uploadDocument(String username, String name, List<String> tags, MultipartFile file) {
        if(!StringUtils.hasText(username) || !StringUtils.hasText(name)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Names cannot be null");
        }
        validateDocument(file);
        User user = userService.validateAndCreateUser(sanitizeString(username));
        Document doc = new Document();
        doc.setUser(user);
        doc.setName(sanitizeFileName(name));
        doc.setTags(createAndSanitizeTags(tags));
        doc.setFileSize(file.getSize());
        doc.setFileType(file.getContentType());
        return documentRepository.save(doc);
    }

    @Override
    public Page<Document> searchDocuments(DocumentSearchRequest documentSearchRequest, Integer page, Integer size) {
        String username = sanitizeString(documentSearchRequest.getUser());
        String filename = sanitizeFileName(documentSearchRequest.getName());
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
    public Document downloadDocument(Integer documentId) {
        return null;
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
