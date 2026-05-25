package com.ambulance.app;

import com.ambulance.resource.TestResource;
import org.eclipse.californium.core.CoapServer;

import java.util.Scanner;

public class Application {
    /**
     *
     * Make an Orchestra
     * Make a server
     * Make a port for server
     * **/
    public static void main(String[] args) {

        CoapServer server = new CoapServer(5683);


        // Add resources here
        server.add(new TestResource());
        server.start();

        System.out.println("CoAP Server Started on port 5683");
        System.out.println("Type 'exit' to stop the server");

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("> ");


            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {

                System.out.println("Stopping server...");

                server.stop();
                System.exit(0);
                break;
            }

            System.out.println("Server still running...");
        }

        scanner.close();

        System.out.println("Application terminated");

    }

}