package com.ambulance.client;

import org.json.JSONObject;

public class DriverClient extends Client {

    public DriverClient(Device device) {
        super("driver", device);
    }

    /**
     * Set the target temperature in a specific cabin.
     */
    public String setTemperature(double targetTemp) {
        JSONObject json = baseJson();
        json.put("target_temperature", targetTemp);
        return put(json.toString());
    }

    /**
     * Update emergency info on the HMI.
     */
    public String updateHmi(int emergencyLevelCode, int eventLocationCode) {
        JSONObject json = baseJson();
        json.put("emergency_level", emergencyLevelCode);
        json.put("event_location", eventLocationCode);
        return put(json.toString());
    }

    /**
     * Read current HMI state.
     */
    public String getHmiState() {
        return get();
    }

    /**
     * Read current target temperature.
     */
    public String getTargetTemperature() {
        return get();
    }
}