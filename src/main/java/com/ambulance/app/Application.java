package com.ambulance.app;

import org.eclipse.californium.core.CoapServer;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        CoapServer server = new CoapServer(5683);

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