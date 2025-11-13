package com.clara.ops.challenge.document_management_service_challenge.exceptions;

import io.minio.errors.InternalException;
import io.minio.errors.MinioException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> badRequest(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.BAD_REQUEST),
                new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> unauthorized(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.UNAUTHORIZED),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> forbidden(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.FORBIDDEN),
                new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> notFound(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.NOT_FOUND),
                new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Object> conflict(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.CONFLICT),
                new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<Object> payloadTooLarge(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.PAYLOAD_TOO_LARGE),
                new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({MinioException.class, InternalException.class})
    public ResponseEntity<Object> internalServerError(Exception exception) {
        return new ResponseEntity<>(createErrorResponse(exception, HttpStatus.UNAUTHORIZED),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    private ErrorResponse createErrorResponse(Exception e, HttpStatus status){
        return ErrorResponse.builder().status(status.value()).error(status.name()).message(e.getMessage())
                .timestamp(LocalDateTime.now()).build();
    }

}
