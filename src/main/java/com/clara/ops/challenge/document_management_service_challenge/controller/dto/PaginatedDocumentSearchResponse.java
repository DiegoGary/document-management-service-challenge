package com.clara.ops.challenge.document_management_service_challenge.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(
    description =
        "The response for the document search endpoint with pagination data"
            + "containing the list of documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedDocumentSearchResponse {

  @JsonProperty("page")
  @Schema(example = "0", description = "The 0-based page number")
  private int page;

  @Schema(example = "15", description = "The number of items per page")
  @JsonProperty("size")
  private int size;

  @JsonProperty("totalPages")
  @Schema(example = "15", description = "The total number of pages")
  private int totalPages;

  @JsonProperty("numberOfDocuments")
  @Schema(example = "80", description = "The total number of documents that are returned")
  private long numberOfDocuments;

  @JsonProperty("documents")
  @Schema(description = "The list of documents")
  private List<DocumentDTO> documents;
}
