package com.ambulance.enums;

/**
 * Represents the emergency severity level that can be set on the HMI.
 * The numeric codes match the project specification:
 * 1 = Low, 2 = Medium, 3 = High.
 */
public enum EmergencyLevel {

    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High");

    private final int code;
    private final String label;

    EmergencyLevel(int code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * @return the numeric code as specified (1,2,3)
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
     * Lookup an EmergencyLevel by its numeric code.
     *
     * @param code the emergency level code (1-3)
     * @return the matching level, or {@link #LOW} if not found
     */
    public static EmergencyLevel fromCode(int code) {
        for (EmergencyLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        return LOW; // safe default
    }

    /**
     * Checks if this emergency level is at least as severe as another.
     *
     * @param other the level to compare against
     * @return true if this.code >= other.code
     */
    public boolean isAtLeast(EmergencyLevel other) {
        return this.code >= other.code;
    }

    @Override
    public String toString() {
        return label + " (" + code + ")";
    }
}