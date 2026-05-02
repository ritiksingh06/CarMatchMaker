package com.example.carmatchmaker.enums;

public enum Transmission {
    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    NO_PREFERENCE("No Preference");

    private final String displayName;

    Transmission(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
