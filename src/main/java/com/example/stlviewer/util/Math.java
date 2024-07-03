package com.example.stlviewer.util;

/**
 * The Math class contains static methods for mathematical operations.
 */
public abstract class Math
{
    /**
     * Finds the maximum double in the given array.
     *
     * @param array - The array of doubles.
     * @return The maximum double in the array.
     */
    public static double findMaxDouble (double[] array)
    {
        double max = array[0];
        for (int i = 1; i < array.length; i++)
        {
            if (array[i] > max)
            {
                max = array[i];
            }
        }
        return max;
    }
}
