package com.example.stlviewer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient
{
    private final String host;
    private final int port;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        System.out.println("Connecting to server: " + host + ":" + port);
        try (Socket socket = new Socket(host, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            handleUserInput(output, scanner);
        } catch (IOException ioException) {
            System.err.println("Error connecting to server: " + ioException.getMessage());
        }
    }

    private void handleUserInput(PrintWriter output, Scanner scanner) {
        while (true) {
            String commandType = fetchCommandType(scanner);
            String axis = fetchAxis(scanner);
            double amount = fetchAmount(scanner);

            String command = commandType + " " + axis + " " + amount;
            output.println(command);
        }
    }

    private String fetchCommandType(Scanner scanner) {
        while (true) {
            System.out.print("Enter command type (rotate, translate): ");
            String commandType = scanner.nextLine().trim().toLowerCase();
            if (commandType.equals("rotate") || commandType.equals("translate")) {
                return commandType;
            } else {
                System.out.println("Invalid command type. Please enter rotate or translate.");
            }
        }
    }

    private String fetchAxis(Scanner scanner) {
        while (true) {
            System.out.print("Enter axis (x, y, z): ");
            String axis = scanner.nextLine().trim().toLowerCase();
            if (axis.equals("x") || axis.equals("y") || axis.equals("z")) {
                return axis;
            } else {
                System.out.println("Invalid axis. Please enter x, y, or z.");
            }
        }
    }

    private double fetchAmount(Scanner scanner) {
        while (true) {
            System.out.print("Enter amount: ");
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }
}
