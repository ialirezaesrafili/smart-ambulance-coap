package com.ambulance.enums;

/**
 * Represents the two operating environments inside the smart ambulance.
 * Used to distinguish between the cockpit and the rear cabin for
 * server instantiation, permission checks, and resource addressing.
 */
public enum CabinType {

    /**
     * The front cabin where the driver sits.
     */
    COCKPIT("Cockpit", "AC"),

    /**
     * The rear medical cabin where the reliever operates.
     */
    REAR_CABIN("Rear Cabin", "AR");

    private final String displayName;
    private final String shortCode;   // matches the project notation (AC, AR)

    CabinType(String displayName, String shortCode) {
        this.displayName = displayName;
        this.shortCode = shortCode;
    }

    /**
     * Human‑readable name of the cabin.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Short code used in project documentation (AC / AR).
     */
    public String getShortCode() {
        return shortCode;
    }

    /**
     * Checks if a temperature setpoint update is allowed from the given role.
     *
     * @param role the client role attempting the update (e.g., "driver", "reliever")
     * @return true if the role has authorization to change the temperature in this cabin
     */
    public boolean canSetTemperature(String role) {
        // Driver can set temperature in both cabins, Reliever only in REAR_CABIN
        if ("driver".equalsIgnoreCase(role)) {
            return true;
        }
        if ("reliever".equalsIgnoreCase(role)) {
            return this == REAR_CABIN;
        }
        return false;
    }

    @Override
    public String toString() {
        return displayName + " (" + shortCode + ")";
    }
}