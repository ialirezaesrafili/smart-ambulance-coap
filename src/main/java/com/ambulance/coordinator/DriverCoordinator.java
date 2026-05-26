package com.ambulance.coordinator;

import com.ambulance.client.Device;
import com.ambulance.client.DriverClient;

/**
 * Orchestrates a Driver's interactions with both cabins.
 * Simultaneously updates temperature in both ACs and emergency info on both HMIs.
 */
public class DriverCoordinator {

    private final DriverClient cockpitAc;
    private final DriverClient rearCabinAc;
    private final DriverClient cockpitHmi;
    private final DriverClient rearCabinHmi;

    /**
     * @param cockpitHost    e.g., "localhost"
     * @param cockpitPort    e.g., 5683
     * @param rearCabinHost  e.g., "localhost"
     * @param rearCabinPort  e.g., 5684
     */
    public DriverCoordinator(String cockpitHost, int cockpitPort,
                             String rearCabinHost, int rearCabinPort) {
        cockpitAc = new DriverClient(new Device("driver-ac-AC", cockpitHost, cockpitPort, "ac"));
        rearCabinAc = new DriverClient(new Device("driver-ac-AR", rearCabinHost, rearCabinPort, "ac"));
        cockpitHmi = new DriverClient(new Device("driver-hmi-AC", cockpitHost, cockpitPort, "hmi"));
        rearCabinHmi = new DriverClient(new Device("driver-hmi-AR", rearCabinHost, rearCabinPort, "hmi"));
    }

    /** Sets the target temperature in both cabins simultaneously. */
    public void setTemperatureBoth(double targetTemp) {
        System.out.println("[Driver] Setting temperature " + targetTemp + "°C in both cabins");
        String resAc = cockpitAc.setTemperature(targetTemp);
        String resAr = rearCabinAc.setTemperature(targetTemp);
        System.out.println("  Cockpit AC: " + resAc);
        System.out.println("  RearCabin AC: " + resAr);
    }

    /** Updates emergency level and event location on both HMIs simultaneously. */
    public void updateEmergencyInfoBoth(int emergencyLevel, int eventLocation) {
        System.out.println("[Driver] Updating emergency info: level=" + emergencyLevel + ", location=" + eventLocation);
        String resAc = cockpitHmi.updateHmi(emergencyLevel, eventLocation);
        String resAr = rearCabinHmi.updateHmi(emergencyLevel, eventLocation);
        System.out.println("  Cockpit HMI: " + resAc);
        System.out.println("  RearCabin HMI: " + resAr);
    }

    /** Optional: read current temperature from cockpit AC. */
    public String getCockpitTemperature() {
        return cockpitAc.getTargetTemperature();
    }

    /** Optional: read current temperature from rear cabin AC. */
    public String getRearCabinTemperature() {
        return rearCabinAc.getTargetTemperature();
    }
}