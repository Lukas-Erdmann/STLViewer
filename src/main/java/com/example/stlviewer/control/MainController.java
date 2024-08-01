package com.example.stlviewer.control;

import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.view.ConsoleApplication;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The MainController class is the main controller class of the application.
 * It is responsible for managing the different controllers and starting the application.
 * It also provides methods to open files, read STL files, start the TCP connection, and sort triangles.
 */
public class MainController
{
    /**
     * The mode of the application. (CONSOLE, TCP, P2P)
     */
    private final String userOperationMode;
    /**
     * Whether to read the file in parallel.
     */
    private final boolean parallelized;
    /**
     * The STLReader instance to read STL files.
     */
    private final STLReader stlReader;
    /**
     * The GUIController instance to manage the STL viewer.
     */
    private GUIController GUIController;
    /**
     * The PolyhedronController instance to manage the polyhedron data when reading the file in the console.
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

    // -- P2P Controllers --
    /**
     * The P2PController instance to manage the first peer-to-peer process.
     */
    private P2PController p2pController1;
    /**
     * The P2PController instance to manage the second peer-to-peer process.
     */
    private P2PController p2pController2;
    /**
     * The GUIController instance to manage the first peer-to-peer STL viewer.
     */
    private GUIController GUIControllerP2P1;
    /**
     * The GUIController instance to manage the second peer-to-peer STL viewer.
     */
    private GUIController GUIControllerP2P2;

    // -- Stages --
    /**
     * The stage passed from the main application.
     */
    private final Stage mainStage;
    /**
     * The stage 2 for the peer-to-peer connection.
     */
    private Stage stageP2P2;

    /**
     * The logger for the MainController class.
     */
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    /**
     * Constructs an MainController with the default controllers.
     * Precondition: None
     * Post-Condition: An MainController instance is created.
     */
    public MainController (Stage stage, String userOperationMode, boolean parallelized) throws IOException {
        this.mainStage = stage;
        this.parallelized = parallelized;
        // Reader always initialized
        this.stlReader = new STLReader();

        switch (userOperationMode)
        {
            case Strings.CONSOLE_MODE: // Console mode
                this.userOperationMode = Strings.CONSOLE_MODE;
                this.GUIController = new GUIController(this);
                this.consoleApplication = new ConsoleApplication();
                readSTLFileInConsole(parallelized);
                break;
            case Strings.TCP_MODE: // TCP mode
                this.userOperationMode = Strings.TCP_MODE;
                this.GUIController = new GUIController(this);
                this.tcpController = new TCPController();
                startTCPConnection();
                break;
            case Strings.P2P_MODE: // Peer-to-peer mode
                this.userOperationMode = Strings.P2P_MODE;
                this.GUIControllerP2P1 = new GUIController(this);
                this.GUIControllerP2P2 = new GUIController(this);
                this.p2pController1 = new P2PController(GUIControllerP2P1);
                this.p2pController2 = new P2PController(GUIControllerP2P2);
                startP2PConnection();
                break;
            default:
                throw new IllegalArgumentException(Strings.INVALID_MODE);
        }
    }

