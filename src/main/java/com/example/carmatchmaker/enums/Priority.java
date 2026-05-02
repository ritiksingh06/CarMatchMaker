package com.example.carmatchmaker.enums;

public enum Priority {
    MILEAGE("Mileage"),
    SAFETY("Safety"),
    COMFORT("Comfort"),
    LOW_MAINTENANCE("Low Maintenance"),
    PERFORMANCE("Performance"),
    FEATURES("Features");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
