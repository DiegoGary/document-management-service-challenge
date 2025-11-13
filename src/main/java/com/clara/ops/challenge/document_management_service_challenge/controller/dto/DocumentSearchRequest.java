package com.clara.ops.challenge.document_management_service_challenge.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "The document search request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchRequest {

    @JsonProperty("user")
    @Schema(description = "The user that uploaded the document for the search")
    private String user;

    @JsonProperty("name")
    @Schema(description = "The document name to search for")
    private String name;

    @JsonProperty("tags")
    @Schema(description = "The list of document tags to search for")
    private List<String> tags;
}
