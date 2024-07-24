package com.example.stlviewer.control;

import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.view.ConsoleApplication;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The masterController class is the main controller class of the application.
 * It is responsible for managing the different controllers and starting the application.
 * It also provides methods to open files, read STL files, start the TCP connection, and sort triangles.
 */
public class masterController
{
    /**
     * The mode of the application. (CONSOLE, TCP, P2P)
     */
    private String mode;
    /**
     * The STLReader instance to read STL files.
     */
    private final STLReader stlReader;
    /**
     * The STLViewerController instance to manage the STL viewer.
     */
    private STLViewerController stlViewerController;
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
     * The STLViewerController instance to manage the first peer-to-peer STL viewer.
     */
    private STLViewerController stlViewerControllerP2P1;
    /**
     * The STLViewerController instance to manage the second peer-to-peer STL viewer.
     */
    private STLViewerController stlViewerControllerP2P2;

    /**
     * Constructs an masterController with the default controllers.
     * Precondition: None
     * Postcondition: An masterController instance is created.
     */
    public masterController (String mode, boolean parallelized) throws IOException {
        // Reader always initialized
        this.stlReader = new STLReader();

        switch (mode)
        {
            case "CONSOLE": // Console mode
                this.mode = "CONSOLE";
                this.stlViewerController = new STLViewerController(this);
                this.consoleApplication = new ConsoleApplication();
                readSTLFileInConsole(parallelized);
                break;
            case "TCP": // TCP mode
                this.mode = "TCP";
                this.stlViewerController = new STLViewerController(this);
                this.tcpController = new TCPController();
                startTCPConnection();
                break;
            case "P2P": // Peer-to-peer mode
                this.mode = "P2P";
                this.stlViewerControllerP2P1 = new STLViewerController(this);
                this.stlViewerControllerP2P2 = new STLViewerController(this);
                this.p2pController1 = new P2PController(stlViewerControllerP2P1);
                this.p2pController2 = new P2PController(stlViewerControllerP2P2);
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
     * Postcondition: The STL data is read from the file and stored in the polyhedron controller.
     *
     * @param filepath - The path to the file to open.
     */
    public void openFile (String filepath, PolyhedronController polyhedronController)
    {
        reinitializePolyhedronController();
        try
        {
            this.stlReader.readSTLFileParallelized(filepath, polyhedronController);
        } catch (Exception exception)
        {
            // TODO: Handle exception
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
     * Postcondition: The TCP connection is established and the STL viewer is started.
     */
    public void startTCPConnection ()
    {
        try
        {
            // Start the server and client
            startTCPServer(Constants.SERVER_PORT);
            startTCPClient(Strings.LOCALHOST, Constants.SERVER_PORT);

            // Start the STL viewer without blocking the main thread
            Platform.runLater(() -> {
                try
                {
                    Stage stageTCP = new Stage();
                    stlViewerController.startSTLViewer(stageTCP);
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
     * Postcondition: The server is started on a new thread.
     *
     * @param port - The port to start the server on.
     */
    public void startTCPServer (int port)
    {
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
    public void startTCPClient (String host, int port)
    {
        tcpController.startClient(host, port);
    }

    /**
     * Starts a peer-to-peer connection between two peers. Each peer acts as a server and a client.
     * Precondition: The ip address and ports must be valid and not in use by another application.
     * Postcondition: The peer-to-peer connection is established and the STL viewers are started.
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
                    Stage stageP2P1 = new Stage();
                    stlViewerControllerP2P1.startSTLViewer(stageP2P1);
                    Stage stageP2P2 = new Stage();
                    stlViewerControllerP2P2.startSTLViewer(stageP2P2);
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
     * Postcondition: The polyhedron controller is returned.
     *
     * @return The polyhedron controller.
     */
    public PolyhedronController getPolyhedronController ()
    {
        return polyhedronController;
    }

    /**
     * Sorts the triangles of the polyhedron by their area.
     * Precondition: None
     * Postcondition: The triangles are sorted.
     */
    public void sortTriangles ()
    {
        polyhedronController.getPolyhedron().getTriangles().sort(Triangle::compareTo);

        // Print the sorted triangles to the console
        for (Triangle triangle : polyhedronController.getPolyhedron().getTriangles())
        {
            System.out.println(triangle);
        }
    }

    public void reinitializePolyhedronController() {
        this.polyhedronController = new PolyhedronController();
    }

    public String getMode() {
        return mode;
    }
}
