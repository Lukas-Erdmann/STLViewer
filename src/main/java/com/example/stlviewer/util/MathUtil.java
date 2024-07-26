package com.example.stlviewer.util;

/**
 * The Math class contains static methods for mathematical operations.
 */
public abstract class MathUtil
{
    /**
     * Rounds a double value to a specified number of decimal places.
     *
     * @param value  - The value to round.
     * @param digits - The number of decimal places to round to.
     * @return The rounded value.
     */
    public static double roundToDigits(double value, int digits)
    {
        double factor = Math.pow(10, digits);
        return Math.round(value * factor) / factor;
    }

    public static double roundToThreeDigits(double value)
    {
        return roundToDigits(value, 3);
    }
}
