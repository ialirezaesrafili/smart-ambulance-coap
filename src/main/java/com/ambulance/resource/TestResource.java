package com.ambulance.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;


public class TestResource extends CoapResource {
    public TestResource() {
        super("test");
    }
}