    /**
     * Opens a file and reads the STL data from it. If a polyhedron already exists,
     * it is cleared before reading the new data.
     * Precondition: The file path must be valid.
     * Post-Condition: The STL data is read from the file and stored in the polyhedron controller.
     *
     * @param polyhedronController     The polyhedron controller to store the data in.
     */
    public void openFile (String filePath, PolyhedronController polyhedronController)
    {
        // TODO: Rework the Logger usage
        reinitializePolyhedronController();
        try
        {
            this.stlReader.readSTLFile(filePath, polyhedronController, parallelized);
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.log(Level.SEVERE, "File not found: " + filePath, fileNotFoundException);
        } catch (IOException ioException) {
            LOGGER.log(Level.SEVERE, "I/O error while reading the file: " + filePath, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException("File is corrupted and cannot be read: " + filePath);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while opening the file: " + filePath, exception);
        }
    }

    /**
     * Reads an STL file from a path defined by the user in the console and prints the polyhedron data to the console.
     * The file is read in parallel if the parallelized parameter is set to true, and sequentially otherwise.
     * Precondition: None
     * Post-Condition: The STL data is read from the file and stored in the polyhedron controller.
     *
     * @param parallelized - Whether to read the file in parallel.
     * @throws IOException - If an I/O error occurs.
     */
    public void readSTLFileInConsole (boolean parallelized) throws IOException
    {
        reinitializePolyhedronController();
        stlReader.readSTLFile(consoleApplication.askForFileName(), polyhedronController, parallelized);
        System.out.println(polyhedronController.getPolyhedron().toString());
    }

    /**
     * Starts a TCP connection between a server and a client. The server listens on the specified port,
     * and the client connects to the server. The STL viewer is then started.
     * Precondition: The specified port should be available and not in use by another application.
     * Post-Condition: The TCP connection is established and the STL viewer is started.
     */
    public void startTCPConnection ()
    {
        try
        {
            // Start the server and client
            startTCPServer(Constants.SERVER_PORT);

            // Set the callback to start the client after the file is loaded
            GUIController.setOnFileLoadedCallback((_) ->
                    startTCPClient(Strings.LOCALHOST, Constants.SERVER_PORT));

            // Start the STL viewer without blocking the main thread
            Platform.runLater(() -> {
                try
                {
                    GUIController.startSTLViewer(mainStage);
                } catch (Exception exception)
                {
                    throw new RuntimeException(Strings.EXCEPTION_WHEN_ATTEMPTING_TO_START_STL_VIEWER, exception);
                }
            });
        } catch (Exception exception)
        {
            throw new RuntimeException(Strings.UNABLE_TO_START_TCP_CONNECTION, exception);
        }
    }

    /**
     * Starts the server on the specified port.
     * Precondition: The port must be valid and the STL viewer controller must be initialized.
     * Post-Condition: The server is started on a new thread.
     *
     * @param port - The port to start the server on.
     */
    public void startTCPServer (int port)
    {
        tcpController.startServer(port, GUIController);
    }

    /**
     * Starts the client to connect to the specified host and port.
     * Precondition: The host must be a valid string and the port must be valid.
     * Post-Condition: The client is started on a new thread.
     *
     * @param host - The host to connect to.
     * @param port - The port to connect to.
     */
    public void startTCPClient (String host, int port)
    {
        tcpController.startClient(host, port);
    }

    /**
     * Starts a peer-to-peer connection between two peers. Each peer acts as a server and a client.
     * Precondition: The ip address and ports must be valid and not in use by another application.
     * Post-Condition: The peer-to-peer connection is established and the STL viewers are started.
     */
    public void startP2PConnection ()
    {
        try
        {
            // Peer 1: Server / Peer 2: Client
            p2pController1.startServer(Constants.P2P_PORT_1);
            p2pController2.startClient(Strings.LOCALHOST, Constants.P2P_PORT_1);

            // Peer 2: Server / Peer 1: Client
            p2pController2.startServer(Constants.P2P_PORT_2);
            p2pController1.startClient(Strings.LOCALHOST, Constants.P2P_PORT_2);

            Platform.runLater(() -> {
                try
                {
                    GUIControllerP2P1.startSTLViewer(mainStage);
                    stageP2P2 = new Stage();
                    GUIControllerP2P2.startSTLViewer(stageP2P2);
                } catch (Exception exception)
                {
                    throw new RuntimeException(Strings.EXCEPTION_WHEN_ATTEMPTING_TO_START_STL_VIEWER, exception);
                }
            });
        } catch (Exception exception)
        {
            throw new RuntimeException(Strings.UNABLE_TO_START_P2P_CONNECTION, exception);
        }
    }


    /**
     * Gets the polyhedron controller.
     * Precondition: None
     * Post-Condition: The polyhedron controller is returned.
     *
     * @return The polyhedron controller.
     */
    public PolyhedronController getPolyhedronController ()
    {
        return polyhedronController;
    }

    /**
     * Sorts the triangles of the polyhedron by their area. The triangles are sorted in ascending order into a new list.
     * It then returns the sorted list of triangles.
     *
     * <p>Precondition: The polyhedron must exist and contain triangles.
     * <p>Post-Condition: The triangles are sorted by their area and returned in a new list. The original list is not modified.
     *
     * @return The sorted list of triangles.
     */
    public ArrayList<Triangle> sortTrianglesByArea ()
    {
        // TODO: Output the sorted triangles to the console or some other output
        ArrayList<Triangle> sortedTriangles = new ArrayList<>(polyhedronController.getPolyhedron().getTriangles().values());
        sortedTriangles.sort(Triangle::compareTo);
        return sortedTriangles;
    }

    /**
     * Reinitializes the polyhedron controller with a new instance.
     * Precondition: None
     * Post-Condition: The polyhedron controller is reinitialized.
     */
    public void reinitializePolyhedronController() {
        this.polyhedronController = new PolyhedronController();
    }

    /**
     * Gets the user operation mode.
     *
     * @return The user operation mode.
     */
    public String getUserOperationMode() {
        return userOperationMode;
    }

    /**
     * Terminates the application by closing the different controllers corresponding to the user operation mode.
     *
     * <p>Precondition: The controllers must exist.
     * <p>Post-Condition: The application is terminated.
     */
    public void terminate() {
        switch (userOperationMode)
        {
            case Strings.CONSOLE_MODE:
                consoleApplication.terminate();
                break;
            case Strings.TCP_MODE:
                tcpController.terminate();
                break;
            case Strings.P2P_MODE:
                p2pController1.terminate();
                p2pController2.terminate();
                break;
            default:
                throw new IllegalArgumentException(Strings.INVALID_MODE);
        }
    }
}