package com.ambulance.resource;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

class AliveResource extends CoapResource {
    public AliveResource() {
        super("Alive");
    }
    @Override
    public Resource getParent() {
        return super.getParent();
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        exchange.respond("Server is alive!");
    }
}