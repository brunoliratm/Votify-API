package com.votify.exceptions;

import java.util.List;

public class ValidationErrorException extends RuntimeException {
    private final List<String> errors;

    public ValidationErrorException(List<String> errors) {
        super("Erro(s) de validação");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
