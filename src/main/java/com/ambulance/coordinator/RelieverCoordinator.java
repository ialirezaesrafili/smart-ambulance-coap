package com.ambulance.coordinator;

import com.ambulance.client.Device;
import com.ambulance.client.RelieverClient;
import com.ambulance.util.JsonCliRenderer;

public class RelieverCoordinator {

    private final RelieverClient rearCabinAc;
    private final RelieverClient cockpitHmi;
    private final RelieverClient rearCabinHmi;

    public RelieverCoordinator(String cockpitHost, int cockpitPort,
                               String rearCabinHost, int rearCabinPort) {
        rearCabinAc = new RelieverClient(new Device("reliever-ac-AR", rearCabinHost, rearCabinPort, "ac"));
        cockpitHmi = new RelieverClient(new Device("reliever-hmi-AC", cockpitHost, cockpitPort, "hmi"));
        rearCabinHmi = new RelieverClient(new Device("reliever-hmi-AR", rearCabinHost, rearCabinPort, "hmi"));
    }

    public void setRearCabinTemperature(double targetTemp) {
        System.out.println("[Reliever] Setting rear cabin temperature to " + targetTemp + "°C");
        String res = rearCabinAc.setTemperature(targetTemp);
        System.out.print("  RearCabin AC: ");
        JsonCliRenderer.render(res);
    }

    public void updateEmergencyInfoBoth(int emergencyLevel, int eventLocation) {
        System.out.println("[Reliever] Updating emergency info: level="
                + emergencyLevel + ", location=" + eventLocation);
        String resAc = cockpitHmi.updateHmi(emergencyLevel, eventLocation);
        String resAr = rearCabinHmi.updateHmi(emergencyLevel, eventLocation);
        System.out.print("  Cockpit HMI: ");
        JsonCliRenderer.render(resAc);
        System.out.print("  RearCabin HMI: ");
        JsonCliRenderer.render(resAr);
    }

    public void attemptSetCockpitTemperature(double targetTemp) {
        System.out.println("[Reliever] Attempting to set cockpit temperature (should be rejected)");
        RelieverClient cockpitAc = new RelieverClient(
                new Device("reliever-ac-AC", "localhost", 5683, "ac"));
        String res = cockpitAc.setTemperature(targetTemp);
        System.out.print("  Cockpit AC: ");
        JsonCliRenderer.render(res);
    }
}