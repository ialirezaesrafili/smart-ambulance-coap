package com.ambulance.client;

public class GpsClient extends Client {

    public GpsClient(Device device) {
        super("gps", device);
    }

    /**
     * Update GPS coordinates on the HMI.
     */
    public String updateGps(String gpsCoordinates) {
        return put(baseJson().put("gps_coordinates", gpsCoordinates).toString());
    }

    /**
     * Read current HMI state (for verification).
     */
    public String getHmiState() {
        return get();
    }
}