package com.ambulance.coordinator;

import com.ambulance.client.Device;
import com.ambulance.client.GpsClient;
import com.ambulance.util.JsonCliRenderer;

public class GpsCoordinator {

    private final GpsClient cockpitHmi;
    private final GpsClient rearCabinHmi;

    public GpsCoordinator(String cockpitHost, int cockpitPort,
                          String rearCabinHost, int rearCabinPort) {
        cockpitHmi = new GpsClient(new Device("gps-hmi-AC", cockpitHost, cockpitPort, "hmi"));
        rearCabinHmi = new GpsClient(new Device("gps-hmi-AR", rearCabinHost, rearCabinPort, "hmi"));
    }

    public void updateGpsBoth(String gpsCoordinates) {
        System.out.println("[GPS] Updating coordinates to: " + gpsCoordinates);
        String resAc = cockpitHmi.updateGps(gpsCoordinates);
        String resAr = rearCabinHmi.updateGps(gpsCoordinates);
        System.out.print("  Cockpit HMI: ");
        JsonCliRenderer.render(resAc);
        System.out.print("  RearCabin HMI: ");
        JsonCliRenderer.render(resAr);
    }
}