package com.example.stlviewer.util.network;

import com.example.stlviewer.res.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The TCPClient class handles communication with the TCP server.
 * It allows the user to send commands to the server to manipulate a polyhedron.
 */
public class TCPClient
{
    /**
     * The host name or IP address of the server.
     */
    private final String host;
    /**
     * The port number of the server.
     */
    private final int port;

    /**
     * Constructs a TCPClient with the specified host and port.
     * Precondition: host should be a valid server address and port should be a valid port number.
     * Postcondition: A TCPClient instance is created with the given host and port.
     *
     * @param host - The host name or IP address of the server.
     * @param port - The port number of the server.
     */
    public TCPClient (String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Starts the TCP client and handles user input to send commands to the server.
     * Precondition: The server should be running and accessible.
     * Postcondition: Commands are sent to the server based on user input.
     */
    public void start ()
    {
        System.out.printf(Strings.CONNECTING_TO_SERVER_AT, host, port);
        try (Socket socket = new Socket(host, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in))
        {

            handleUserInput(output, scanner);
        } catch (IOException ioException)
        {
            System.err.println(Strings.FAILED_CONNECTING_TO_SERVER + ioException.getMessage());
        }
    }

    /**
     * Handles user input to create and send commands to the server.
     * Precondition: The output stream and scanner should be initialized.
     * Postcondition: User input is read and commands are sent to the server.
     *
     * @param output  - The PrintWriter for server output.
     * @param scanner - The Scanner for user input.
     */
    private void handleUserInput (PrintWriter output, Scanner scanner)
    {
        while (true)
        {
            String commandType = fetchCommandType(scanner);
            String axis = fetchAxis(scanner, commandType);
            double amount = fetchAmount(scanner);

            String command = commandType + Strings.SPACE + axis + Strings.SPACE + amount;
            output.println(command);
        }
    }

    /**
     * Fetches the command type from the user input.
     * Precondition: The scanner should be initialized.
     * Postcondition: Returns a valid command type entered by the user.
     *
     * @param scanner - The Scanner for user input.
     * @return A valid command type.
     */
    private String fetchCommandType (Scanner scanner)
    {
        while (true)
        {
            System.out.print(Strings.ENTER_COMMAND_TYPE);
            String commandType = scanner.nextLine().trim().toLowerCase();
            if (commandType.equals(Strings.COMMAND_TYPE_ROTATE) || commandType.equals(Strings.COMMAND_TYPE_TRANSLATE))
            {
                return commandType;
            } else
            {
                System.out.println(Strings.INVALID_COMMAND_TYPE);
            }
        }
    }

    /**
     * Fetches the axis from the user input.
     * Precondition: The scanner should be initialized.
     * Postcondition: Returns a valid axis entered by the user.
     *
     * @param scanner     - The Scanner for user input.
     * @param commandType - The type of command (rotate or translate).
     * @return A valid axis.
     */
    private String fetchAxis (Scanner scanner, String commandType)
    {
        while (true)
        {
            System.out.print(Strings.ENTER_AXIS);
            String axis = scanner.nextLine().trim().toLowerCase();
            if (commandType.equals(Strings.COMMAND_TYPE_TRANSLATE) && (axis.equals(Strings.AXIS_X) || axis.equals(Strings.AXIS_Y) || axis.equals(Strings.AXIS_Z)))
            {
                return axis;
            } else if (commandType.equals(Strings.COMMAND_TYPE_ROTATE) && (axis.equals(Strings.AXIS_X) || axis.equals(Strings.AXIS_Y)))
            {
                return axis;
            } else
            {
                System.out.println(Strings.INVALID_AXIS_REPEAT);
            }
        }
    }

    /**
     * Fetches the amount of the command's attributed action from the user input.
     * Precondition: The scanner should be initialized.
     * Postcondition: Returns a valid amount entered by the user.
     *
     * @param scanner - The Scanner for user input.
     * @return A valid amount.
     */
    private double fetchAmount (Scanner scanner)
    {
        while (true)
        {
            System.out.print(Strings.ENTER_AMOUNT);
            try
            {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException numberFormatException)
            {
                System.out.println(Strings.INVALID_AMOUNT);
            }
        }
    }
}
