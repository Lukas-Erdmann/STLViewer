package com.example.stlviewer.res;

/**
 * Class Constants. Contains numerical constants used in the program. <br>
 *
 * @author Lukas Erdmann
 */
public interface Constants
{
    // Numbers
    /**
     * The number -1.
     */
    int NUMBER_MINUS_ONE = -1;
    /**
     * The number 0.
     */
    int N_ZERO = 0;
    /**
     * The number 1.
     */
    int N_ONE = 1;
    /**
     * The number 2.
     */
    int N_TWO = 2;
    /**
     * The number 3.
     */
    int N_THREE = 3;
    int STL_BINARY_OFFSET_X = 0;
    int TRIANGLE_VERTEX_COUNT = 3;
    int TRIANGLE_VERTEX1_INDEX = 0;
    int TRIANGLE_VERTEX2_INDEX = 1;
    int TRIANGLE_VERTEX3_INDEX = 2;
    int WINDOW_WIDTH = 1200;
    int WINDOW_HEIGHT = 800;
    int INFOBAR_WIDTH = 200;
    int STL_ASCII_NORMAL_WORD_POS_X = 2;
    int STL_ASCII_NORMAL_WORD_POS_Y = 3;
    int STL_ASCII_NORMAL_WORD_POS_Z = 4;
    int STL_ASCII_FACET_WORDCOUNT = 5;
    int STL_ASCII_FACET_START_TAG_POS = 0;
    int STL_ASCII_NORMAL_TAG_POS = 1;
    int STL_ASCII_NORMAL_WORDCOUNT = 4;
    int STL_ASCII_VERTEX_START_TAG_POS = 0;
    int STL_ASCII_VERTEX_WORD_POS_X = 1;
    int STL_ASCII_VERTEX_WORD_POS_Y = 2;
    int STL_ASCII_VERTEX_WORD_POS_Z = 3;
    int STL_BINARY_HEADER_BYTE_SIZE = 80;
    int STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE = 4;
    int STL_BINARY_NORMAL_BYTE_SIZE = 12;
    int STL_BINARY_TRIANGLES_BYTE_SIZE = 12;
    int STL_BINARY_ATTR_BYTE_SIZE = 2;
    int STL_BINARY_VERTEX_BYTE_SIZE = 4;
    int STL_BINARY_OFFSET_Y = 4;
    int STL_BINARY_OFFSET_Z = 8;
    int SERVER_PORT = 9756;
    int BOUNDING_BOX_MIN_X_INDEX = 0;
    int BOUNDING_BOX_MIN_Y_INDEX = 1;
    int BOUNDING_BOX_MIN_Z_INDEX = 2;
    int BOUNDING_BOX_MAX_X_INDEX = 3;
    int BOUNDING_BOX_MAX_Y_INDEX = 4;
    int BOUNDING_BOX_MAX_Z_INDEX = 5;
    int FACE_FIRST = 0;
    int FACE_SECOND = 1;
    int FACE_THIRD = 2;
    int COMMAND_WORDCOUNT = 3;
    int COMMAND_TYPE_INDEX = 0;
    int COMMAND_AXIS_INDEX = 1;
    int COMMAND_AMOUNT_INDEX = 2;
    int AXIS_X_INDEX = 0;
    int AXIS_Y_INDEX = 1;
    int AXIS_Z_INDEX = 2;
    int P2P_PORT_1 = 9757;
    int P2P_PORT_2 = 9758;
    int Z_DISTANCE_FACTOR = 4;
    int BOUNDING_BOX_SIZE = 6;
    int THREAD_SLEEP_MILLIS = 10;
    int ID_COUNTER_START = 0;
    double NORMAL_DIFFERENCE_ROUNDING_TOLERANCE = 0.0001;
    int DOT_PRODUCT_PRECISION = 5;
    int TRIANGLE_BYTE_SIZE = 50;
    int BYTES_TO_SKIP = 100;
    double FACTOR_MASS_DEFAULT = 1.0;
    double FACTOR_LENGTH_DEFAULT = 1.0;
    double INCH_TO_METER = 0.0254;
    double KG_TO_LB = 2.20462;
    int MATERIAL_STRING_POS_NAME = 0;
    int MATERIAL_TO_STRING_DESC = 1;
    int MATERIAL_TO_STRING_DENSITY = 2;
    int MATERIAL_TO_STRING_COLOR = 3;
    int MATERIAL_TO_STRING_SPECULAR_COLOR = 4;
    int MATERIAL_TO_STRING_SPECULAR_POWER = 5;
}
