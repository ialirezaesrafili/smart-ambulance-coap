package com.ambulance.app;

import com.ambulance.client.Client;
import com.ambulance.client.Device;
import com.ambulance.client.DriverClient;
import com.ambulance.server.CockpitServer;
import com.ambulance.server.RearCabinServer;
import com.ambulance.coordinator.DriverCoordinator;
import com.ambulance.coordinator.RelieverCoordinator;
import com.ambulance.coordinator.GpsCoordinator;
import com.ambulance.util.JsonCliRenderer;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONObject;

import java.util.Scanner;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * Main entry point for the Smart Ambulance CoAP simulation.
 * <p>
 * Starts both cabin servers (Cockpit and Rear Cabin) once,
 * then presents a menu allowing the user to run an automatic
 * multi‑phase emergency scenario, interact manually with
 * every CoAP resource, or exit the application.
 * </p>
 */
public class Application {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        // Enable coloured terminal output (Jansi)
        AnsiConsole.systemInstall();
        printBanner();

        // --- Start the two CoAP servers (run once for the whole session) ---
        info("SYSTEM", "Starting Smart Ambulance servers...");
        CockpitServer cockpit = new CockpitServer();
        RearCabinServer rearCabin = new RearCabinServer();
        cockpit.start();
        rearCabin.start();
        success("SERVER", "Cockpit server started");
        success("SERVER", "Rear cabin server started");
        Thread.sleep(1000);

