package com.ambulance.app;

import com.ambulance.server.CockpitServer;
import com.ambulance.server.RearCabinServer;
import com.ambulance.coordinator.DriverCoordinator;
import com.ambulance.coordinator.RelieverCoordinator;
import com.ambulance.coordinator.GpsCoordinator;
import com.ambulance.util.JsonCliRenderer;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class Application {

    public static void main(String[] args) throws InterruptedException {

        AnsiConsole.systemInstall();
        printBanner();

        info("SYSTEM", "Starting Smart Ambulance servers...");

        CockpitServer cockpit = new CockpitServer();
        RearCabinServer rearCabin = new RearCabinServer();
        cockpit.start();
        rearCabin.start();

        success("SERVER", "Cockpit server started");
        success("SERVER", "Rear cabin server started");
        Thread.sleep(1000);

        // Coordinators
        DriverCoordinator driver = new DriverCoordinator("localhost", 5683, "localhost", 5684);
        RelieverCoordinator reliever = new RelieverCoordinator("localhost", 5683, "localhost", 5684);
        GpsCoordinator gps = new GpsCoordinator("localhost", 5683, "localhost", 5684);

        // ------------------------------------------------------------------
        // PHASE 1 – Dispatch & Initialisation
        // ------------------------------------------------------------------
        section("PHASE 1 – DISPATCH");
        driverAction("Setting initial cabin temperatures to 21°C");
        driver.setTemperatureBoth(21.0);
        sleep(1);

        driverAction("Setting emergency: LOW / STREET");
        driver.updateEmergencyInfoBoth(1, 1);
        sleep(1);

        gpsAction("Initial GPS: Parma city centre");
        gps.updateGpsBoth("44.8015,10.3282");
        sleep(1);

        // ------------------------------------------------------------------
        // PHASE 2 – En Route (escalation)
        // ------------------------------------------------------------------
        section("PHASE 2 – EN ROUTE");
        driverAction("Raising emergency to MEDIUM, location WORKPLACE");
        driver.updateEmergencyInfoBoth(2, 2);
        sleep(2);

        gpsAction("Moving along Via Emilia");
        gps.updateGpsBoth("44.8040,10.3350");
        sleep(2);
        gps.updateGpsBoth("44.8065,10.3420");
        sleep(2);

        driverAction("Increasing cockpit temperature to 22.5°C");
        driver.setTemperatureBoth(22.5);   // driver sets both
        sleep(1);

        relieverAction("Setting rear cabin temperature to 22.0°C (comfort)");
        reliever.setRearCabinTemperature(22.0);
        sleep(1);

        relieverAction("Attempting to set cockpit temperature (FORBIDDEN)");
        reliever.attemptSetCockpitTemperature(20.0);
        sleep(1);

        // Read current temperatures for verification
        info("READING", "Cockpit temperature: " + driver.getCockpitTemperature());
        info("READING", "Rear cabin temperature: " + driver.getRearCabinTemperature());
        sleep(1);

        // ------------------------------------------------------------------
        // PHASE 3 – On Scene (HIGH emergency)
        // ------------------------------------------------------------------
        section("PHASE 3 – ON SCENE");
        driverAction("Escalating to HIGH emergency, location PUBLIC SPACE");
        driver.updateEmergencyInfoBoth(3, 3);
        sleep(2);

        gpsAction("Arrived at scene");
        gps.updateGpsBoth("44.8100,10.3500");
        sleep(2);

        // Driver and reliever both update HMI simultaneously
        driverAction("Driver notes: victim critical");
        driver.updateEmergencyInfoBoth(3, 3); // re-affirm
        relieverAction("Reliever confirms emergency level HIGH");
        reliever.updateEmergencyInfoBoth(3, 3);
        sleep(1);

        // Temperature adjustment while stationary
        relieverAction("Setting rear cabin to 20.5°C (cooler for treatment)");
        reliever.setRearCabinTemperature(20.5);
        driverAction("Driver sets cockpit to 23.0°C");
        driver.setTemperatureBoth(23.0);
        sleep(1);

        // ------------------------------------------------------------------
        // PHASE 4 – Transport to Hospital
        // ------------------------------------------------------------------
        section("PHASE 4 – TRANSPORT");
        driverAction("Departing scene, maintain HIGH emergency");
        driver.updateEmergencyInfoBoth(3, 4);  // location now HOME? Not accurate, but demonstrates.
        // Actually better: location = 2 (workplace) or 4 (home) – let's use 4 for hospital vicinity
        gpsAction("Moving towards Ospedale Maggiore");
        gps.updateGpsBoth("44.8120,10.3550");
        sleep(2);
        gps.updateGpsBoth("44.8135,10.3600");
        sleep(2);
        gps.updateGpsBoth("44.8150,10.3650");
        sleep(2);

        driverAction("Reducing emergency to MEDIUM as patient stabilizes");
        driver.updateEmergencyInfoBoth(2, 4);
        sleep(1);

        relieverAction("Adjusting rear cabin temperature to 21.5°C");
        reliever.setRearCabinTemperature(21.5);
        sleep(1);

        // ------------------------------------------------------------------
        // PHASE 5 – Hospital Arrival & Handover
        // ------------------------------------------------------------------
        section("PHASE 5 – HOSPITAL ARRIVAL");
        gpsAction("Arrived at hospital");
        gps.updateGpsBoth("44.8165,10.3700");
        sleep(2);

        driverAction("Downgrading to LOW emergency, location HOME");
        driver.updateEmergencyInfoBoth(1, 4);
        sleep(1);

        relieverAction("Setting rear cabin temperature back to 22.0°C");
        reliever.setRearCabinTemperature(22.0);
        driverAction("Setting cockpit temperature back to 21.5°C");
        driver.setTemperatureBoth(21.5);
        sleep(1);

        // Final sensor check
        info("READING", "Final cockpit temperature: " + driver.getCockpitTemperature());
        info("READING", "Final rear cabin temperature: " + driver.getRearCabinTemperature());

        section("SIMULATION COMPLETE");
        info("SYSTEM", "Press Ctrl+C to exit.");
        Thread.currentThread().join();  // keep alive
    }

    // ------------------------------------------------------------------
    // Utility: short sleep with message
    // ------------------------------------------------------------------
    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================================================
    // UI Helpers (unchanged from original)
    // =========================================================

    private static void printBanner() {
        String art = """
                
                ███████╗███╗   ███╗ █████╗ ██████╗ ████████╗     █████╗ ███╗   ███╗██████╗ ██╗   ██╗██╗      █████╗ ███╗   ██╗ ██████╗███████╗    ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗
                ██╔════╝████╗ ████║██╔══██╗██╔══██╗╚══██╔══╝    ██╔══██╗████╗ ████║██╔══██╗██║   ██║██║     ██╔══██╗████╗  ██║██╔════╝██╔════╝    ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║
                ╚█████╗ ██╔████╔██║███████║██████╔╝   ██║       ███████║██╔████╔██║██████╔╝██║   ██║██║     ███████║██╔██╗ ██║██║     █████╗      ███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║
                 ╚═══██╗██║╚██╔╝██║██╔══██║██╔══██╗   ██║       ██╔══██║██║╚██╔╝██║██╔══██╗██║   ██║██║     ██╔══██║██║╚██╗██║██║     ██╔══╝      ╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║
                ██████╔╝██║ ╚═╝ ██║██║  ██║██║  ██║   ██║       ██║  ██║██║ ╚═╝ ██║██████╔╝╚██████╔╝███████╗██║  ██║██║ ╚████║╚██████╗███████╗    ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║
                ╚═════╝ ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝       ╚═╝  ╚═╝╚═╝     ╚═╝╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝╚══════╝    ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝
                
                """;

        System.out.println(ansi().fg(BLUE).bold().a(art).reset());
    }

    private static void section(String title) {
        System.out.println(ansi().fg(MAGENTA).bold().a("\n========== " + title + " ==========\n").reset());
    }

    private static void info(String tag, String msg) {
        System.out.println(ansi().fg(CYAN).a("[" + tag + "] ").reset().a(msg));
    }

    private static void success(String tag, String msg) {
        System.out.println(ansi().fg(GREEN).bold().a("[" + tag + "] ").reset().a(msg));
    }

    private static void error(String tag, String msg) {
        System.out.println(ansi().fg(RED).bold().a("[" + tag + "] ").reset().a(msg));
    }

    private static void driverAction(String msg) {
        System.out.println(ansi().fg(CYAN).bold().a("[DRIVER] ").reset().a(msg));
    }

    private static void relieverAction(String msg) {
        System.out.println(ansi().fg(YELLOW).bold().a("[RELIEVER] ").reset().a(msg));
    }

    private static void gpsAction(String msg) {
        System.out.println(ansi().fg(GREEN).bold().a("[GPS] ").reset().a(msg));
    }
}