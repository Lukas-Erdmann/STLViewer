package com.example.stlviewer.control;

import com.example.stlviewer.util.network.P2PClient;
import com.example.stlviewer.util.network.P2PServer;

/**
 * The P2PController class manages peer-to-peer network operations. It mainly handles starting
 * the P2P client and server, sending data between connected peers, and stopping the client and server.
 */
public class P2PController
{
    /**
     * The P2PClient instance to control the client.
     */
    private P2PClient p2pClient;
    /**
     * The P2PServer instance to control the server.
     */
    private P2PServer p2pServer;
    /**
     * The GUIController instance to handle viewer-related operations.
     */
    private GUIController GUIController;
    /**
     * A flag to indicate if the application is running.
     */
    private boolean closeApplication = false;

    /**
     * Constructs a P2PController with the specified GUIController.
     * Precondition: The GUIController should be initialized.
     * Postcondition: The P2PController is created.
     *
     * @param GUIController - The corresponding GUIController instance to handle viewer-related operations.
     */
    public P2PController (GUIController GUIController)
    {
        this.GUIController = GUIController;
        this.GUIController.setP2PController(this);
    }

    /**
     * Starts the P2P client to connect to the specified host and port.
     * Precondition: It must be possible to connect to the specified host and port.
     * Postcondition: The P2P client is started and establishes a connection to the specified host and port.
     *
     * @param host - The host to connect to.
     * @param port - The port to connect to.
     */
    public void startClient (String host, int port)
    {
        new Thread(() -> {
            p2pClient = new P2PClient(host, port);
            p2pClient.start();
        }).start();
    }

    /**
     * Starts the P2P server receiving connections on the given port.
     * Precondition: The port must be valid and not in use.
     * Postcondition: The P2P server is started and waits for incoming connections.
     *
     * @param port - The port to start the server on.
     */
    public void startServer (int port)
    {
        new Thread(() -> {
            p2pServer = new P2PServer(port, GUIController);
            p2pServer.start();
        }).start();
    }

    /**
     * Sends data to the connected peer if the P2P client is running.
     * Precondition: The P2P client has to be started and connected.
     * Postcondition: The data is sent to the connected peer.
     *
     * @param data - The data to send to the peer.
     */
    public void sendData (Object data)
    {
        if (p2pClient != null)
        {
            p2pClient.sendData(data);
        }
    }

    /**
     * Stops the P2P client and server if they are running.
     * Precondition: The P2P client and server must be running.
     * Postcondition: The P2P client and server are stopped.
     */
    public void terminate ()
    {
        if (p2pClient != null)
        {
            p2pClient.stopClient();
        }
        if (p2pServer != null)
        {
            p2pServer.stopServer();
        }
    }
}