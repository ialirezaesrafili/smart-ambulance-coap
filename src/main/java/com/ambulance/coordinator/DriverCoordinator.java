package com.ambulance.coordinator;

import com.ambulance.client.Device;
import com.ambulance.client.DriverClient;
import com.ambulance.util.JsonCliRenderer;

public class DriverCoordinator {

    private final DriverClient cockpitAc;
    private final DriverClient rearCabinAc;
    private final DriverClient cockpitHmi;
    private final DriverClient rearCabinHmi;

    public DriverCoordinator(String cockpitHost, int cockpitPort,
                             String rearCabinHost, int rearCabinPort) {
        cockpitAc = new DriverClient(new Device("driver-ac-AC", cockpitHost, cockpitPort, "ac"));
        rearCabinAc = new DriverClient(new Device("driver-ac-AR", rearCabinHost, rearCabinPort, "ac"));
        cockpitHmi = new DriverClient(new Device("driver-hmi-AC", cockpitHost, cockpitPort, "hmi"));
        rearCabinHmi = new DriverClient(new Device("driver-hmi-AR", rearCabinHost, rearCabinPort, "hmi"));
    }

    public void setTemperatureBoth(double targetTemp) {
        System.out.println("[Driver] Setting temperature " + targetTemp + "°C in both cabins");
        String resAc = cockpitAc.setTemperature(targetTemp);
        String resAr = rearCabinAc.setTemperature(targetTemp);
        System.out.print("  Cockpit AC: ");
        JsonCliRenderer.render(resAc);
        System.out.print("  RearCabin AC: ");
        JsonCliRenderer.render(resAr);
    }

    public void updateEmergencyInfoBoth(int emergencyLevel, int eventLocation) {
        System.out.println("[Driver] Updating emergency info: level="
                + emergencyLevel + ", location=" + eventLocation);
        String resAc = cockpitHmi.updateHmi(emergencyLevel, eventLocation);
        String resAr = rearCabinHmi.updateHmi(emergencyLevel, eventLocation);
        System.out.print("  Cockpit HMI: ");
        JsonCliRenderer.render(resAc);
        System.out.print("  RearCabin HMI: ");
        JsonCliRenderer.render(resAr);
    }

    public String getCockpitTemperature() {
        return cockpitAc.getTargetTemperature();
    }

    public String getRearCabinTemperature() {
        return rearCabinAc.getTargetTemperature();
    }
}