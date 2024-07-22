package com.example.stlviewer.res;

/**
 * Class Strings. Contains all strings used in the program. <br>
 *
 * @author Lukas Erdmann
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

    // Other
    /**
     * Empty string.
     */
    String EMPTY_STRING = "";

    // Formatting


    String ANY_NUMBER_OF_WHITESPACES = "\\s+";
    String INVALID_NORMAL_LINE = "Invalid normal line: ";
    String ERROR_PARSING_NORMAL = "Error parsing normal: ";
    String INVALID_TRIANGLE_LINE = "Invalid triangle line: ";
    String ERROR_WHILE_READING_TRIANGLE = "Error while reading triangle: ";
    String INVALID_VERTEX_LINE = "Invalid vertex line: ";
    String ERROR_PARSING_VERTEX = "Error parsing vertex: ";
    String AXIS_X = "x";
    String AXIS_Y = "y";
    String AXIS_Z = "z";
    String INVALID_AXIS = "Invalid axis!";
    String WINDOW_TITLE = "STL Viewer | ";
    String SOUT_READING_ASCII_FILE = "Reading ASCII file";
    String SOUT_READING_BINARY_FILE = "Reading binary file";
    String STL_ASCII_START_TAG = "solid";
    String STL_ASCII_FACET_START_TAG = "facet";
    String STL_ASCII_NORMAL_TAG = "normal";
    String STL_ASCII_TRIANGLE_START_TAG = "outer loop";
    String THREAD_WAS_INTERRUPTED = "Thread was interrupted";
    String CONSAPP_FILE_DOESNT_EXIST = "The file does not exist or is not an STL file. Please enter a valid STL file path.";
    String CONSAPP_PLEASE_ENTER_THE_FILE_NAME_AND_PATH = "Please enter the file name and path:";
    String STL_FILE_SUFFIX = ".stl";
    String SOUT_ELAPSED_TIME = "Elapsed time: %d ms\n";
    String SOUT_READING_FINISHED = "Reading finished";
    String LOCALHOST = "localhost";
    String EXCEPTION_WHEN_ATTEMPTING_TO_START_STL_VIEWER = "Exception when attempting to start stlViewer";
    String UNABLE_TO_START_TCP_CONNECTION = "Unable to start TCP connection";
    String THE_EDGE_IS_ALREADY_IN_THE_CHAIN = "The edge is already in the chain.";
    String THE_START_VERTEX_OF_THE_EDGE_IS_NOT_THE_LAST_VERTEX_OF_THE_CHAIN = "The start vertex of the edge is not the last vertex of the chain.";
    String POLYGONAL_CHAIN_TOSTRING = "PolygonalChain{";
    String COMMA_SPACE = ", ";
    String CURLY_BRACKET_RIGHT = "}";
    String POLYHEDRON_TOSTRING = "Polyhedron{";
    String POLYHEDRON_TOSTRING_2 = "volume=";
    String POLYHEDRON_TOSTRING_3 = ", surfaceArea=";
    String POLYHEDRON_TOSTRING_4 = ", boundingBox=";
    String POLYHEDRON_TOSTRING_5 = ", center=";
    String POLYHEDRON_TOSTRING_6 = ", triangles=";
    String VERTEX_TOSTRING = "Vertex{";
    String CONNECTING_TO_SERVER_AT = "Connecting to server at %s:%d%n";
    String FAILED_CONNECTING_TO_SERVER = "Failed connecting to server: ";
    String ENTER_COMMAND_TYPE = "Enter command type (rotate, translate): ";
    String COMMAND_TYPE_ROTATE = "rotate";
    String COMMAND_TYPE_TRANSLATE = "translate";
    String INVALID_COMMAND_TYPE = "Invalid command type. Please enter rotate or translate.";
    String ENTER_AXIS = "Enter axis (x, y, z || x, y for rotate): ";
    String INVALID_AXIS_REPEAT = "Invalid axis. Please enter x, y, or z.";
    String ENTER_AMOUNT = "Enter amount: ";
    String INVALID_AMOUNT = "Invalid amount. Please enter a valid number.";
    String CLIENT_CONNECTED = "Client connected: ";
    String AN_EXCEPTION_OCCURRED_PROCESSING_CLIENT_COMMANDS = "An Exception occurred processing client commands: ";
    String AN_EXCEPTION_OCCURRED_CLOSING_CLIENT_SOCKET = "An Exception occurred closing client socket: ";
    String EXECUTED_COMMAND = "Executed command: ";
    String AN_EXCEPTION_OCCURRED_EXECUTING_THE_COMMAND = "An Exception occurred executing the command: ";
    String INVALID_COMMAND_EXPECTED_FORMAT = "Invalid command: %s. Expected format: <command> <axis> <amount>";
    String INVALID_VALUE_FOR_AMOUNT = "Invalid value for amount: ";
    String COMMAND_COULDN_T_BE_EXECUTED = "Command couldn't be executed: ";
    String SERVER_IS_AVAILABLE_ON_PORT = "The server is available on port: ";
    String EXCEPTION_SERVER_START = "An error occurred while starting the server on port %d: %s%n";
    String STLV_MENU = "File";
    String STLV_OPEN_FILE = "Open File...";
    String STLV_EXIT = "Exit";
    String STLV_OPEN_STL_FILE = "Open STL File";
    String STLV_STL_FILES = "STL Files";
    String STL_FILE_SUFFIX_ALL = "*.stl";
    String STLV_EDIT = "Edit";
    String STLV_SET_COLOR = "Set Color...";
    String STLV_SET_COLORS = "Set Colors";
    String STLV_3D_MODEL_COLOR = "3D Model Color:";
    String STLV_BACKGROUNG_COLOR = "Background Color:";
    String STLV_VIEW = "View";
    String STLV_TRANSLATE = "Translate...";
    String STLV_ROTATE = "Rotate...";
    String STLV_SET_ZOOM = "Set Zoom...";
    String STLV_RESET_VIEW = "Reset View";
    String STLV_TRANSLATE_MODEL = "Translate Model";
    String STLV_X_COLON = "X:";
    String STLV_Y_COLON = "Y:";
    String STLV_ROTATE_MODEL = "Rotate Model";
    String STLV_SET_ZOOM_2 = "Set Zoom";
    String STLV_ZOOM_FACTOR = "Zoom Factor";
    String COLON = ":";
    String STLV_MODEL_INFORMATION = "Model Information";
    String STLV_NUMBER_OF_TRIANGLES = "Number of Triangles: ";
    String STLV_SURFACE_AREA = "Surface Area: ";
    String STLV_VOLUME = "Volume: ";
    String STLV_VIEW_PROPERTIES = "View Properties";
    String STLC_ROTATION_LABEL = "Rotation: ";
    String STLV_TRANSLATION_LABEL = "Translation: ";
    String STLV_VIEWPROP_ROTATE = "X: %.2f, Y: %.2f";
    String STLV_VIEWPROP_TRANSLATE = "X: %.2f, Y: %.2f, Z: %.2f";
    String ARIAL = "Arial";
    String FORMAT_STRING_2F = "%.2f";
    String P2PPACKAGE_TOSTRING = "P2PPackage{";
    String P2PPACKAGE_TOSTRING_2 = "polyhedron=";
    String P2PPACKAGE_TOSTRING_3 = ", translate=";
    String P2PPACKAGE_TOSTRING_4 = ", rotate=";
    String P2PPACKAGE_TOSTRING_5 = ", zoom=";
    String P2PPACKAGE_TOSTRING_6 = ", anchorAngles=";
    String INVALID_P2P_PACKAGE = "Invalid P2P package.";
    String UNABLE_TO_START_P2P_CONNECTION = "Unable to start P2P connection.";
    String TRIANGLE_TOSTRING = "Triangle{";
    String TRIANGLE_TOSTRING_2 = "area = ";
    String TRIANGLE_TOSTRING_3 = "normal = ";
    String EDGE_TOSTRING = "Edge{";
    String ARROW_RIGHT = " -> ";
    String THE_POLYGON_IS_ALREADY_CLOSED = "The polygon is already closed.";
}
