package com.ambulance.server;

import com.ambulance.enums.CabinType;
import com.ambulance.enums.ServerState;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.UdpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract base for any ambulance cabin CoAP server.
 * <p>
 * Holds the Californium {@link CoapServer}, the cabin type, and a
 * thread-safe {@link ServerState}. Subclasses must implement
 * {@link #registerResources()} to add their specific resources.
 * </p>
 */
public abstract class BaseAmbulanceServer {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAmbulanceServer.class);

    protected final CabinType cabinType;
    protected final CoapServer coapServer;
    protected final int port;

    // Thread-safe server state
    private final AtomicReference<ServerState> state =
            new AtomicReference<>(ServerState.CREATED);

    /**
     * Creates the base server for a given cabin.
     *
     * @param cabinType the physical cabin (COCKPIT or REAR_CABIN)
     * @param port      the CoAP port to bind to
     */
    protected BaseAmbulanceServer(CabinType cabinType, int port) {
        this.cabinType = cabinType;
        this.port = port;

        // Build a Californium configuration (can also load from file)
        Configuration config = Configuration.createStandardWithoutFile();
        config.set(CoapConfig.COAP_PORT, port);
        config.set(UdpConfig.UDP_CONNECTOR_OUT_CAPACITY, 100);

        // Create the CoAP server instance
        this.coapServer = new CoapServer(config);
    }

    // -------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------

    /**
     * Starts the server: transitions to STARTING, registers resources,
     * starts the Californium server, and finally sets state to RUNNING.
     *
     * @throws IllegalStateException if not in CREATED or STOPPED state
     */
    public final synchronized void start() {
        if (state.get() != ServerState.CREATED && state.get() != ServerState.STOPPED) {
            throw new IllegalStateException(
                    "Can only start from CREATED or STOPPED, current state: " + state.get());
        }

        transitionTo(ServerState.STARTING);
        LOG.info("{} server starting on port {}...", cabinType.getShortCode(), port);

        try {
            // Let the subclass register its resources
            registerResources();

            // Start the underlying CoAP server (non-blocking)
            coapServer.start();

            transitionTo(ServerState.RUNNING);
            LOG.info("{} server running on port {}.", cabinType.getShortCode(), port);
        } catch (Exception e) {
            LOG.error("Failed to start {} server", cabinType.getShortCode(), e);
            transitionTo(ServerState.ERROR);
        }
    }

    /**
     * Stops the server: transitions to STOPPING, shuts down CoAP server,
     * then sets state to STOPPED.
     */
    public final synchronized void stop() {
        if (state.get() != ServerState.RUNNING) {
            LOG.warn("Server is not running, current state: {}. Ignoring stop.", state.get());
            return;
        }

        transitionTo(ServerState.STOPPING);
        LOG.info("{} server stopping...", cabinType.getShortCode());

        coapServer.stop();

        transitionTo(ServerState.STOPPED);
        LOG.info("{} server stopped.", cabinType.getShortCode());
    }

    /**
     * Subclasses must add their CoAP resources (Temperature, AC, HMI) here.
     * Called during {@link #start()}.
     */
    protected abstract void registerResources();

    // -------------------------------------------------------------------
    // State management
    // -------------------------------------------------------------------

    /**
     * Thread-safe state transition with logging.
     */
    private void transitionTo(ServerState newState) {
        ServerState old = state.getAndSet(newState);
        LOG.debug("State: {} → {}", old.getMessage(), newState.getMessage());
    }

    public ServerState getState() {
        return state.get();
    }

    public boolean isRunning() {
        return state.get().isOperational();
    }

    // -------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------

    public CabinType getCabinType() {
        return cabinType;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getAddress() {
        for (var endpoint : coapServer.getEndpoints()) {
            if (endpoint.getAddress() != null) {
                return endpoint.getAddress();
            }
        }
        return null; // no active endpoint yet
    }

    // Optional: access to the underlying CoapServer for advanced use
    public CoapServer getCoapServer() {
        return coapServer;
    }
}