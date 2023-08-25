package com.miadowicz.githubrepoexplorer.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(UnsupportedAcceptHeaderException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedAcceptHeaderException(
            UnsupportedAcceptHeaderException ex, WebRequest request) {
        int statusCode = HttpStatus.NOT_ACCEPTABLE.value();

        MediaType acceptedMediaType = MediaType.parseMediaType(request.getHeader(HttpHeaders.ACCEPT));

        // Construct the error message with the reason
        String errorResponseMessage = String.format("Unsupported accept header: %s", acceptedMediaType);
        ErrorResponse errorResponse = new ErrorResponse(statusCode, errorResponseMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.status(statusCode).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
