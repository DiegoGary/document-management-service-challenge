package com.clara.ops.challenge.document_management_service_challenge.config;

import com.clara.ops.challenge.document_management_service_challenge.utils.DocumentToDTOConverter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentManagementConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addConverter(new DocumentToDTOConverter());
    return modelMapper;
  }

  @Bean
  public OpenAPI openAPIBean() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Clara Document Management Service API")
                .version("1.0.0")
                .description(
                    "This document service manages the file upload to a MinIO bucket "
                        + "with upload, search and download functionalities"));
  }
}
