package com.example.stlviewer;

import com.example.stlviewer.control.ApplicationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start (Stage stage) throws IOException
    {
        ApplicationController appController = new ApplicationController();

        appController.startViewer(stage);
    }

    public static void main (String[] args)
    {
        launch();
    }
}