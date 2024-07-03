package com.example.stlviewer.util.network;

import com.example.stlviewer.control.STLViewerController;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * The P2PClientHandler class handles incoming data from a peer-to-peer client connection,
 * processing and forwarding it to the PolyhedronViewerController.
 */
public class P2PClientHandler extends Thread
{

    private final Socket clientSocket;
    private final STLViewerController stlViewerController;

    /**
     * Constructs a P2PClientHandler with the specified client socket and PolyhedronViewerController.
     * Precondition: The clientSocket must be fully initialized and connected. The stlViewerController must be fully initialized and non-null.
     * Postcondition: A P2PClientHandler instance is created.
     *
     * @param clientSocket        - The client socket connected to the peer.
     * @param stlViewerController - The PolyhedronViewerController instance to handle received data.
     */
    public P2PClientHandler (Socket clientSocket, STLViewerController stlViewerController)
    {
        this.clientSocket = clientSocket;
        this.stlViewerController = stlViewerController;
    }

    /**
     * Runs the client handler thread, continuously reading data from the input stream and forwarding it to the viewer controller.
     * Precondition: The client socket must be connected, and the input stream must be available.
     * Postcondition: Incoming data is processed and handled by the viewer controller.
     */
    @Override
    public void run ()
    {
        try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream()))
        {
            while (true)
            {
                Object p2pData = inputStream.readObject();
                stlViewerController.processP2PData(p2pData);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}