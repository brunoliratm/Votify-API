package com.votify.exceptions;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(int lastPage) {
        super("Page not found, last page: " + lastPage);
    }
}
