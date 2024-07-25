package com.example.stlviewer;

import com.example.stlviewer.control.masterController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application
{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Retrieve the arguments passed to the application
        List<String> args = getParameters().getRaw();

        // Use the arguments to initialize masterController
        String arg1 = !args.isEmpty() ? args.get(0) : "TCP";
        boolean arg2 = args.size() <= 1 || Boolean.parseBoolean(args.get(1));

        masterController appController = new masterController(arg1, arg2);
    }
}