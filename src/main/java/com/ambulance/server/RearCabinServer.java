package com.ambulance.server;

import com.ambulance.enums.CabinType;
import com.ambulance.resource.TemperatureSensorResource;
import com.ambulance.resource.AirConditioningResource;
import com.ambulance.resource.HmiResource;

/**
 * CoAP server for the ambulance rear cabin (AR).
 * Hosts the three mandatory resources: temperature sensor (ST),
 * air‑conditioning controller (SA), and the HMI (SH).
 */
public class RearCabinServer extends BaseAmbulanceServer {

    /**
     * Default CoAP port for the rear cabin server.
     */
    public static final int DEFAULT_PORT = 5684;

    /**
     * Creates a new rear cabin server on the given port.
     */
    public RearCabinServer(int port) {
        super(CabinType.REAR_CABIN, port);
    }

    /**
     * Creates a rear cabin server on the default port 5684.
     */
    public RearCabinServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Registers the three CoAP resources for the rear cabin:
     * /temp – temperature sensor
     * /ac – air‑conditioning controller
     * /hmi – human‑machine interface
     */
    @Override
    protected void registerResources() {
        // 1. Temperature sensor (standalone resource)
        TemperatureSensorResource tempSensor = new TemperatureSensorResource("temp", cabinType);

        // 2. Air‑conditioning controller (linked to the sensor)
        AirConditioningResource ac = new AirConditioningResource("ac", cabinType, tempSensor);

        // 3. Human‑machine interface
        HmiResource hmi = new HmiResource("hmi", cabinType);

        // 4. Add all to the CoAP server
        coapServer.add(tempSensor);
        coapServer.add(ac);
        coapServer.add(hmi);
    }
}