package com.ambulance.resource;

import com.ambulance.enums.CabinType;
import com.ambulance.enums.EmergencyLevel;
import com.ambulance.enums.EventLocation;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CoAP resource representing the Human‑Machine Interface (SH) of a cabin.
 * Stores the emergency level, location of the event, and current GPS
 * coordinates. Access rules for PUT:
 *   Driver or Reliever can update emergency level and event location.
 *   GPS client can update only the GPS coordinates.
 * The client's role is sent in the PUT payload as {@code "role"}.
 */
public class HmiResource extends CoapResource {

    private static final Logger LOG = LoggerFactory.getLogger(HmiResource.class);

    // Default values – you can change them later
    private EmergencyLevel emergencyLevel = EmergencyLevel.LOW;
    private EventLocation eventLocation = EventLocation.STREET;
    private String gpsCoordinates = "44.8015,10.3282";   // example GPS

    private final CabinType cabinType;

    /**
     * Creates a new HMI resource.
     * @param name      the URI path segment (e.g., "hmi")
     * @param cabinType the cabin this HMI belongs to
     */
    public HmiResource(String name, CabinType cabinType) {
        super(name);
        this.cabinType = cabinType;
        LOG.info("HMI '{}' created for cabin {}.", name, cabinType.getShortCode());
    }

    // -------------------------------------------------------------------
    // CoAP handlers
    // -------------------------------------------------------------------

    @Override
    public void handleGET(CoapExchange exchange) {
        String payload = toJson();
        exchange.respond(CoAP.ResponseCode.CONTENT, payload);
        LOG.debug("GET {} -> {}", getName(), payload);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        String payload = exchange.getRequestText();

        try {
            JSONObject json = new JSONObject(payload);
            String role = json.getString("role");

            // Case 1: Driver or Reliever updating emergency details
            if ("driver".equalsIgnoreCase(role) || "reliever".equalsIgnoreCase(role)) {
                int emergencyCode = json.getInt("emergency_level");
                int locationCode = json.getInt("event_location");

                EmergencyLevel newLevel = EmergencyLevel.fromCode(emergencyCode);
                EventLocation newLocation = EventLocation.fromCode(locationCode);

                this.emergencyLevel = newLevel;
                this.eventLocation = newLocation;

                LOG.info("PUT {}: Emergency info updated by {} in cabin {} -> level={}, location={}.",
                        getName(), role, cabinType.getShortCode(), newLevel, newLocation);

                exchange.respond(CoAP.ResponseCode.CHANGED, toJson());
                return;
            }

            // Case 2: GPS client updating coordinates only
            if ("gps".equalsIgnoreCase(role)) {
                String newGps = json.getString("gps_coordinates");
                this.gpsCoordinates = newGps;

                LOG.info("PUT {}: GPS updated by {} in cabin {} -> {}.",
                        getName(), role, cabinType.getShortCode(), newGps);

                exchange.respond(CoAP.ResponseCode.CHANGED, toJson());
                return;
            }

            // Unauthorised role
            LOG.warn("PUT {}: Unauthorised role '{}' attempted update in cabin {}.",
                    getName(), role, cabinType.getShortCode());
            exchange.respond(CoAP.ResponseCode.FORBIDDEN,
                    "{\"error\": \"Role '" + role + "' not allowed to modify this resource.\"}");

        } catch (Exception e) {
            LOG.error("Malformed PUT to {}: {}", getName(), e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST,
                    "{\"error\": \"Invalid JSON. Expected fields depend on role.\"}");
        }
    }

    // -------------------------------------------------------------------
    // Helper: build JSON from current state
    // -------------------------------------------------------------------

    private String toJson() {
        JSONObject json = new JSONObject();
        json.put("emergency_level", emergencyLevel.getCode());
        json.put("event_location", eventLocation.getCode());
        json.put("gps_coordinates", gpsCoordinates);
        // Include human‑readable labels for debugging / demo
        json.put("emergency_label", emergencyLevel.getLabel());
        json.put("location_label", eventLocation.getLabel());
        return json.toString();
    }

    // -------------------------------------------------------------------
    // Getters & Setters (for internal use or services)
    // -------------------------------------------------------------------

    public EmergencyLevel getEmergencyLevel() {
        return emergencyLevel;
    }

    public EventLocation getEventLocation() {
        return eventLocation;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public CabinType getCabinType() {
        return cabinType;
    }
}