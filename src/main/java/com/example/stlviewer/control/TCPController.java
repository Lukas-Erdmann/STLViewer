package com.example.stlviewer.control;

import com.example.stlviewer.util.network.TCPClient;
import com.example.stlviewer.util.network.TCPServer;

/**
 * The TCPController class manages the TCP network operations for the application,
 * mainly starting a server and a client for TCP communication.
 */
public class TCPController
{
    /**
     * The TCPServer instance to control the server.
     */
    private TCPServer tcpServer;
    /**
     * The TCPClient instance to control the client.
     */
    private TCPClient tcpClient;

    /**
     * Starts the server on its own thread.
     * Precondition: The port must be valid and the stlViewerController must be initialized.
     * Postcondition: The server is started on a new thread.
     *
     * @param port                - The port the server should listen on.
     * @param stlViewerController - The STLViewerController that handles viewer operations.
     */
    public void startServer (int port, STLViewerController stlViewerController)
    {
        new Thread(() -> {
            try
            {
                tcpServer = new TCPServer(port, stlViewerController);
                tcpServer.start();
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }).start();
    }

    /**
     * Starts the client on its own thread.
     * Precondition: The host must be a valid string and the port must be valid.
     * Postcondition: The client is started on a new thread.
     *
     * @param host - The host to connect to.
     * @param port - The port to connect to.
     */
    public void startClient (String host, int port)
    {
        new Thread(() -> {
            try
            {
                tcpClient = new TCPClient(host, port);
                tcpClient.start();
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }).start();
    }

    public void terminate ()
    {
        if (tcpServer != null)
        {
            tcpServer.terminate();
        }
        if (tcpClient != null)
        {
            tcpClient.terminate();
        }
    }
}
