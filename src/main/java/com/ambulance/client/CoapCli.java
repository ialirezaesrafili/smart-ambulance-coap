package com.ambulance.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class CoapCli extends Client {

    public CoapCli(Device device) {

        // Create CoAP client using device URI
        super(device, new CoapClient(device.getUri()));
    }

    @Override
    public CoapClient getClient() {
        return client;
    }

    @Override
    public void setDevice(Device device) {
        this.device = device;
        this.client = new CoapClient(device.getUri());
    }


    @Override
    public void setClient(CoapClient client) {
        this.client = client;
    }

    @Override
    public String connect() {

        try {

            CoapResponse response = client.get();

            if (response != null) {
                return "Connected: " + response.getResponseText();
            }

            return "No response from device";

        } catch (Exception e) {

            return "Connection failed: " + e.getMessage();
        }
    }

    @Override
    public String disconnect() {

        try {

            client.shutdown();

            return "Disconnected successfully";

        } catch (Exception e) {

            return "Disconnect failed: " + e.getMessage();
        }
    }
}