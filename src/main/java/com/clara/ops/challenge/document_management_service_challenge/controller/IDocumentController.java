package com.clara.ops.challenge.document_management_service_challenge.controller;


import com.clara.ops.challenge.document_management_service_challenge.annotations.ErrorResponseAnnotations;
import com.clara.ops.challenge.document_management_service_challenge.annotations.PayloadTooLarge;
// Usually * imports are not good, but in this case, we are using every dto object here
import com.clara.ops.challenge.document_management_service_challenge.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentController {

    @Operation(
            summary = "Upload document endpoint",
            description = "This endpoints allows to upload a document with a user and a list of tags"
    )
    @ApiResponse(
            responseCode = "201", description = "The document was successfully uploaded",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DocumentDTO.class)
            )
    )
    @PayloadTooLarge
    @ErrorResponseAnnotations
    @RequestMapping(
            value = "/document/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    ResponseEntity<DocumentDTO> uploadDocument(
                @Parameter(in = ParameterIn.DEFAULT, description = "The document upload user name",
                        required = true, schema = @Schema()
                )
                @RequestParam(value = "user") String user,
                @Parameter(in = ParameterIn.DEFAULT, description = "The document filename",
                        required = true, schema = @Schema()
                )
                @RequestParam(value = "name") String name,
                @Parameter(in = ParameterIn.DEFAULT, description = "The document tags",
                        required = true, schema = @Schema()
                )
                @RequestParam(value = "tags") List<String> tags,
                @Parameter(in = ParameterIn.DEFAULT, description = "The file as a multipart binary data",
                        required = true, schema = @Schema()
                )
                @RequestParam(value = "file") MultipartFile file
            );

    @Operation(
            summary = "Document search endpoint",
            description = "This endpoint allows to perform a paginated search of documents" +
                    " by username, document name, and tags"
    )
    @ApiResponse(
            responseCode = "200", description = "Document(s) were successfully found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaginatedDocumentSearchResponse.class)
            )
    )
    @ErrorResponseAnnotations
    @RequestMapping(
            value = "/document/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    ResponseEntity<PaginatedDocumentSearchResponse> searchDocuments(
            @Parameter(in = ParameterIn.DEFAULT, description = "The document search request object",
                    required = true, schema = @Schema(implementation = PaginatedDocumentSearchResponse.class))
            @RequestBody DocumentSearchRequest searchRequest,
            @Parameter(in = ParameterIn.QUERY, description = "The page number (zero-based)",
            required = false, schema = @Schema(minimum = "0", defaultValue = "0"))
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(in = ParameterIn.QUERY, description = "The page size (0-N)",
            required = false, schema = @Schema(minimum = "0", defaultValue = "20"))
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size);


    @Operation(
            summary = "Document download endpoint",
            description = "This endpoint allows to download a document by it's ID," +
                    " it returns a temporary download URL to the storage location"
    )
    @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DocumentDownloadResponse.class)
            )
    )
    @ErrorResponseAnnotations
    @RequestMapping(
            value = "/document/download/{documentId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    ResponseEntity<DocumentDownloadResponse> downloadDocument(
            @Parameter(in = ParameterIn.PATH, description = "The document ID",
                    required = true, schema = @Schema())
            @PathVariable("documentId") Integer documentId);
}
