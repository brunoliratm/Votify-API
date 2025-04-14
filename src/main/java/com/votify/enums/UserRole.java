package com.votify.enums;

public enum UserRole {
    ADMIN("admin"),
    ORGANIZER("organizer"),
    ASSOCIATE("associate");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
