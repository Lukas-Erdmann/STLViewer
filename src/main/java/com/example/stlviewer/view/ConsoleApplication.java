package com.example.stlviewer.view;

import com.example.stlviewer.model.Polyhedron;

import java.util.Scanner;

public class ConsoleApplication
{
    private Scanner scanner;

    public ConsoleApplication ()
    {
        this.scanner = new Scanner(System.in);
    }

    public String requestFileNameAndPath()
    {
        // Request the file name and path from the user
        System.out.println("Please enter the file name and path:");
        String fileNameAndPath = scanner.nextLine();

        // Check if the file exists and is an STL file
        if (fileNameAndPath.endsWith(".stl"))
        {
            return fileNameAndPath;
        }
        else
        {
            System.out.println("The file does not exist or is not an STL file. Please enter a valid STL file path.");
            // Recursively call the method to request the file name and path again
            return requestFileNameAndPath();
        }
    }

    public void printPolyhedronData(Polyhedron polyhedron)
    {
        System.out.println(polyhedron.toString());
    }

    public void printTriangleSingleData(Polyhedron polyhedron, int index)
    {
        System.out.println(polyhedron.getTriangles().get(index));
    }

    public void printTriangleAllData(Polyhedron polyhedron)
    {
        for (int i = 0; i < polyhedron.getTriangles().size(); i++)
        {
            System.out.println(polyhedron.getTriangles().get(i));
        }
    }
}
