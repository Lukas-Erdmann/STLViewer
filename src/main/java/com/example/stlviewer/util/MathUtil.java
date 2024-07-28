package com.example.stlviewer.util;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;

/**
 * The Math class contains static methods for mathematical operations.
 */
public abstract class MathUtil
{

    /**
     * Rounds a double value to a specified number of decimal places.
     *
     * @param value     The value to round.
     * @param digits    The number of decimal places to round to.
     * @return          The rounded value.
     */
    public static double roundToDigits(double value, int digits)
    {
        double factor = Math.pow(10, digits);
        return Math.round(value * factor) / factor;
    }

    /**
     * Rounds a double value to three decimal places.
     *
     * @param value     The value to round.
     * @return          The rounded value.
     */
    public static double roundToThreeDigits(double value)
    {
        return roundToDigits(value, 3);
    }

    /**
     * Rounds a double value so that the first two non-zero digits are preserved.
     *
     * @param value     The value to round.
     * @return          The rounded value.
     */
    public static double roundToTwoNonZeroDigits(double value) {
        String valueStr = Double.toString(value);
        int nonZeroCount = 0;
        int roundPosition = 0;

        // Detect if the number is so small it's represented in exponential notation
        if (valueStr.contains(Strings.EXP_DELIMITER_NEGATIVE)) {
            // Set the round position to number following the E
            roundPosition = Math.abs(Integer.parseInt(valueStr.substring(valueStr.indexOf
                    (Strings.EXP_DELIMITER_NEGATIVE) + Constants.N_ONE))) + Constants.N_TWO;
        } else
        {
            // Find the first two non-zero digits
            for (int i = 0; i < valueStr.length(); i++)
            {
                char c = valueStr.charAt(i);
                if (c != Strings.CHAR_ZERO && c != Strings.CHAR_DOT && c != Strings.CHAR_DASH)
                {
                    nonZeroCount++;
                    if (nonZeroCount == Constants.N_TWO)
                    {
                        roundPosition = i - valueStr.indexOf(Strings.CHAR_DOT);
                        break;
                    }
                }
            }
        }

        return roundToDigits(value, Math.max(roundPosition, Constants.N_TWO));
    }
}
