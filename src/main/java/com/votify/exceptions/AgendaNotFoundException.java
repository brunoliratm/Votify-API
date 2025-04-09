package com.votify.exceptions;

public class AgendaNotFoundException extends RuntimeException {
    public AgendaNotFoundException() {
        super("Agenda not found");
    }
}
