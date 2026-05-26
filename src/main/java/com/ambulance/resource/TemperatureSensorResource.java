package com.ambulance.resource;

import com.ambulance.enums.CabinType;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CoAP resource representing the temperature sensor (ST) inside a cabin.
 * Accepts only GET requests, returning the current temperature as a JSON
 * object. The temperature can be updated externally (e.g., by the air‑conditioning
 * system simulation) to reflect the effect of the AC setpoint.
 */
public class TemperatureSensorResource extends CoapResource {

    private static final Logger LOG = LoggerFactory.getLogger(TemperatureSensorResource.class);

    private double currentTemperature;              // in Celsius
    private final CabinType cabinType;              // which cabin this sensor belongs to

    /**
     * Creates a temperature sensor resource.
     *
     * @param name      the URI path segment (e.g., "temp")
     * @param cabinType the cabin where the sensor is installed
     */
    public TemperatureSensorResource(String name, CabinType cabinType) {
        super(name);
        this.cabinType = cabinType;
        this.currentTemperature = 22.0;          // default ambient temperature

        // Mark as observable – clients can subscribe to temperature changes
        setObservable(true);

        LOG.info("TemperatureSensor '{}' created for cabin {}.", name, cabinType.getShortCode());
    }

    // -------------------------------------------------------------------
    // CoAP handlers
    // -------------------------------------------------------------------

    @Override
    public void handleGET(CoapExchange exchange) {
        // Simple JSON response: {"temperature": 22.5, "unit": "celsius"}
        String payload = String.format("{\"temperature\": %.1f, \"unit\": \"celsius\"}", currentTemperature);
        exchange.respond(CoAP.ResponseCode.CONTENT, payload);
        LOG.debug("GET {} -> temperature {} °C (cabin {}).", getName(), currentTemperature, cabinType.getShortCode());
    }

    // We do not override handlePUT / handlePOST because this sensor is read‑only.

    // -------------------------------------------------------------------
    // Temperature update (for simulation)
    // -------------------------------------------------------------------

    /**
     * Updates the current temperature.
     * Typically called by the temperature regulation service or the AC resource
     * to simulate how the cabin temperature reacts to the target setpoint.
     *
     * @param newTemperature the new temperature in Celsius
     */
    public void setTemperature(double newTemperature) {
        this.currentTemperature = newTemperature;
        LOG.trace("Temperature updated to {} °C (cabin {}).", newTemperature, cabinType.getShortCode());
        // Notify any observing CoAP clients
        changed();
    }

    // -------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public CabinType getCabinType() {
        return cabinType;
    }
}