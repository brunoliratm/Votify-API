package com.votify.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.votify.exceptions.ConflictException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.exceptions.ValidationErrorException;
import com.votify.models.CustomErrorResponse;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handlerUserNotFound(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<Map<String, Object>> handlerValidationError(ValidationErrorException ex) {
        logger.warn("Validation error: {}", ex.getErrors());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation error");
        response.put("errors", ex.getErrors());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorResponse> handlerConflict(ConflictException ex) {
        logger.warn("Conflict: {}", ex.getMessage());
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handlerGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);
        return new ResponseEntity<>(new CustomErrorResponse("Internal server error"), 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
