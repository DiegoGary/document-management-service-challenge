package com.clara.ops.challenge.document_management_service_challenge.utils;

import com.clara.ops.challenge.document_management_service_challenge.controller.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.domain.entities.Tag;
import java.util.stream.Collectors;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class DocumentToDTOConverter implements Converter<Document, DocumentDTO> {
  @Override
  public DocumentDTO convert(MappingContext<Document, DocumentDTO> mappingContext) {
    Document source = mappingContext.getSource();
    DocumentDTO target = new DocumentDTO();
    target.setId(String.valueOf(source.getId()));
    target.setName(source.getName());
    target.setType(source.getFileType());
    target.setTags(
        source.getTags() != null
            ? source.getTags().stream().map(Tag::getTag).collect(Collectors.toList())
            : null);
    target.setUser(source.getUser().getName());
    target.setSize(Math.toIntExact(source.getFileSize()));
    target.setCreatedAt(String.valueOf(source.getCreatedAt()));
    return target;
  }
}
