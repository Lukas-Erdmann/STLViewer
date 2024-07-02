package com.example.stlviewer.util;

public abstract class Math
{
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
