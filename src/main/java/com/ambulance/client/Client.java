package com.ambulance.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.californium.core.CoapClient;

public abstract class Client {

    protected Device device;
    protected CoapClient client;

    // Abstract methods
    public abstract CoapClient getClient();

    public abstract void setDevice(Device device);

    public abstract void setClient(CoapClient client);

    public abstract String connect();

    public abstract String disconnect();

    // Common JSON method
    public ObjectNode toJson(String data) {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();

        root.put("deviceId",
                device.getDeviceId());

        root.put("ipAddress",
                device.getIpAddress());

        root.put("port",
                device.getPort());

        root.put("resource",
                device.getResource());

        root.put("data", data);

        return root;
    }
}