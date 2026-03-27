package com.printer.myprinter.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN, USER, VIEWER;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) return null;
        return Role.valueOf(value.toUpperCase());
    }
}