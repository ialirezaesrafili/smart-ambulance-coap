package com.ambulance.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract client representing an actor in the ambulance system.
 * Provides common CoAP operations and JSON payload building.
 */
public abstract class Client {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final String role;        // "driver", "reliever", "gps"
    protected Device device;
    protected CoapClient coapClient;

    public Client(String role, Device device) {
        this.role = role;
        this.device = device;
        this.coapClient = new CoapClient(device.getUri());
    }

    // -------------------------------------------------------------------
    // CoAP primitive operations
    // -------------------------------------------------------------------

    /**
     * Perform a GET and return the response text, or null on failure.
     */
    public String get() {
        try {
            CoapResponse response = coapClient.get();
            if (response != null && response.isSuccess()) {
                return response.getResponseText();
            }
            log.warn("GET {} failed: {}", device.getUri(),
                    response != null ? response.getCode() : "no response");
        } catch (Exception e) {
            log.error("GET {} exception: {}", device.getUri(), e.getMessage());
        }
        return null;
    }

    /**
     * Perform a PUT with a JSON payload string. Returns the response text or null.
     */
    public String put(String jsonPayload) {
        try {
            CoapResponse response = coapClient.put(jsonPayload, MediaTypeRegistry.APPLICATION_JSON);
            if (response != null && response.isSuccess()) {
                return response.getResponseText();
            }
            log.warn("PUT {} failed: {} (payload: {})", device.getUri(),
                    response != null ? response.getCode() : "no response", jsonPayload);
        } catch (Exception e) {
            log.error("PUT {} exception: {}", device.getUri(), e.getMessage());
        }
        return null;
    }

    /**
     * Convenience method to add the "role" field to any JSON object.
     */
    protected JSONObject baseJson() {
        JSONObject json = new JSONObject();
        json.put("role", role);
        return json;
    }

    // -------------------------------------------------------------------
    // Lifecycle helpers (optional)
    // -------------------------------------------------------------------

    public String connect() {
        // CoAP is connectionless, but we can test reachability with a GET
        String result = get();
        return (result != null) ? "Connected to " + device.getUri() : "Connection failed";
    }

    public String disconnect() {
        coapClient.shutdown();
        return "Disconnected";
    }

    // -------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------

    public String getRole() {
        return role;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
        this.coapClient = new CoapClient(device.getUri());
    }

    public CoapClient getCoapClient() {
        return coapClient;
    }

    @Override
    public String toString() {
        return role + " -> " + device;
    }
}