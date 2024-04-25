package com.example.stlviewer.util;

import com.example.stlviewer.model.ConsoleMessages;

import java.util.concurrent.Callable;

public class RuntimeHandler
{
    /**
     * Accepts a non-void function as parameter and tries to execute it. If the function throws an exception,
     * it displays the correct error message and tries to execute the operation again. If the number operation
     * fails exceeds maxTries, the program exits. <br>
     * Pre-condition: none <br>
     * Post-condition: none
     *
     * @param <T>      Type of the function
     * @param function Function to execute
     * @param maxTries Maximum number of tries
     * @return Result of the function
     * @author Lukas Erdmann
     */
    public static <T> T exceptionHandler (Callable<T> function, int maxTries)
    {
        int tries = 0;
        // Try to execute the function until it succeeds or the maximum number of tries is reached
        while (tries++ <= maxTries)
        {
            try
            {
                return function.call();
            } catch (Throwable e)
            {
                System.out.println(e.getMessage());
                if (tries <= maxTries)
                {
                    // If the maximum number of tries is not reached, display the number of tries
                    System.out.printf(ConsoleMessages.EXCEPTION_HANDLER_LOOP, tries, maxTries);
                }
            }
        }
        // If the maximum number of tries is reached, display the exit message and exit the program
        System.out.println(ConsoleMessages.EXCEPTION_HANDLER_EXIT);
        System.exit(1);
        return null;
    }

    /**
     * Accepts a void function as parameter and tries to execute it. If the function throws an exception,
     * the correct error message is displayed and the function is executed again. If the number operation
     * fails exceeds maxTries, the program exits. <br>
     * Pre-condition: none <br>
     * Post-condition: none
     *
     * @param function Function to execute
     * @param maxTries Maximum number of tries
     * @author Lukas Erdmann
     */
    public static void exceptionHandler (Runnable function, int maxTries)
    {
        int tries = 0;
        // Try to execute the function until it succeeds or the maximum number of tries is reached
        while (tries++ <= maxTries)
        {
            try
            {
                function.run();
                return;
            } catch (Throwable e)
            {
                System.out.println(e.getMessage());
                if (tries <= maxTries)
                {
                    // If the maximum number of tries is not reached, display the number of tries
                    System.out.printf(ConsoleMessages.EXCEPTION_HANDLER_LOOP, tries, maxTries);
                }
            }
        }
        // If the maximum number of tries is reached, display the exit message and exit the program
        System.out.println(ConsoleMessages.EXCEPTION_HANDLER_EXIT);
        System.exit(1);
    }
}
