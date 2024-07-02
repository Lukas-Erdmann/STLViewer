package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import javafx.stage.Stage;
import com.example.stlviewer.view.ConsoleApplication;

import java.io.IOException;

public class ApplicationController
{
    private STLReader stlReader;
    private STLViewerController stlViewerController;
    private PolyhedronController polyhedronController;
    private ConsoleApplication consoleApplication;
    private TCPController tcpController;

    public ApplicationController() {
        this.stlReader = new STLReader();
        this.stlViewerController = new STLViewerController(this);
        this.polyhedronController = new PolyhedronController();
        this.consoleApplication = new ConsoleApplication();
        this.tcpController = new TCPController();
    }

    public void startViewer(Stage stage) {
        stlViewerController.startSTLViewer(stage);
    }

    public void openFile(String filepath)
    {
        try {
            this.stlReader.readSTLFile(filepath, polyhedronController);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void startServer(int port) {
        tcpController.startServer(port, stlViewerController);
    }

    public void startClient(String host, int port) {
        tcpController.startClient(host, port);
    }

    public PolyhedronController getPolyhedronController() {
        return polyhedronController;
    }

    public void sortTriangles() {
        polyhedronController.getPolyhedron().getTriangles().sort(null);
    }
}
