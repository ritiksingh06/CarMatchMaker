package com.example.carmatchmaker.enums;

public enum MustHave {
    AUTOMATIC_TRANSMISSION("Automatic Transmission"),
    HIGH_SAFETY_RATING("High Safety Rating (4+)"),
    LARGE_BOOT("Large Boot Space (400L+)"),
    GOOD_MILEAGE("Good Mileage (18+ km/l)"),
    SUNROOF("Sunroof"),
    CONNECTED_FEATURES("Connected Car Features");

    private final String displayName;

    MustHave(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
