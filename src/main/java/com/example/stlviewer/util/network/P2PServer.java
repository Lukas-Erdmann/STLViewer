package com.example.stlviewer.util.network;

import com.example.stlviewer.control.GUIController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The P2PServer class represents a peer-to-peer server that listens for incoming client connections
 * and handles them using the PolyhedronViewerController.
 */
public class P2PServer extends Thread
{

    /**
     * The port to listen on.
     */
    private final int port;
    /**
     * The GUIController instance that creates the displayinh application.
     */
    private final GUIController GUIController;
    /**
     * The server socket to listen for incoming connections.
     */
    private ServerSocket serverSocket;

    /**
     * Constructs a P2PServer with the specified port and GUIController.
     *
     * @param port                - The port to listen on.
     * @param GUIController - The PolyhedronViewerController instance to handle client connections.
     */
    public P2PServer (int port, GUIController GUIController)
    {
        this.port = port;
        this.GUIController = GUIController;
    }

    /**
     * Runs the server thread. Listens for incoming client connections and forwards them to the client handler.
     * Precondition: The port must be available and not in use.
     * Postcondition: The server listens for and accepts incoming client connections.
     */
    @Override
    public void run ()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            // Continuously listen for incoming client connections
            while (!serverSocket.isClosed())
            {
                Socket clientSocket = serverSocket.accept();
                new P2PClientHandler(clientSocket, GUIController).start();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Close the server socket to stop the server from accepting new connections and shuts down the server.
     * Precondition: None.
     * Postcondition: The server socket is closed, stopping the server from accepting new connections.
     */
    public void stopServer ()
    {
        try
        {
            if (serverSocket != null)
            {
                serverSocket.close();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }
}