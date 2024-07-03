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
    int NUMBER_ZERO = 0;
    /**
     * The number 1.
     */
    int NUMBER_ONE = 1;
    /**
     * The number 2.
     */
    int STL_BINARY_OFFSET_X = NUMBER_ZERO;
    int NUMBER_TWO = 2;
    int TRIANGLE_VERTEX_COUNT = 3;
    int TRIANGLE_VERTEX1_INDEX = 0;
    int TRIANGLE_VERTEX2_INDEX = 1;
    int TRIANGLE_VERTEX3_INDEX = 2;
    int WINDOW_WIDTH = 1200;
    int WINDOW_HEIGHT = 800;
    int INFOBAR_WIDTH = 200;
    String STL_ASCII_VERTEX_START_TAG = "vertex";
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
}
