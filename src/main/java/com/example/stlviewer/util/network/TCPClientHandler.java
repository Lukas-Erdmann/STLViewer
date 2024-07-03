package com.example.stlviewer.util.network;

import com.example.stlviewer.control.STLViewerController;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The TCPClientHandler class handles the processing of client commands for the TCP server.
 * It reads commands from the client, executes them, and sends the results back to the client.
 */
public class TCPClientHandler extends Thread
{
    /**
     * The client socket for communication with the client.
     */
    private final Socket clientSocket;
    /**
     * The controller for the STL viewer application.
     */
    private final STLViewerController stlViewerController;

    /**
     * Constructs a TCPClientHandler with the specified client socket and controller.
     * Precondition: The client socket and controller should be initialized.
     * Postcondition: A TCPClientHandler instance is created with the given client socket and controller.
     *
     * @param clientSocket        - The client socket for communication with the client.
     * @param stlViewerController - The controller for the STL viewer application.
     */
    public TCPClientHandler (Socket clientSocket, STLViewerController stlViewerController)
    {
        this.clientSocket = clientSocket;
        this.stlViewerController = stlViewerController;
    }

    /**
     * Runs the client handler thread to process client commands.
     * Precondition: The client socket should be connected to a client.
     * Postcondition: The client commands are processed and the client socket is closed.
     */
    @Override
    public void run ()
    {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true))
        {

            processCommands(input, output);

        } catch (IOException e)
        {
            System.err.println(Strings.AN_EXCEPTION_OCCURRED_PROCESSING_CLIENT_COMMANDS + e.getMessage());
            e.printStackTrace();
        } finally
        {
            try
            {
                clientSocket.close();
            } catch (IOException e)
            {
                System.err.println(Strings.AN_EXCEPTION_OCCURRED_CLOSING_CLIENT_SOCKET + e.getMessage());
            }
        }
    }

    /**
     * Processes commands from the client input stream. Checks if the command is of the correct format and executes it.
     * Precondition: The input stream should be initialized.
     * Postcondition: Commands are processed and executed.
     *
     * @param input  - The BufferedReader for client input.
     * @param output - The PrintWriter for client output.
     * @throws IOException - Thrown if an I/O error occurs when reading or writing to the client.
     */
    private void processCommands (BufferedReader input, PrintWriter output) throws IOException
    {
        String command;
        while ((command = input.readLine()) != null)
        {
            String[] commandParts = command.split(Strings.SPACE);
            if (commandParts.length == Constants.COMMAND_WORDCOUNT)
            {
                try
                {
                    executeCommand(commandParts, output);
                    output.println(Strings.EXECUTED_COMMAND + command);
                } catch (Exception exception)
                {
                    output.println(Strings.AN_EXCEPTION_OCCURRED_EXECUTING_THE_COMMAND + exception.getMessage());
                    exception.printStackTrace();
                }
            } else
            {
                output.printf(Strings.INVALID_COMMAND_EXPECTED_FORMAT, command);
            }
        }
    }

    /**
     * Executes a single command from the client. The command is parsed and executed based on the command type.
     * Precondition: The command parts should be valid. The PrintWriter should be initialized.
     * Postcondition: The command is executed if valid, otherwise an error message is sent.
     *
     * @param commandParts - The parts of the command string from the client.
     * @param output       - The PrintWriter for client output.
     */
    private void executeCommand (String[] commandParts, PrintWriter output)
    {
        String commandType = commandParts[Constants.COMMAND_TYPE_INDEX];
        String axis = commandParts[Constants.COMMAND_AXIS_INDEX];
        double amount;

        try
        {
            amount = Double.parseDouble(commandParts[Constants.COMMAND_AMOUNT_INDEX]);
        } catch (NumberFormatException numberFormatException)
        {
            output.println(Strings.INVALID_VALUE_FOR_AMOUNT + commandParts[Constants.COMMAND_AMOUNT_INDEX]);
            return;
        }

        switch (commandType)
        {
            case Strings.COMMAND_TYPE_TRANSLATE:
                stlViewerController.translateModel(axis, amount);
                break;
            case Strings.COMMAND_TYPE_ROTATE:
                stlViewerController.rotateModel(axis, amount);
                break;
            default:
                output.println(Strings.COMMAND_COULDN_T_BE_EXECUTED + commandType);
        }
    }
}
