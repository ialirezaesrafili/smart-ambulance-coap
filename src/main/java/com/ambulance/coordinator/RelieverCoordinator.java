package com.ambulance.coordinator;

import com.ambulance.client.Device;
import com.ambulance.client.RelieverClient;

/**
 * Orchestrates a Reliever's interactions.
 * Can set temperature ONLY in the rear cabin; can update both HMIs.
 */
public class RelieverCoordinator {

    private final RelieverClient rearCabinAc;
    private final RelieverClient cockpitHmi;
    private final RelieverClient rearCabinHmi;

    public RelieverCoordinator(String cockpitHost, int cockpitPort,
                               String rearCabinHost, int rearCabinPort) {
        // Reliever only allowed to set AC in rear cabin
        rearCabinAc = new RelieverClient(new Device("reliever-ac-AR", rearCabinHost, rearCabinPort, "ac"));
        // Both HMIs accessible
        cockpitHmi = new RelieverClient(new Device("reliever-hmi-AC", cockpitHost, cockpitPort, "hmi"));
        rearCabinHmi = new RelieverClient(new Device("reliever-hmi-AR", rearCabinHost, rearCabinPort, "hmi"));
    }

    /**
     * Sets temperature in the rear cabin only.
     */
    public void setRearCabinTemperature(double targetTemp) {
        System.out.println("[Reliever] Setting rear cabin temperature to " + targetTemp + "°C");
        String res = rearCabinAc.setTemperature(targetTemp);
        System.out.println("  RearCabin AC: " + res);
    }

    /**
     * Updates emergency info on both HMIs simultaneously.
     */
    public void updateEmergencyInfoBoth(int emergencyLevel, int eventLocation) {
        System.out.println("[Reliever] Updating emergency info: level=" + emergencyLevel + ", location=" + eventLocation);
        String resAc = cockpitHmi.updateHmi(emergencyLevel, eventLocation);
        String resAr = rearCabinHmi.updateHmi(emergencyLevel, eventLocation);
        System.out.println("  Cockpit HMI: " + resAc);
        System.out.println("  RearCabin HMI: " + resAr);
    }

    /**
     * Attempt to set cockpit temperature – should fail (FORBIDDEN).
     */
    public void attemptSetCockpitTemperature(double targetTemp) {
        System.out.println("[Reliever] Attempting to set cockpit temperature (should be rejected)");
        // Create a one‑off client targeting cockpit AC (role is still "reliever")
        RelieverClient cockpitAc = new RelieverClient(new Device("reliever-ac-AC", "localhost", 5683, "ac"));
        String res = cockpitAc.setTemperature(targetTemp);
        System.out.println("  Cockpit AC: " + res);
    }
}