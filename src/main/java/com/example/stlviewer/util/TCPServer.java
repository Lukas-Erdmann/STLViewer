package com.example.stlviewer.util;

import com.example.stlviewer.control.PolyhedronController;
import com.example.stlviewer.control.STLViewerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer
{
    private final int port;
    private final STLViewerController stlViewerController;

    public TCPServer(int port, STLViewerController stlViewerController) {
        this.port = port;
        this.stlViewerController = stlViewerController;
    }

    public void start() throws IOException
    {
        try (ServerSocket serverSocket = createSocket()) {
            System.out.println("The server is reachable on port: " + port);
            acceptClients(serverSocket);
        }
    }

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(port);
    }

    private void acceptClients(ServerSocket serverSocket) {
        while (true) {
            try {
                handleClient(serverSocket.accept());
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        new TCPClientHandler(clientSocket, stlViewerController).start();
    }
}
