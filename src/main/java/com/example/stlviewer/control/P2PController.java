package com.example.stlviewer.control;

import com.example.stlviewer.util.network.P2PClient;
import com.example.stlviewer.util.network.P2PServer;

/**
 * The P2PController class manages peer-to-peer network operations. It mainly handles starting
 * the P2P client and server, sending data between connected peers, and stopping the client and server.
 */
public class P2PController
{

    private P2PClient p2pClient;
    private P2PServer p2pServer;
    private STLViewerController stlViewerController;

    /**
     * Constructs a P2PController with the specified stlViewerController.
     * Precondition: The stlViewerController should be initialized.
     * Postcondition: The P2PController is created.
     *
     * @param stlViewerController - The corresponding STLViewerController instance to handle viewer-related operations.
     */
    public P2PController (STLViewerController stlViewerController)
    {
        this.stlViewerController = stlViewerController;
        this.stlViewerController.setP2PController(this);
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
            p2pServer = new P2PServer(port, stlViewerController);
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
}