package com.example.stlviewer;

import com.example.stlviewer.control.MainController;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static com.example.stlviewer.util.RuntimeHandler.logMessage;

/**
 * The Main class is the entry point for the STL Viewer application.
 * It initializes the application and sets up the stage for the GUI.
 */
public class Main extends Application
{
    /**
     * The MainController instance that manages the application.
     */
    MainController appController;

    /**
     * The entry point for the application.
     *
     * @param args - The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the application and sets up the stage for the GUI. It uses the arguments passed to the application
     * to initialize the MainController with the specified user operating mode and parallel processing flag.
     *
     * @param stage - The primary stage for the application.
     * @throws IOException - Thrown if an I/O error occurs when starting the application.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Retrieve the arguments passed to the application
        List<String> args = getParameters().getRaw();

        // Use the arguments to initialize MainController, use default values if no arguments are provided
        String arg1 = !args.isEmpty() ? args.get(Constants.ARGS_MODE) : Strings.TCP_MODE;
        boolean arg2 = args.size() <= Constants.ARGS_PARALLEL || Boolean.parseBoolean(args.get(Constants.ARGS_PARALLEL));

        appController = new MainController(stage, arg1, arg2);

        // Set up the stage close request to terminate the application
        stage.setOnCloseRequest(_ -> {
            logMessage(Strings.CLOSING_APPLICATION);
            try {
                stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.exit(Constants.N_ZERO);
        });
    }

    /**
     * Stops the application by terminating all processes and threads. It is called when the application is closed. <br>
     * Precondition: The application should be running. <br>
     * Post-Condition: All processes and threads are terminated.
     */
    @Override
    public void stop() {
        // Terminate all processes and threads
        appController.terminate();
    }
}