package com.example.carmatchmaker.enums;

public enum UseCase {
    CITY_COMMUTE("City Commute"),
    FAMILY("Family"),
    HIGHWAY("Highway"),
    PERFORMANCE("Performance"),
    FIRST_CAR("First Car");

    private final String displayName;

    UseCase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
