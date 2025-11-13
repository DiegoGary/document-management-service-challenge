package com.clara.ops.challenge.document_management_service_challenge.controller.dto;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "The document data transfer object")
public class DocumentDTO {

    @JsonProperty("id")
    @Schema(description = "The document ID")
    private String id;

    @JsonProperty("user")
    @Schema(description = "The user that uploaded the document")
    private String user;

    @JsonProperty("name")
    @Schema(description = "The document filename")
    private String name;

    @JsonProperty("tags")
    @Schema(description = "The list of tags that were specified when uploading the document")
    private List<String> tags;

    @JsonProperty("size")
    @Schema(description = "The document file size")
    private Integer size;

    @JsonProperty("type")
    @Schema(description = "The document file type")
    private String type;

    @JsonProperty("createdAt")
    @Schema(description = "The date when the document was uploaded")
    private String createdAt;

}
