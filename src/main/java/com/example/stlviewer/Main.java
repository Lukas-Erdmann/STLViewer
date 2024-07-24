package com.example.stlviewer;

import com.example.stlviewer.control.masterController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    public static void main (String[] args)
    {
        launch();
    }

    @Override
    public void start (Stage stage) throws IOException
    {
        masterController appController = new masterController("P2P", true);
    }
}