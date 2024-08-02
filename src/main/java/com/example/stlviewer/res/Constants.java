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
    int N_MINUS_ONE = -1;
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
    /**
     * The number 6.
     */
    int N_SIX = 6;
    /**
     * The number 10.
     */
    int N_TEN = 10;
    /**
     * The number of vertices in a triangle.
     */
    int TRIANGLE_VERTEX_COUNT = 3;
    /**
     * The index of the first vertex in a triangle.
     */
    int TRIANGLE_VERTEX1_INDEX = 0;
    /**
     * The index of the second vertex in a triangle.
     */
    int TRIANGLE_VERTEX2_INDEX = 1;
    /**
     * The index of the third vertex in a triangle.
     */
    int TRIANGLE_VERTEX3_INDEX = 2;
    /**
     * The window width.
     */
    int WINDOW_WIDTH = 1200;
    /**
     * The window height.
     */
    int WINDOW_HEIGHT = 800;
    /**
     * The width of the infobar.
     */
    int INFOBAR_WIDTH = 200;
    /**
     * The position of the x-coordinate in the normal array.
     */
    int STL_ASCII_NORMAL_WORD_POS_X = 2;
    /**
     * The position of the y-coordinate in the normal array.
     */
    int STL_ASCII_NORMAL_WORD_POS_Y = 3;
    /**
     * The position of the z-coordinate in the normal array.
     */
    int STL_ASCII_NORMAL_WORD_POS_Z = 4;
    /**
     * The number of words in a facet.
     */
    int STL_ASCII_FACET_WORDCOUNT = 5;
    /**
     * The position of the start tag in the facet array.
     */
    int STL_ASCII_FACET_START_TAG_POS = 0;
    /**
     * The position of the normal tag in the facet array.
     */
    int STL_ASCII_NORMAL_TAG_POS = 1;
    /**
     * The number of words in a normal.
     */
    int STL_ASCII_NORMAL_WORDCOUNT = 4;
    /**
     * The position of the start tag in the vertex array.
     */
    int STL_ASCII_VERTEX_START_TAG_POS = 0;
    /**
     * The position of the x-coordinate in the vertex array.
     */
    int STL_ASCII_VERTEX_WORD_POS_X = 1;
    /**
     * The position of the y-coordinate in the vertex array.
     */
    int STL_ASCII_VERTEX_WORD_POS_Y = 2;
    /**
     * The position of the z-coordinate in the vertex array.
     */
    int STL_ASCII_VERTEX_WORD_POS_Z = 3;
    /**
     * The number of bytes in the header of a binary STL file.
     */
    int STL_BINARY_HEADER_BYTE_SIZE = 80;
    /**
     * The number of bytes in the attribute of a triangle in a binary STL file.
     */
    int STL_BINARY_ATTR_BYTE_SIZE = 2;
    /**
     * The port of the server for TCP and P2P communication.
     */
    int SERVER_PORT = 9756;
    /**
     * The index of the minimum x-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MIN_X_INDEX = 0;
    /**
     * The index of the minimum y-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MIN_Y_INDEX = 1;
    /**
     * The index of the minimum z-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MIN_Z_INDEX = 2;
    /**
     * The index of the maximum x-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MAX_X_INDEX = 3;
    /**
     * The index of the maximum y-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MAX_Y_INDEX = 4;
    /**
     * The index of the maximum z-coordinate in the bounding box array.
     */
    int BOUNDING_BOX_MAX_Z_INDEX = 5;
    /**
     * The index of the triangle corresponding to the first face.
     */
    int FACE_FIRST = 0;
    /**
     * The index of the triangle corresponding to the second face.
     */
    int FACE_SECOND = 1;
    /**
     * The index of the triangle corresponding to the third face.
     */
    int FACE_THIRD = 2;
    /**
     * The number of words in a command.
     */
    int COMMAND_WORDCOUNT = 3;
    /**
     * The index of the command type in the command array.
     */
    int COMMAND_TYPE_INDEX = 0;
    /**
     * The index of the axis in the command array.
     */
    int COMMAND_AXIS_INDEX = 1;
    /**
     * The index of the amount in the command array.
     */
    int COMMAND_AMOUNT_INDEX = 2;
    /**
     * The port of the first P2P server.
     */
    int P2P_PORT_1 = 9757;
    /**
     * The port of the second P2P server.
     */
    int P2P_PORT_2 = 9758;
    /**
     * The factor to multiply the z-coordinate with to get the starting z-coordinate of the camera.
     */
    int Z_DISTANCE_FACTOR = 4;
    /**
     * The size of the bounding box array.
     */
    int BOUNDING_BOX_SIZE = 6;
    /**
     * The amount of time to sleep in milliseconds.
     */
    int THREAD_SLEEP_MILLIS = 10;
    /**
     * The starting value of the ID counter.
     */
    int ID_COUNTER_START = 0;
    /**
     * The tolerance for rounding errors.
     */
    double NORMAL_DIFFERENCE_ROUNDING_TOLERANCE = 0.001;
    /**
     * The precision for the dot product.
     */
    int DOT_PRODUCT_PRECISION = 5;
    /**
     * The amount of bytes in a triangle in a binary STL file.
     */
    int TRIANGLE_BYTE_SIZE = 50;
    /**
     * The amount of bytes to skip in a binary STL file when trying to read the next triangle.
     */
    int BYTES_TO_SKIP = 100;
    /**
     * The default mass factor.
     */
    double FACTOR_MASS_DEFAULT = 1.0;
    /**
     * The default length factor.
     */
    double FACTOR_LENGTH_DEFAULT = 1.0;
    /**
     * The conversion factor from inch to meter.
     */
    double INCH_TO_METER = 0.0254;
    /**
     * The conversion factor from kg to lb.
     */
    double KG_TO_LB = 2.20462;
    /**
     * The position of the name in the material string representation.
     */
    int MATERIAL_STRING_POS_NAME = 0;
    /**
     * The position of the description in the material string representation.
     */
    int MATERIAL_TO_STRING_DESC = 1;
    /**
     * The position of the density in the material string representation.
     */
    int MATERIAL_TO_STRING_DENSITY = 2;
    /**
     * The position of the color in the material string representation.
     */
    int MATERIAL_TO_STRING_COLOR = 3;
    /**
     * The position of the specular color in the material string representation.
     */
    int MATERIAL_TO_STRING_SPECULAR_COLOR = 4;
    /**
     * The position of the specular power in the material string representation.
     */
    int MATERIAL_TO_STRING_SPECULAR_POWER = 5;
    /**
     * The position of the name in the material string array.
     */
    int MAT_POS_NAME = 0;
    /**
     * The position of the density in the material string array.
     */
    int MAT_POS_DENSITY = 1;
    /**
     * The position of the color in the material string array.
     */
    int MAT_POS_COLORHEX = 2;
    /**
     * The position of the description in the material string array.
     */
    int MAT_POS_DESC = 3;
    /**
     * The position of the mode in the arguments array.
     */
    int ARGS_MODE = 0;
    /**
     * The position of the parallel flag in the arguments array.
     */
    int ARGS_PARALLEL = 1;
    /**
     * The default zoom limit.
     */
    int ZOOM_LIMIT_DEFAULT = 100;
    /**
     * The default zoom coefficient.
     */
    double ZOOM_COEFF_DEFAULT = 0.0001;
    /**
     * The default density.
     */
    int DENSITY_DEFAULT = 1;
    /**
     * The camera near clip value.
     */
    double CAMERA_NEAR_CLIP_VALUE = 0.001;
    /**
     * The camera far clip value.
     */
    int CAMERA_FAR_CLIP_VALUE = 10000;
    /**
     * 90 degrees.
     */
    int DEGREES_90 = 90;
    /**
     * 270 degrees.
     */
    int DEGREES_270 = 270;
    /**
     * 360 degrees.
     */
    int DEGREES_360 = 360;
    /**
     * The minimum zoom speed.
     */
    int ZOOM_SPEED_MIN = 1;
    /**
     * The maximum translation value.
     */
    int TRANSLATION_MAX = 0;
    /**
     * The float value 0.5.
     */
    float FLOATPOINT5 = 0.5f;
    /**
     * The unit factor for meters.
     */
    int UNIT_FACTOR_M = 1;
    /**
     * The unit factor for centimeters.
     */
    int UNIT_FACTOR_CM = 100;
    /**
     * The unit factor for millimeters.
     */
    int UNIT_FACTOR_MM = 1000;
    /**
     * The unit factor for kilograms.
     */
    int UNIT_FACTOR_KG = 1;
    /**
     * The unit factor for grams.
     */
    int UNIT_FACTOR_G = 1000;
    /**
     * The width of the info labels.
     */
    int INFO_LABELS_WIDTH = 200;
    /**
     * The padding of the info labels.
     */
    int INFO_LABELS_PADDING = 10;
    /**
     * The spacing of the info labels.
     */
    int INFO_LABELS_SPACING = 10;
    /**
     * The normal font size of the info labels.
     */
    int FONT_SIZE_LABEL_TEXT = 14;
    /**
     * The font size of the title labels.
     */
    int FONT_SIZE_LABELS_TITLE = 16;
    /**
     * The maximum zoom limit on the slider.
     */
    double ZOOM_LIMIT_MAX = 10000;
    /**
     * The major tick unit of the zoom slider.
     */
    int ZOOM_LIMIT_MAJOR_TICK_UNIT = 1000;
    /**
     * The relative width of the buttons.
     */
    double BUTTON_WIDTH_FACTOR = 0.75;
    /**
     * The maximum number of tries the exception handler will try to execute the command.
     */
    int MAX_TRIES = 5;
}
