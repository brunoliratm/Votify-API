package com.votify.handlers;

import com.votify.enums.SortSession;
import com.votify.exceptions.SessionNotFoundException;
import com.votify.exceptions.UserNotFoundException;
import com.votify.helpers.UtilHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.votify.exceptions.ValidationErrorException;
import com.votify.models.CustomErrorResponse;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final UtilHelper utilHelper = new UtilHelper();
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handlerValidationError(ValidationErrorException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation error");
        response.put("errors", ex.getErrors());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.add(error.getField() + ": " + error.getDefaultMessage())
        );

        response.put("message", "Validation error");
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = ex.getMessage();
        if (ex.getName().equals("direction")) {
            message = "Invalid direction value. Allowed values are: ASC, DESC.";
        }

        if (ex.getName().equals("sort")) {
            String enumValues = utilHelper.getEnumValues(Objects.requireNonNull(ex.getRequiredType()));
            message = "Invalid sort value. Allowed values are: " + enumValues;
        }

        CustomErrorResponse response = new CustomErrorResponse(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomErrorResponse> handlerUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomErrorResponse> handlerUserNotFound(SessionNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CustomErrorResponse> handlerGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);
        return new ResponseEntity<>(new CustomErrorResponse("An unknown error occurred"),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}