package com.example.stlviewer.control;

import com.example.stlviewer.util.TCPClient;
import com.example.stlviewer.util.TCPServer;

public class TCPController
{
    private TCPServer tcpServer;
    private TCPClient tcpClient;

    public void startServer(int port, STLViewerController stlViewerController) {
        new Thread(() -> {
            try {
                tcpServer = new TCPServer(port, stlViewerController);
                tcpServer.start();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public void startClient(String host, int port) {
        new Thread(() -> {
            try {
                tcpClient = new TCPClient(host, port);
                tcpClient.start();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }
}
