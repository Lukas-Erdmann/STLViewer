package com.example.stlviewer.view;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.res.Strings;

import java.util.Scanner;

/**
 * The ConsoleApplication class provides a console-based interface for interacting with Polyhedron objects.
 * It allows the user to input the path to an STL file, and print information about the Polyhedron and its triangles.
 */
public class ConsoleApplication
{
    /**
     * The Scanner object for reading user input.
     */
    private Scanner scanner;

    /**
     * Constructs a ConsoleApplication with a new Scanner for user input.
     * Precondition: None.
     * Postcondition: A ConsoleApplication instance is created with a new Scanner.
     */
    public ConsoleApplication ()
    {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts the user to enter the path to an STL file. Checks if the file exists and is an STL file.
     * Precondition: Scanner object should be initialized.
     * Postcondition: Returns a valid STL file path entered by the user.
     *
     * @return A valid STL file path.
     */
    public String askForFileName ()
    {
        // Request the file name and path from the user
        System.out.println(Strings.CONSAPP_PLEASE_ENTER_THE_FILE_NAME_AND_PATH);
        String fileNameAndPath = scanner.nextLine();

        // Check if the file exists and is an STL file
        if (fileNameAndPath.endsWith(Strings.STL_FILE_SUFFIX))
        {
            return fileNameAndPath;
        } else
        {
            System.out.println(Strings.CONSAPP_FILE_DOESNT_EXIST);
            // Recursively call the method to request the file name and path again
            return askForFileName();
        }
    }

    /**
     * Prints the details of the given Polyhedron to the console.
     * Precondition: The Polyhedron object should be initialized.
     * Postcondition: The Polyhedron details are printed to the console.
     *
     * @param polyhedron - The Polyhedron object to print.
     */
    public void printPolyhedronData (Polyhedron polyhedron)
    {
        System.out.println(polyhedron.toString());
    }

    /**
     * Terminates the Scanner object used for user input. <br>
     * Precondition: None. <br>
     * Post-Condition: The Scanner object is closed.
     */
    public void terminate ()
    {
        scanner.close();
    }
}
