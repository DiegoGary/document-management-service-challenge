package com.clara.ops.challenge.document_management_service_challenge.annotations;

import com.clara.ops.challenge.document_management_service_challenge.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@BadRequest
@UnauthorizedRequest
@ForbiddenRequest
@RequestNotFound
@RequestConflict
@InternalServerError
public @interface ErrorResponseAnnotations {
}
