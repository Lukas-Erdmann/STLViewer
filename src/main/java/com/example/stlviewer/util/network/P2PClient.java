package com.example.stlviewer.util.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The P2PClient class is used to create peer-to-peer clients that connect to a given host and port,
 * and send data to the other peer. The peer thus acts as the server.
 */
public class P2PClient extends Thread
{

    /**
     * The host to connect to.
     */
    private final String host;
    /**
     * The port to connect to.
     */
    private final int port;
    /**
     * The socket to connect to the host.
     */
    private Socket socket;
    /**
     * The output stream to send data to the connected peer.
     */
    private ObjectOutputStream outputStream;

    /**
     * Constructs a P2PClient with the specified host and port.
     *
     * @param host - The host to connect to.
     * @param port - The port to connect to.
     */
    public P2PClient (String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Runs the client thread, creating a connection to the specified socket.
     * Once the connection is established, the output stream is initialized for communication.
     * Precondition: The host and port must be reachable.
     * Postcondition: The socket and output stream are initialized for communication.
     */
    @Override
    public void run ()
    {
        try
        {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Sends data to the connected peer.
     * Precondition: The socket and output stream must be initialized and connected.
     * Postcondition: The data is serialized and sent to the connected peer.
     *
     * @param data - The data to send.
     */
    public void sendData (Object data)
    {
        try
        {
            if (outputStream != null)
            {
                outputStream.writeObject(data);
                outputStream.flush();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Closes the socket and output stream to stop the client.
     * Precondition: None.
     * Postcondition: The connection is closed and the client is stopped.
     */
    public void stopClient ()
    {
        try
        {
            if (outputStream != null)
            {
                outputStream.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }
}