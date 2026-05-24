package com.ambulance.client;

public class Device {
    private String deviceId;
    private String ipAddress;
    private int port;
    private String resource;

    public Device(
            String deviceId,
            String ipAddress,
            int port,
            String resource) {

        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.port = port;
        this.resource = resource;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getUri() {
        return "coap://" +
                ipAddress +
                ":" +
                port +
                "/" +
                resource;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", resource='" + resource + '\'' +
                '}';
    }
}