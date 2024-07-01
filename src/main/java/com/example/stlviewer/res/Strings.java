package com.example.stlviewer.res;

/**
 * Class Strings. Contains all strings used in the program. <br>
 *
 * @author  Lukas Erdmann
 */
public interface Strings
{
    // Exception messages
    /**
     * Exception message for invalid float values.
     */
    String TYPE_EXCEPTION_FLOAT = " is not a valid float.";
    /**
     * Exception message for invalid double values.
     */
    String TYPE_EXCEPTION_DOUBLE = " is not a valid double.";
    /**
     * Exception message for invalid integer values.
     */
    String TYPE_EXCEPTION_INT = " is not a valid integer.";
    /**
     * Exception message for error in csv file line.
     */
    String ERROR_IN_LINE = "Error in line %1$d: %2$s\n";
    /**
     * Generic exception message for exceptions caught in the exception handler.
     */
    String EXCEPTION_HANDLER_LOOP = "Trying again. (%d/%d)%n\n";
    /**
     * Exception message for too many exceptions caught in the exception handler.
     */
    String EXCEPTION_HANDLER_EXIT = "Exiting program.";
    /**
     * Exception message if file is not found.
     */
    String FILE_NOT_FOUND = "File not found: ";
    /**
     * Exception message if file is empty.
     */
    String FILE_IS_EMPTY = "File is empty: ";
    /**
     * Exception message for when reading the file fails.
     */
    String ERROR_WHILE_READING_FILE = "Error while reading file: ";
    /**
     * Exception message for invalid integer input.
     */
    String NOT_AN_INTEGER = "Input is not a valid integer.";
    /**
     * Generic exception message if the user enters an invalid input.
     */
    String INVALID_INPUT = "Invalid input. Please try again.";

    // Symbols
    /**
     * Constant for new line.
     */
    String LINE_DELIMITER = "\r\n";
    /**
     * Constant for end of file.
     */
    String END_OF_FILE = "\\Z";
    /**
     * Constant for semicolon.
     */
    String SEMICOLON = ";";
    /**
     * Constant for comma.
     */
    String COMMA = ",";
    /**
     * Dash.
     */
    String DASH = "-";
    /**
     * Constant for space.
     */
    String SPACE = " ";
    /**
     * Character for a new line.
     */
    String NEW_LINE = "\n";

    // Messages
    /**
     * Message that is displayed when the program starts.
     */
    String WELCOME_MESSAGE = "Welcome to the charging station path finder!";
    /**
     * Format string for the execution time.
     */
    String EXECUTION_TIME = "Execution time: %1$d ms\n";
    /**
     * Message that displays the number of edges.
     */
    String NUMBER_OF_EDGES = "Number of edges: ";

    // Other
    /**
     * Spacer for console output.
     */
    String SPACER = "-------------------------------------------------------------------------------------------------";
    /**
     * Empty string.
     */
    String EMPTY_STRING = "";

    // Formatting
    /**
     * Format string for 2-digit float values.
     */
    String FORMAT_2F = "%.2f";


    String ANY_NUMBER_OF_WHITESPACES = "\\s+";
    String INVALID_NORMAL_LINE = "Invalid normal line: ";
    String ERROR_PARSING_NORMAL = "Error parsing normal: ";
    String INVALID_TRIANGLE_LINE = "Invalid triangle line: ";
    String ERROR_WHILE_READING_TRIANGLE = "Error while reading triangle: ";
    String INVALID_VERTEX_LINE = "Invalid vertex line: ";
    String ERROR_PARSING_VERTEX = "Error parsing vertex: ";
}
