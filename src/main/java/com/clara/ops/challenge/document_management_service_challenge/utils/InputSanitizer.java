package com.clara.ops.challenge.document_management_service_challenge.utils;

import com.clara.ops.challenge.document_management_service_challenge.domain.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InputSanitizer {

    private static final Pattern ACCEPTED_CHARS = Pattern.compile("[^a-zA-Z0-9-_.]");

    public static String sanitizeString(String input){
        if(input == null) return input;
        input = input.trim().toLowerCase();
        return ACCEPTED_CHARS.matcher(input.replace(' ', '-')).replaceAll("-");
    }

    public static String sanitizeFileName(String filename){
        if(filename == null) {
            return null;
        };
        filename = filename.trim().toLowerCase();
        if (!filename.endsWith(".pdf")) {
              filename += ".pdf";
        }
        return ACCEPTED_CHARS.matcher(filename.replace(' ', '-')).replaceAll("-");
    }

    public static List<Tag> createAndSanitizeTags(List<String> tags){
        if(tags == null){
            return new ArrayList<>();
        }
        return tags.stream().filter(StringUtils::hasText)
                .map(InputSanitizer::sanitizeString)
                .map(Tag::new)
                .collect(Collectors.toList());
    }

    public static List<String> createAndSanitizeStringTags(List<String> tags){
        if(tags == null){
            return new ArrayList<>();
        }
        return tags.stream().filter(StringUtils::hasText)
                .map(InputSanitizer::sanitizeString)
                .collect(Collectors.toList());
    }
}
