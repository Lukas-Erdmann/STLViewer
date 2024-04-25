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
     * Exception message for latitude values.
     */
    String GEO_COORDINATE_EXCEPTION_LAT = "Latitude value [%1$.2f] must be between %2$.2f and %3$.2f.";
    /**
     * Exception message for longitude values.
     */
    String GEO_COORDINATE_EXCEPTION_LON = "Longitude value [%1$.2f] must be between %2$.2f and %3$.2f.";
    /**
     * Exception message for empty street names.
     */
    String ADDRESS_EXCEPTION_STREET = "Street must not be empty";
    /**
     * Exception message for empty house numbers.
     */
    String ADDRESS_EXCEPTION_HOUSE_NUMBER = "House number must not be empty";
    /**
     * Exception message for postal codes that are not 5-digit numbers.
     */
    String ADDRESS_EXCEPTION_POSTAL_CODE = "Postal code must be a positive five-digit number";
    /**
     * Exception message for empty city names.
     */
    String ADDRESS_EXCEPTION_CITY = "City must not be empty";
    /**
     * Exception message for empty state names.
     */
    String ADDRESS_EXCEPTION_STATE = "State must not be empty";
    /**
     * Exception message for empty operator names.
     */
    String CHARGING_STATION_EXCEPTION_OPERATOR = "Operator must not be empty";
    /**
     * Exception message for empty address objects.
     */
    String CHARGING_STATION_EXCEPTION_ADDRESS = "Address must not be empty";
    /**
     * Exception message for empty geo coordinate objects.
     */
    String CHARGING_STATION_EXCEPTION_GEO_COORDINATE = "Geo coordinate must not be empty";
    /**
     * Exception message for non-positive power values.
     */
    String CHARGING_STATION_EXCEPTION_POWER = "Power in kW must be a positive number";
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
     * Exception message for non-positive minimum distance values.
     */
    String MIN_DISTANCE_NEGATIVE = "Minimum distance must be a positive number.";
    /**
     * Exception message for non-positive maximum distance values.
     */
    String MAX_DISTANCE_NEGATIVE = "Maximum distance must be a positive number.";
    /**
     * Exception message if file is not found.
     */
    String FILE_NOT_FOUND = "File not found: %s";
    /**
     * Exception message for invalid integer input.
     */
    String NOT_AN_INTEGER = "Input is not a valid integer.";
    /**
     * Exception message for invalid positive integer input.
     */
    String NOT_A_POSITIVE_INTEGER = "Input is not a positive integer.";
    /**
     * Exception message for invalid postal code input.
     */
    String POSTAL_CODE_NOT_FOUND = "Postal code not found: %d";
    /**
     * Exception message if list of charging stations is null or empty.
     */
    String NO_CHARGING_STATIONS = "List of charging stations is null or empty.";
    /**
     * Generic exception message if the user enters an invalid input.
     */
    String INVALID_INPUT = "Invalid input. Please try again.";
    /**
     * Exception message if the maxStringLength() method is called with an out-of-bounds index.
     */
    String INVALID_POSITION = "Invalid position. Please try again.";
    /**
     * Message that is displayed when the pathfinder is unable to find a path.
     */
    String NO_PATH_FOUND = "No path found.";

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
    /**
     * Constant for an arrow.
     */
    String ARROW = " -> ";
    /**
     * Constant for a column separator.
     */
    String COLUMN_SEPARATOR = " | ";

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
     * Message that prompts the user to enter the minimum distance.
     */
    String PROMPT_MIN_DISTANCE = "Please enter the minimum distance in whole km: ";
    /**
     * Message that prompts the user to enter the maximum distance.
     */
    String PROMPT_MAX_DISTANCE = "Please enter the maximum distance in whole km: ";
    /**
     * Message that prompts the user to enter the postal code of the start location.
     */
    String PROMPT_POSTAL_CODE_START = "Please enter the postal code of the start location: ";
    /**
     * Message that prompts the user to enter the postal code of the end location.
     */
    String PROMPT_POSTAL_CODE_END = "Please enter the postal code of the end location: ";
    /**
     * Message that displays the number of imported charging stations.
     */
    String NUMBER_OF_IMPORTED_CHARGING_STATIONS = "Number of imported charging stations: ";
    /**
     * Message that displays the number of imported charging stations.
     */
    String NUMBER_OF_CHARGING_STATIONS = "Number of charging stations: ";
    /**
     * Message that displays the number of nodes.
     */
    String NUMBER_OF_NODES = "Number of nodes: ";
    /**
     * Message that displays the number of edges.
     */
    String NUMBER_OF_EDGES = "Number of edges: ";
    /**
     * Format string for node's number of connections.
     */
    String GRAPH_EDGES = "Node %1$d - Number of Connections: %2$d\n";
    /**
     * Message that prompts the user at start of the program to continue or exit.
     */
    String PROMPT_CONTINUE = "To continue, press enter. To exit, type 'exit' and press enter. ";
    /**
     * Message that describes the program's functionality.
     */
    String PROGRAM_DESCRIPTION = "This program imports charging stations from a csv file, validates the data and creates a graph.\n" +
            "The user can then enter a start and end location and the program will determine if there is a path between the two locations. ";

    /**
     * Message that is displayed when the data is imported from the csv file and validated.
     */
    String IMPORT_DATA = "Importing data from csv file at %s...%n";
    /**
     * Message that is displayed when the data is sorted.
     */
    String SORT_DATA = "Sorting data...";
    /**
     * Message that is displayed when the data is filtered.
     */
    String FILTER_DATA = "Removing redundant data...";
    /**
     * Message that is displayed when the graph is created.
     */
    String CREATE_GRAPH = "Creating graph...";
    /**
     * Message that is displayed when the program is looking for a path.
     */
    String FIND_PATH = "Looking for path...";

    // Other
    /**
     * Spacer for console output.
     */
    String SPACER = "-------------------------------------------------------------------------------------------------";
    /**
     * Exit command.
     */
    String INPUT_EXIT = "exit";
    /**
     * Empty string.
     */
    String EMPTY_STRING = "";

    // Formatting
    /**
     * Format string for 2-digit float values.
     */
    String FORMAT_2F = "%.2f";
    /**
     * File path for the csv file.
     */
    String CSV_FILE_PATH = "src/res/LadeStationen.csv";
    /**
     * Data delimiter for the csv file.
     */
    String DATA_DELIMITER = ";";
    /**
     * Format string for the charging station's operator.
     */
    String OPERATOR = "Operator";
    /**
     * Format string for the charging station's street and house number.
     */
    String STREET_HOUSE_NUMBER = "Street";
    /**
     * Format string for the charging station's postal code.
     */
    String POSTAL_CODE = "PLZ";
    /**
     * Format string for the charging station's city.
     */
    String CITY = "City";
    /**
     * Format string for the charging station's state.
     */
    String STATE = "State";
    /**
     * Format string for the charging station's latitude.
     */
    String LATITUDE = "Lat.";
    /**
     * Format string for the charging station's longitude.
     */
    String LONGITUDE = "Long.";
    /**
     * Format string for the charging station's output power.
     */
    String POWER = "Power";
}
