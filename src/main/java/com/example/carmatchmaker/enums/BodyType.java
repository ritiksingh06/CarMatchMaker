package com.example.carmatchmaker.enums;

public enum BodyType {
    HATCHBACK("Hatchback"),
    SEDAN("Sedan"),
    SUV("SUV"),
    COMPACT_SUV("Compact SUV"),
    MPV("MPV"),
    NO_PREFERENCE("No Preference");

    private final String displayName;

    BodyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
