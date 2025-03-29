package org.proiect.persistence.model.enums;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private String code;
    private Role(String code) {
        this.code = code;
    }
    public  String getCode() {
        return code;
    }
}
