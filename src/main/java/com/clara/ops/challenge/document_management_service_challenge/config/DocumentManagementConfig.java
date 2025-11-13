package com.clara.ops.challenge.document_management_service_challenge.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentManagementConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
