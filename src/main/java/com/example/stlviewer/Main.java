package com.example.stlviewer;

import com.example.stlviewer.control.masterController;
import com.example.stlviewer.res.Strings;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static com.example.stlviewer.util.RuntimeHandler.logMessage;

public class Main extends Application
{
    masterController appController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Retrieve the arguments passed to the application
        List<String> args = getParameters().getRaw();

        // Use the arguments to initialize masterController, use default values if no arguments are provided
        String arg1 = !args.isEmpty() ? args.get(0) : Strings.TCP_MODE;
        boolean arg2 = args.size() <= 1 || Boolean.parseBoolean(args.get(1));

        appController = new masterController(stage, arg1, arg2);

        // Set up the stage close request to terminate the application
        stage.setOnCloseRequest(event -> {
            logMessage(Strings.CLOSING_APPLICATION);
            try {
                stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        });
    }

    @Override
    public void stop() {
        // Terminate all processes and threads
        appController.terminate();
    }
}