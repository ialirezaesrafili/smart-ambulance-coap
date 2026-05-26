package com.ambulance.app;

import com.ambulance.server.CockpitServer;
import com.ambulance.server.RearCabinServer;
import com.ambulance.coordinator.DriverCoordinator;
import com.ambulance.coordinator.RelieverCoordinator;
import com.ambulance.coordinator.GpsCoordinator;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        // Start servers
        CockpitServer cockpit = new CockpitServer();
        RearCabinServer rearCabin = new RearCabinServer();
        cockpit.start();
        rearCabin.start();

        // Wait a moment for servers to be ready
        Thread.sleep(1000);

        // Create coordinators (using default ports and localhost)
        DriverCoordinator driver = new DriverCoordinator("localhost", 5683,
                "localhost", 5684);
        RelieverCoordinator reliever = new RelieverCoordinator("localhost", 5683,
                "localhost", 5684);
        GpsCoordinator gps = new GpsCoordinator("localhost", 5683,
                "localhost", 5684);

        // --- SCENARIO ---

        // 1. Driver sets temperature to 21°C in both cabins
        driver.setTemperatureBoth(21.0);

        // 2. Driver updates emergency info: level=HIGH, location=HOME
        driver.updateEmergencyInfoBoth(3, 4);

        // 3. Reliever tries to set cockpit temperature (should be rejected)
        reliever.attemptSetCockpitTemperature(18.0);

        // 4. Reliever sets rear cabin temperature to 19°C
        reliever.setRearCabinTemperature(19.0);

        // 5. Reliever updates emergency info: level=MEDIUM, location=PUBLIC_SPACE
        reliever.updateEmergencyInfoBoth(2, 3);

        // 6. GPS updates coordinates
        gps.updateGpsBoth("45.4642,9.1900");  // Milan example

        // Keep running for a while to observe, then stop
        System.out.println("\nAll operations performed. Press Ctrl+C to exit.");
        // In a real scenario, you might keep the main thread alive, but for the demo we can sleep a bit
        Thread.sleep(2000);

        // Shutdown
        cockpit.stop();
        rearCabin.stop();
        System.out.println("Servers stopped.");
    }
}