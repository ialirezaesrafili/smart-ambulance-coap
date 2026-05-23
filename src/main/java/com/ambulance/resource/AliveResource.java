package com.ambulance.resource;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

class AliveResource extends CoapResource {
    public AliveResource() {
        super("Alive");
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        exchange.respond("Server is alive!");
    }
}