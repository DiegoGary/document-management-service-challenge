package com.clara.ops.challenge.document_management_service_challenge.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "The download URL for the document")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDownloadResponse {

    @JsonProperty("document_name")
    @Schema(description = "The document name")
    private String documentName;

    @JsonProperty("user")
    @Schema(description = "The user that uploaded the document")
    private String user;

    @JsonProperty("downloadURL")
    @Schema(description = "The MinIO document download url")
    private String downloadURL;

}
