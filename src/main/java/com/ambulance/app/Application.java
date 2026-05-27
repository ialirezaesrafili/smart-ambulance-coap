package com.ambulance.app;

import com.ambulance.coordinator.DriverCoordinator;
import com.ambulance.coordinator.GpsCoordinator;
import com.ambulance.coordinator.RelieverCoordinator;
import com.ambulance.server.CockpitServer;
import com.ambulance.server.RearCabinServer;


public class Application {

    public static void main(String[] args) throws Exception {
        CockpitServer cockpit = new CockpitServer();
        RearCabinServer rearCabin = new RearCabinServer();
        cockpit.start();
        rearCabin.start();

        System.out.println("===========================================");
        System.out.println("Smart Ambulance CoAP servers are running.");
        System.out.println("Cockpit:   coap://localhost:5683");
        System.out.println("  /temp    - temperature sensor (GET)");
        System.out.println("  /ac      - air conditioning (GET/PUT)");
        System.out.println("  /hmi     - human machine interface (GET/PUT)");
        System.out.println("RearCabin: coap://localhost:5684");
        System.out.println("  /temp    - temperature sensor (GET)");
        System.out.println("  /ac      - air conditioning (GET/PUT)");
        System.out.println("  /hmi     - human machine interface (GET/PUT)");
        System.out.println("===========================================");

        // If --demo flag is passed, run the automatic scenario
        boolean demoMode = args.length > 0 && args[0].equals("--demo");
        if (demoMode) {
            System.out.println("Running automatic demonstration...\n");
            runDemoScenario();
            System.out.println("Demo completed. Servers still running.");
        } else {
            System.out.println("Server-only mode. Use a CoAP client to send requests.");
            System.out.println("Press Ctrl+C to stop.\n");
        }

        // Keep the main thread alive (servers run in daemon threads)
        Thread.currentThread().join();
    }

    private static void runDemoScenario() throws InterruptedException {
        DriverCoordinator driver = new DriverCoordinator("localhost", 5683, "localhost", 5684);
        RelieverCoordinator reliever = new RelieverCoordinator("localhost", 5683, "localhost", 5684);
        GpsCoordinator gps = new GpsCoordinator("localhost", 5683, "localhost", 5684);

        driver.setTemperatureBoth(21.0);
        driver.updateEmergencyInfoBoth(3, 4);
        reliever.attemptSetCockpitTemperature(18.0);
        reliever.setRearCabinTemperature(19.0);
        reliever.updateEmergencyInfoBoth(2, 3);
        gps.updateGpsBoth("45.4642,9.1900");

        System.out.println("\nAll automatic operations performed.");
    }
}