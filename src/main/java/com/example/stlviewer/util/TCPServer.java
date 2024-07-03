package com.example.stlviewer.util;

import com.example.stlviewer.control.STLViewerController;
import com.example.stlviewer.res.Strings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCPServer class listens for client connections and delegates the handling of each client to a
 * CPClientHandler instance. It runs on a specified port and communicates with the STLViewerController.
 */
public class TCPServer
{
    /**
     * The port number on which the server listens for client connections.
     */
    private final int port;
    /**
     * The controller for the STL viewer application.
     */
    private final STLViewerController stlViewerController;

    /**
     * Constructs a TCPServer with the specified port and controller.
     * Precondition: The port should be a valid port number, and the controller should be initialized.
     * Postcondition: A TCPServer instance is created with the given port and controller.
     *
     * @param port - The port number on which the server listens for connections.
     * @param stlViewerController - The controller for the STL viewer application.
     */
    public TCPServer(int port, STLViewerController stlViewerController) {
        this.port = port;
        this.stlViewerController = stlViewerController;
    }

    /**
     * Starts the TCP server and begins accepting client connections.
     * Precondition: The port should be available and not in use by another application.
     * Postcondition: The server listens for client connections and handles them.
     *
     * @throws IOException - Thrown if an I/O error occurs when opening the server socket.
     */
    public void start() throws IOException
    {
        try (ServerSocket serverSocket = createSocket()) {
            System.out.println(Strings.SERVER_IS_AVAILABLE_ON_PORT + port);
            acceptClients(serverSocket);
        } catch (IOException exception) {
            System.err.printf(Strings.EXCEPTION_SERVER_START, port, exception.getMessage());
            throw exception;
        }
    }

    /**
     * Creates a ServerSocket on the specified port.
     * Precondition: The port should be valid and available.
     * Postcondition: A ServerSocket is created and bound to the port.
     *
     * @return The created ServerSocket.
     * @throws IOException - Thrown if an I/O error occurs when creating the server socket.
     */
    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(port);
    }

    /**
     * Accepts client connections in a loop and forwards the connection to the client handler.
     * Precondition: The server socket should be initialized and bound to the port.
     * Postcondition: Client connections are accepted and handled.
     *
     * @param serverSocket - The ServerSocket instance.
     */
    private void acceptClients(ServerSocket serverSocket) {
        while (true) {
            try {
                handleClient(serverSocket.accept());
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    /**
     * Handles a client connection by creating a new TCPClientHandler instance.
     * Precondition: The client socket should be connected.
     * Postcondition: A new TCPClientHandler instance is created and started.
     *
     * @param clientSocket - The client socket for communication with the client.
     */
    private void handleClient(Socket clientSocket) {
        new TCPClientHandler(clientSocket, stlViewerController).start();
    }
}
