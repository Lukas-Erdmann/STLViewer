package com.example.stlviewer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.example.stlviewer.control.STLViewerController;

public class TCPClientHandler extends Thread
{
    private final Socket clientSocket;
    private final STLViewerController stlViewerController;

    public TCPClientHandler(Socket clientSocket, STLViewerController stlViewerController) {
        this.clientSocket = clientSocket;
        this.stlViewerController = stlViewerController;
    }

    @Override
    public void run() {
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

            processCommands(input, output);

        } catch (IOException e) {
            System.err.println("Error processing client commands: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void processCommands(BufferedReader input, PrintWriter output) throws IOException {
        String command;
        while ((command = input.readLine()) != null) {
            String[] commandParts = command.split(" ");
            if (commandParts.length == 3) {
                try {
                    executeCommand(commandParts, output);
                    output.println("Executed command: " + command);
                } catch (Exception exception) {
                    output.println("Error executing command: " + exception.getMessage());
                    exception.printStackTrace();
                }
            } else {
                output.println("Invalid command: " + command + ". Expected format: <command> <axis> <amount>");
            }
        }
    }

    private void executeCommand(String[] commandParts, PrintWriter output) {
        String commandType = commandParts[0];
        String axis = commandParts[1];
        double amount;

        try {
            amount = Double.parseDouble(commandParts[2]);
        } catch (NumberFormatException numberFormatException) {
            output.println("Invalid value for amount: " + commandParts[2]);
            return;
        }

        switch (commandType) {
            case "translate":
                stlViewerController.translateModel(axis, amount);
                break;
            case "rotate":
                stlViewerController.rotateModel(axis, amount);
                break;
            default:
                output.println("Invalid command: " + commandType);
        }
    }
}
