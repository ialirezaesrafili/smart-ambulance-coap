package com.ambulance.enums;

/**
 * Represents the type of location where an emergency event occurs.
 * Codes match the project specification:
 * 1 = Street, 2 = Workplace, 3 = Public Space, 4 = Home.
 */
public enum EventLocation {

    STREET(1, "Street"),
    WORKPLACE(2, "Workplace"),
    PUBLIC_SPACE(3, "Public Space"),
    HOME(4, "Home");

    private final int code;
    private final String label;

    EventLocation(int code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * @return the numeric code as per specification (1-4)
     */
    public int getCode() {
        return code;
    }

    /**
     * @return a human-readable label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Lookup an EventLocation by its numeric code.
     *
     * @param code the location code (1-4)
     * @return the matching location, or {@link #STREET} if not found
     */
    public static EventLocation fromCode(int code) {
        for (EventLocation location : values()) {
            if (location.code == code) {
                return location;
            }
        }
        return STREET; // safe default
    }

    @Override
    public String toString() {
        return label + " (" + code + ")";
    }
}