        // --- Main menu loop ---
        int choice;
        do {
            choice = showModeMenu();
            switch (choice) {
                case 1 -> auto();          // automatic demonstration
                case 2 -> manual();        // interactive CoAP requests
                case 0 -> System.exit(0);  // immediate shutdown
            }
        } while (choice != 0);
    }

    // -----------------------------------------------------------------
    // Main menu
    // -----------------------------------------------------------------
    private static int showModeMenu() {
        System.out.println();
        System.out.println(ansi().bold().fg(YELLOW).a("Select operation mode:").reset());
        System.out.println("  " + ansi().fg(CYAN).a("[1]").reset() + " Automatic demonstration");
        System.out.println("  " + ansi().fg(CYAN).a("[2]").reset() + " Manual mode (interactive CoAP requests)");
        System.out.println("  " + ansi().fg(CYAN).a("[0]").reset() + " Exit");
        System.out.print(ansi().fg(GREEN).a("Your choice: ").reset());

        try {
            int c = Integer.parseInt(scanner.nextLine().trim());
            if (c < 0 || c > 2) {
                error("INPUT", "Invalid choice, try again.");
                return -1;   // loop back
            }
            return c;
        } catch (NumberFormatException e) {
            error("INPUT", "Please enter a number.");
            return -1;
        }
    }

    // -----------------------------------------------------------------
    // Automatic multi‑phase emergency scenario
    // -----------------------------------------------------------------
    private static void auto() throws InterruptedException {
        DriverCoordinator driver = new DriverCoordinator("localhost", 5683, "localhost", 5684);
        RelieverCoordinator reliever = new RelieverCoordinator("localhost", 5683, "localhost", 5684);
        GpsCoordinator gps = new GpsCoordinator("localhost", 5683, "localhost", 5684);

        // Phase 1 – Dispatch
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

        // Phase 2 – En Route
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
        driver.setTemperatureBoth(22.5);
        sleep(1);

        relieverAction("Setting rear cabin temperature to 22.0°C (comfort)");
        reliever.setRearCabinTemperature(22.0);
        sleep(1);

        relieverAction("Attempting to set cockpit temperature (FORBIDDEN)");
        reliever.attemptSetCockpitTemperature(20.0);
        sleep(1);

        info("READING", "Cockpit temperature: " + driver.getCockpitTemperature());
        info("READING", "Rear cabin temperature: " + driver.getRearCabinTemperature());
        sleep(1);

        // Phase 3 – On Scene
        section("PHASE 3 – ON SCENE");
        driverAction("Escalating to HIGH emergency, location PUBLIC SPACE");
        driver.updateEmergencyInfoBoth(3, 3);
        sleep(2);

        gpsAction("Arrived at scene");
        gps.updateGpsBoth("44.8100,10.3500");
        sleep(2);

        driverAction("Driver notes: victim critical");
        driver.updateEmergencyInfoBoth(3, 3);
        relieverAction("Reliever confirms emergency level HIGH");
        reliever.updateEmergencyInfoBoth(3, 3);
        sleep(1);

        relieverAction("Setting rear cabin to 20.5°C (cooler for treatment)");
        reliever.setRearCabinTemperature(20.5);
        driverAction("Driver sets cockpit to 23.0°C");
        driver.setTemperatureBoth(23.0);
        sleep(1);

        // Phase 4 – Transport
        section("PHASE 4 – TRANSPORT");
        driverAction("Departing scene, maintain HIGH emergency");
        driver.updateEmergencyInfoBoth(3, 4);
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

        // Phase 5 – Hospital Arrival
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

        info("READING", "Final cockpit temperature: " + driver.getCockpitTemperature());
        info("READING", "Final rear cabin temperature: " + driver.getRearCabinTemperature());

        section("SIMULATION COMPLETE");
    }

    // -----------------------------------------------------------------
    // Manual mode – interactive CoAP request builder
    // -----------------------------------------------------------------
    private static void manual() {
        section("MANUAL MODE");
        info("SYSTEM", "You can now send CoAP requests manually.");
        int action;
        do {
            System.out.println();
            System.out.println(ansi().bold().fg(YELLOW).a("Manual request menu:").reset());
            System.out.println("  " + ansi().fg(CYAN).a("[1]").reset() + " Cockpit (AC) resource");
            System.out.println("  " + ansi().fg(CYAN).a("[2]").reset() + " Rear Cabin (AR) resource");
            System.out.println("  " + ansi().fg(CYAN).a("[0]").reset() + " Back to main menu");
            System.out.print(ansi().fg(GREEN).a("Choose cabin: ").reset());

            try {
                action = Integer.parseInt(scanner.nextLine().trim());
                if (action < 0 || action > 2) {
                    error("INPUT", "Invalid choice.");
                    continue;
                }
                if (action == 0) break;

                int port = (action == 1) ? 5683 : 5684;
                String cabinName = (action == 1) ? "Cockpit" : "Rear Cabin";
                info("CABIN", "Selected " + cabinName + " (port " + port + ")");

                // Choose resource
                System.out.println(ansi().bold().fg(YELLOW).a("Choose resource:").reset());
                System.out.println("  " + ansi().fg(CYAN).a("[1]").reset() + " Temperature Sensor (temp)");
                System.out.println("  " + ansi().fg(CYAN).a("[2]").reset() + " Air Conditioning (ac)");
                System.out.println("  " + ansi().fg(CYAN).a("[3]").reset() + " HMI (hmi)");
                System.out.print(ansi().fg(GREEN).a("Resource: ").reset());
                int resChoice = Integer.parseInt(scanner.nextLine().trim());
                String resource = switch (resChoice) {
                    case 1 -> "temp";
                    case 2 -> "ac";
                    case 3 -> "hmi";
                    default -> throw new IllegalArgumentException("Invalid resource");
                };

                // For temp only GET, for ac and hmi ask GET or PUT
                boolean isGet = true;
                if (resChoice == 2 || resChoice == 3) {
                    System.out.println(ansi().bold().fg(YELLOW).a("Operation:").reset());
                    System.out.println("  " + ansi().fg(CYAN).a("[1]").reset() + " GET (read)");
                    System.out.println("  " + ansi().fg(CYAN).a("[2]").reset() + " PUT (update)");
                    System.out.print(ansi().fg(GREEN).a("Operation: ").reset());
                    int op = Integer.parseInt(scanner.nextLine().trim());
                    isGet = (op == 1);
                    if (op != 1 && op != 2) {
                        error("INPUT", "Invalid operation, defaulting to GET.");
                        isGet = true;
                    }
                }

                // Build request
                Device device = new Device("manual", "localhost", port, resource);
                Client client = new DriverClient(device);  // any concrete client works for raw get/put

                if (isGet) {
                    // GET request
                    String response = client.get();
                    System.out.print("Response: ");
                    JsonCliRenderer.render(response);
                } else {
                    // PUT request – build JSON payload
                    JSONObject payload = new JSONObject();

                    if (resource.equals("ac")) {
                        System.out.print(ansi().fg(GREEN).a("Temperature (°C): ").reset());
                        double temp = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print(ansi().fg(GREEN).a("Role (driver/reliever): ").reset());
                        String role = scanner.nextLine().trim().toLowerCase();
                        payload.put("target_temperature", temp);
                        payload.put("role", role);
                    } else if (resource.equals("hmi")) {
                        System.out.println(ansi().bold().fg(YELLOW).a("HMI PUT type:").reset());
                        System.out.println("  " + ansi().fg(CYAN).a("[1]").reset() + " Update emergency (role=driver/reliever)");
                        System.out.println("  " + ansi().fg(CYAN).a("[2]").reset() + " Update GPS (role=gps)");
                        System.out.print(ansi().fg(GREEN).a("Type: ").reset());
                        int hmiType = Integer.parseInt(scanner.nextLine().trim());
                        if (hmiType == 1) {
                            System.out.print(ansi().fg(GREEN).a("Role (driver/reliever): ").reset());
                            String role = scanner.nextLine().trim().toLowerCase();
                            System.out.print(ansi().fg(GREEN).a("Emergency level (1=Low,2=Medium,3=High): ").reset());
                            int level = Integer.parseInt(scanner.nextLine().trim());
                            System.out.print(ansi().fg(GREEN).a("Event location (1=Street,2=Workplace,3=Public Space,4=Home): ").reset());
                            int location = Integer.parseInt(scanner.nextLine().trim());
                            payload.put("role", role);
                            payload.put("emergency_level", level);
                            payload.put("event_location", location);
                        } else if (hmiType == 2) {
                            System.out.print(ansi().fg(GREEN).a("GPS coordinates (lat,lon): ").reset());
                            String coords = scanner.nextLine().trim();
                            payload.put("role", "gps");
                            payload.put("gps_coordinates", coords);
                        } else {
                            error("INPUT", "Invalid HMI type, aborting PUT.");
                            continue;
                        }
                    }

                    String response = client.put(payload.toString());
                    System.out.print("Response: ");
                    JsonCliRenderer.render(response);
                }

            } catch (NumberFormatException e) {
                error("INPUT", "Invalid number format. Try again.");
            } catch (IllegalArgumentException e) {
                error("INPUT", "Invalid choice. Try again.");
            } catch (Exception e) {
                error("ERROR", "Request failed: " + e.getMessage());
            }

        } while (true);

        info("SYSTEM", "Exiting manual mode.");
    }

    // -----------------------------------------------------------------
    // Utility: sleep for a given number of seconds
    // -----------------------------------------------------------------
    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================================================
    // CLI UI helpers (coloured output using Jansi)
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