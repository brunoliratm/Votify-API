package com.votify.exceptions;

public class StartDateBeforeEndDateException extends RuntimeException {
    public StartDateBeforeEndDateException() {
        super("Start date must be before end date");
    }
}
