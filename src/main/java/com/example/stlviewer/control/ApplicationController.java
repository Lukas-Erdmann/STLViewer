package com.example.stlviewer.control;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import javafx.application.Platform;
import javafx.stage.Stage;
import com.example.stlviewer.view.ConsoleApplication;

import java.io.IOException;

/**
 * The ApplicationController class is the main controller class of the application.
 * It is responsible for managing the different controllers and starting the application.
 * It also provides methods to open files, read STL files, start the TCP connection, and sort triangles.
 */
public class ApplicationController
{
    /**
     * The STLReader instance to read STL files.
     */
    private STLReader stlReader;
    /**
     * The STLViewerController instance to manage the STL viewer.
     */
    private STLViewerController stlViewerController;
    /**
     * The PolyhedronController instance to manage the polyhedron data.
     */
    private PolyhedronController polyhedronController;
    /**
     * The ConsoleApplication instance to manage the console application.
     */
    private ConsoleApplication consoleApplication;
    /**
     * The TCPController instance to manage the TCP connection.
     */
    private TCPController tcpController;

    public ApplicationController() {
        this.stlReader = new STLReader();
        this.polyhedronController = new PolyhedronController();
        this.stlViewerController = new STLViewerController(this);
        this.tcpController = new TCPController();
        this.consoleApplication = new ConsoleApplication();
    }

    /**
     * Starts the stl viewer application.
     * Precondition: None
     * Postcondition: The viewer is started.
     *
     * @param stage - The stage to start the viewer on.
     */
    public void startViewer(Stage stage) {
        stlViewerController.startSTLViewer(stage);
    }

    /**
     * Opens a file and reads the STL data from it.
     * Precondition: The file path must be valid.
     * Postcondition: The STL data is read from the file and stored in the polyhedron controller.
     *
     * @param filepath - The path to the file to open.
     */
    public void openFile(String filepath)
    {
        try {
            this.stlReader.readSTLFileParallelized(filepath, polyhedronController);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reads an STL file from a path defined by the user in the console and prints the polyhedron data to the console.
     * The file is read in parallel if the parallelized parameter is set to true, and sequentially otherwise.
     * Precondition: None
     * Postcondition: The STL data is read from the file and stored in the polyhedron controller.
     *
     * @param parallelized - Whether to read the file in parallel.
     * @throws IOException - If an I/O error occurs.
     */
    public void readSTLFileInConsole(boolean parallelized) throws IOException
    {
        stlReader.readSTLFile(consoleApplication.askForFileName(), polyhedronController, parallelized);
        System.out.println(polyhedronController.getPolyhedron().toString());
    }

    /**
     * Starts a TCP connection between a server and a client. The server listens on the specified port,
     * and the client connects to the server. The STL viewer is then started.
     * Precondition: The specified port should be available and not in use by another application.
     * Postcondition: The TCP connection is established and the STL viewer is started.
     */
    public void startTCPConnection() {
        try {
            startServer(Constants.SERVER_PORT);
            startClient(Strings.LOCALHOST, Constants.SERVER_PORT);
            Platform.runLater(() -> {
                try {
                    Stage stageTCP = new Stage();
                    stlViewerController.startSTLViewer(stageTCP);
                } catch (Exception exception) {
                    throw new RuntimeException(Strings.EXCEPTION_WHEN_ATTEMPTING_TO_START_STL_VIEWER, exception);
                }
            });
        } catch (Exception exception) {
            throw new RuntimeException(Strings.UNABLE_TO_START_TCP_CONNECTION, exception);
        }
    }

    /**
     * Starts the server on the specified port.
     * Precondition: The port must be valid and the STL viewer controller must be initialized.
     * Postcondition: The server is started on a new thread.
     *
     * @param port - The port to start the server on.
     */
    public void startServer(int port) {
        tcpController.startServer(port, stlViewerController);
    }

    /**
     * Starts the client to connect to the specified host and port.
     * Precondition: The host must be a valid string and the port must be valid.
     * Postcondition: The client is started on a new thread.
     *
     * @param host - The host to connect to.
     * @param port - The port to connect to.
     */
    public void startClient(String host, int port) {
        tcpController.startClient(host, port);
    }

    /**
     * Gets the polyhedron controller.
     * Precondition: None
     * Postcondition: The polyhedron controller is returned.
     *
     * @return The polyhedron controller.
     */
    public PolyhedronController getPolyhedronController() {
        return polyhedronController;
    }

    /**
     * Sorts the triangles of the polyhedron by their area.
     * Precondition: None
     * Postcondition: The triangles are sorted.
     */
    public void sortTriangles() {
        polyhedronController.getPolyhedron().getTriangles().sort(null);
    }
}
