package com.ambulance.resource;

import com.ambulance.enums.CabinType;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CoAP resource for the air‑conditioning controller (SA) in one cabin.
 * Handles GET (read current target temperature) and PUT (set a new target).
 * Access rules:
 * Driver can set temperature in both COCKPIT and REAR_CABIN.
 * Reliever can set temperature only in REAR_CABIN.
 * GPS client is not permitted to use this resource.
 * The client's role is expected in the PUT payload as a JSON field {@code "role"}.
 */
public class AirConditioningResource extends CoapResource {

    private static final Logger LOG = LoggerFactory.getLogger(AirConditioningResource.class);

    private double targetTemperature;               // target set by operator
    private final CabinType cabinType;
    private final TemperatureSensorResource temperatureSensor; // linked sensor

    /**
     * Creates the AC resource.
     *
     * @param name              URI path (e.g., "ac")
     * @param cabinType         the cabin this AC belongs to
     * @param temperatureSensor the temperature sensor in the same cabin
     */
    public AirConditioningResource(String name, CabinType cabinType,
                                   TemperatureSensorResource temperatureSensor) {
        super(name);
        this.cabinType = cabinType;
        this.temperatureSensor = temperatureSensor;
        this.targetTemperature = 22.0;   // default setpoint

        LOG.info("AirConditioning '{}' created for cabin {} (linked to sensor '{}').",
                name, cabinType.getShortCode(), temperatureSensor.getName());
    }

    // -------------------------------------------------------------------
    // CoAP handlers
    // -------------------------------------------------------------------

    @Override
    public void handleGET(CoapExchange exchange) {
        // Return the current target temperature as JSON
        String response = String.format("{\"target_temperature\": %.1f, \"unit\": \"celsius\"}",
                targetTemperature);
        exchange.respond(CoAP.ResponseCode.CONTENT, response);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        String payload = exchange.getRequestText();

        try {
            JSONObject json = new JSONObject(payload);
            double newTarget = json.getDouble("target_temperature");
            String role = json.getString("role");

            // Authorisation check using the CabinType's built-in logic
            if (!cabinType.canSetTemperature(role)) {
                LOG.warn("PUT rejected: role '{}' cannot set temperature in cabin {}.",
                        role, cabinType.getShortCode());
                exchange.respond(CoAP.ResponseCode.FORBIDDEN,
                        "{\"error\": \"Not allowed to change temperature in " +
                                cabinType.getShortCode() + "\"}");
                return;
            }

            // Apply the new target and update the physical sensor
            this.targetTemperature = newTarget;
            temperatureSensor.setTemperature(newTarget);   // immediate effect (can be gradual)

            LOG.info("PUT {}: target set to {} °C by {} (cabin {}).",
                    getName(), newTarget, role, cabinType.getShortCode());

            // Respond with success and the new state
            String response = String.format(
                    "{\"target_temperature\": %.1f, \"unit\": \"celsius\", \"status\": \"ok\"}",
                    targetTemperature);
            exchange.respond(CoAP.ResponseCode.CHANGED, response);

        } catch (Exception e) {
            LOG.error("Malformed PUT to {}: {}", getName(), e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST,
                    "{\"error\": \"Invalid JSON. Expected: {target_temperature: <float>, role: <string>}\"}");
        }
    }

    // -------------------------------------------------------------------
    // Getters (useful for services / HMI)
    // -------------------------------------------------------------------

    public double getTargetTemperature() {
        return targetTemperature;
    }

    public CabinType getCabinType() {
        return cabinType;
    }
}