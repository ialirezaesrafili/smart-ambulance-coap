package com.ambulance.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceRegistry extends CoapResource {

    // Dynamic resource storage
    private final Map<String, JsonObject> resources = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    public ResourceRegistry(String name) {
        super(name);

        getAttributes().setTitle("Dynamic Resource Registry");
    }

    /**
     * GET RESOURCE
     */
    @Override
    public void handleGET(CoapExchange exchange) {

        String resourceId = exchange.getRequestOptions().getUriQuery().toString();

        // Return all resources
        if (resourceId.isEmpty()) {

            String json = gson.toJson(resources);

            exchange.respond(
                    CoAP.ResponseCode.CONTENT,
                    json,
                    MediaTypeRegistry.APPLICATION_JSON
            );

            return;
        }

        JsonObject resource = resources.get(resourceId);

        if (resource == null) {
            exchange.respond(CoAP.ResponseCode.NOT_FOUND, "Resource not found");
            return;
        }

        exchange.respond(
                CoAP.ResponseCode.CONTENT,
                gson.toJson(resource),
                MediaTypeRegistry.APPLICATION_JSON
        );
    }

    /**
     * CREATE RESOURCE
     */
    @Override
    public void handlePOST(CoapExchange exchange) {

        try {

            String payload = exchange.getRequestText();

            JsonObject json = gson.fromJson(payload, JsonObject.class);

            String id = json.get("id").getAsString();

            resources.put(id, json);

            exchange.respond(
                    CoAP.ResponseCode.CREATED,
                    "Resource created"
            );

        } catch (Exception e) {

            exchange.respond(
                    CoAP.ResponseCode.BAD_REQUEST,
                    "Invalid JSON"
            );
        }
    }

    /**
     * UPDATE RESOURCE
     */
    @Override
    public void handlePUT(CoapExchange exchange) {

        try {

            String payload = exchange.getRequestText();

            JsonObject json = gson.fromJson(payload, JsonObject.class);

            String id = json.get("id").getAsString();

            if (!resources.containsKey(id)) {

                exchange.respond(
                        CoAP.ResponseCode.NOT_FOUND,
                        "Resource not found"
                );

                return;
            }

            resources.put(id, json);

            exchange.respond(
                    CoAP.ResponseCode.CHANGED,
                    "Resource updated"
            );

        } catch (Exception e) {

            exchange.respond(
                    CoAP.ResponseCode.BAD_REQUEST,
                    "Invalid JSON"
            );
        }
    }

    /**
     * DELETE RESOURCE
     */
    @Override
    public void handleDELETE(CoapExchange exchange) {

        String id = exchange.getRequestText();

        JsonObject removed = resources.remove(id);

        if (removed == null) {

            exchange.respond(
                    CoAP.ResponseCode.NOT_FOUND,
                    "Resource not found"
            );

            return;
        }

        exchange.respond(
                CoAP.ResponseCode.DELETED,
                "Resource deleted"
        );
    }
}