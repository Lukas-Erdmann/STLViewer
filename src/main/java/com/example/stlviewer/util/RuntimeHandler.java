package com.example.stlviewer.util;

import com.example.stlviewer.res.Strings;

import java.util.concurrent.Callable;

/**
 * This class contains methods that handle exceptions and runtime errors. Additionally, it contains methods
 * that measure the runtime of a function. <br>
 *
 * @author  Lukas Erdmann
 */
public class RuntimeHandler
{
    /**
     * Stores the start time of a function.
     */
    private static long startTime;
    /**
     * Stores the end time of a function.
     */
    private static long endTime;

    /**
     * Accepts a non-void function as parameter and tries to execute it. If the function throws an exception,
     * it displays the correct error message and tries to execute the operation again. If the operation
     * fails 5 times, the program exits. <br>
     * Pre-condition: none <br>
     * Post-condition: none
     *
     * @author  Lukas Erdmann
     * @param <T>           Type of the function
     * @param function      Function to execute
     * @param maxTries      Maximum number of tries
     * @return              Result of the function
     */
    public static <T> T exceptionHandler (Callable<T> function, int maxTries) {
        int tries = 0;
        // Try to execute the function until it succeeds or the maximum number of tries is reached
        while (tries++ <= maxTries) {
            try {
                return function.call();
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                if (tries <= maxTries)
                {
                    // If the maximum number of tries is not reached, display the number of tries
                    System.out.printf(Strings.EXCEPTION_HANDLER_LOOP, tries, maxTries);
                }
            }
        }
        // If the maximum number of tries is reached, display the exit message and exit the program
        System.out.println(Strings.EXCEPTION_HANDLER_EXIT);
        System.exit(1);
        return null;
    }

    /**
     * Accepts a void function as parameter and tries to execute it. If the function throws an exception,
     * the correct error message is displayed and the function is executed again. If the operation
     * fails 5 times, the program exits. <br>
     * Pre-condition: none <br>
     * Post-condition: none
     * @author  Lukas Erdmann
     * @param function      Function to execute
     * @param maxTries      Maximum number of tries
     */
    public static void exceptionHandler (Runnable function, int maxTries) {
        int tries = 0;
        // Try to execute the function until it succeeds or the maximum number of tries is reached
        while (tries++ <= maxTries) {
            try {
                function.run();
                return;
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                if (tries <= maxTries)
                {
                    // If the maximum number of tries is not reached, display the number of tries
                    System.out.printf(Strings.EXCEPTION_HANDLER_LOOP, tries, maxTries);
                }
            }
        }
        // If the maximum number of tries is reached, display the exit message and exit the program
        System.out.println(Strings.EXCEPTION_HANDLER_EXIT);
        System.exit(1);
    }

    /**
     * Measures the current time in milliseconds and stores it in the startTime variable. This method
     * is used in conjunction with the stopTimer() method to measure the execution time of a function. <br>
     * Pre-condition: System.currentTimeMillis() must be available. <br>
     * Post-condition: none
     *
     * @author  Lukas Erdmann
     * @see #stopTimer()
     */
    public void startTimer()
    {
        startTime = System.currentTimeMillis();
    }

    /**
     * Measures the current time in milliseconds and stores it in the endTime variable. This method
     * is used in conjunction with the startTimer() method to measure the execution time of a function. <br>
     * Pre-condition: System.currentTimeMillis() must be available. <br>
     * Post-condition: none
     *
     * @author  Lukas Erdmann
     * @see #startTimer()
     */
    public void stopTimer()
    {
        endTime = System.currentTimeMillis();
    }

    /**
     * Returns the difference between the endTime and startTime variables. This method is used in conjunction with the
     * startTimer() and stopTimer() methods to measure the execution time of a function. <br>
     * Pre-condition: The startTimer() and stopTimer() methods must be called before this method. <br>
     * Post-condition: none
     *
     * @author  Lukas Erdmann
     * @return  The difference between the endTime and startTime variables.
     * @see #stopTimer()
     * @see #startTimer()
     */
    public long getElapsedTime()
    {
        return endTime - startTime;
    }
}
