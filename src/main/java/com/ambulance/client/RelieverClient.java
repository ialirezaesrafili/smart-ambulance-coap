package com.ambulance.client;

public class RelieverClient extends Client {

    public RelieverClient(Device device) {
        super("reliever", device);
    }

    // Same methods as DriverClient
    public String setTemperature(double targetTemp) {
        // Will only succeed if the device is in rear cabin
        return put(baseJson().put("target_temperature", targetTemp).toString());
    }

    public String updateHmi(int emergencyLevelCode, int eventLocationCode) {
        return put(baseJson().put("emergency_level", emergencyLevelCode)
                .put("event_location", eventLocationCode).toString());
    }

    public String getHmiState() {
        return get();
    }

    public String getTargetTemperature() {
        return get();
    }
}