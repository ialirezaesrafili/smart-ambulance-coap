package com.ambulance.server;

import com.ambulance.enums.CabinType;
import com.ambulance.resource.TemperatureSensorResource;
import com.ambulance.resource.AirConditioningResource;
import com.ambulance.resource.HmiResource;

/**
 * CoAP server for the ambulance cockpit (AC).
 * Hosts the three mandatory resources: temperature sensor (ST),
 * air‑conditioning controller (SA), and the HMI (SH).
 */
public class CockpitServer extends BaseAmbulanceServer {

    /**
     * Default CoAP port for the cockpit server.
     */
    public static final int DEFAULT_PORT = 5683;

    /**
     * Creates a new cockpit server on the given port.
     */
    public CockpitServer(int port) {
        super(CabinType.COCKPIT, port);
    }

    /**
     * Creates a cockpit server on the default port 5683.
     */
    public CockpitServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Registers the three CoAP resources for the cockpit:
     * /temp – temperature sensor
     * /ac – air‑conditioning controller
     * /hmi – human‑machine interface
     */
    @Override
    protected void registerResources() {
        // 1. Create the temperature sensor first (so the AC resource can link to it)
        TemperatureSensorResource tempSensor = new TemperatureSensorResource("temp", cabinType);

        // 2. Create the AC resource, passing the temperature sensor
        AirConditioningResource ac = new AirConditioningResource("ac", cabinType, tempSensor);

        // 3. Create the HMI resource
        HmiResource hmi = new HmiResource("hmi", cabinType);

        // 4. Add all three resources to the CoAP server
        coapServer.add(tempSensor);
        coapServer.add(ac);
        coapServer.add(hmi);
    }
}