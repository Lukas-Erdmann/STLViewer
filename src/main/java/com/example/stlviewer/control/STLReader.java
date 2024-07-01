package com.example.stlviewer.control;

import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;

import javax.vecmath.Vector3d;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class STLReader
{

    public static final String STL_ASCII_START_TAG = "solid";
    public static final String STL_ASCII_FACET_START_TAG = "facet normal";
    public static final String STL_ASCII_NORMAL_TAG = "normal";
    public static final String STL_ASCII_TRIANGLE_START_TAG = "outer loop";
    public static final String STL_ASCII_VERTEX_START_TAG = "vertex";
    public static final int STL_ASCII_NORMAL_WORD_POS_X = 2;
    public static final int STL_ASCII_NORMAL_WORD_POS_Y = 3;
    public static final int STL_ASCII_NORMAL_WORD_POS_Z = 4;
    public static final int STL_ASCII_FACET_WORDCOUNT = 5;
    public static final int STL_ASCII_FACET_START_TAG_POS = 0;
    public static final int STL_ASCII_NORMAL_TAG_POS = 1;
    public static final int STL_ASCII_NORMAL_WORDCOUNT = 4;
    public static final int STL_ASCII_VERTEX_START_TAG_POS = 0;
    public static final int STL_ASCII_VERTEX_WORD_POS_X = 1;
    public static final int STL_ASCII_VERTEX_WORD_POS_Y = 2;
    public static final int STL_ASCII_VERTEX_WORD_POS_Z = 3;
    public static final int STL_BINARY_HEADER_BYTE_SIZE = 80;
    public static final int STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE = 4;
    public static final int STL_BINARY_NORMAL_BYTE_SIZE = 12;
    public static final int STL_BINARY_TRIANGLES_BYTE_SIZE = 12;
    public static final int STL_BINARY_ATTR_BYTE_SIZE = 2;
    public static final int STL_BINARY_VERTEX_BYTE_SIZE = 4;
    public static final int STL_BINARY_OFFSET_Y = 4;
    public static final int STL_BINARY_OFFSET_Z = 8;
    public static final int STL_BINARY_OFFSET_X = Constants.NUMBER_ZERO;
    public static final int FLOAT_LENGTH_IN_BYTES = 8;

    public void readSTLFile (String filePath, PolyhedronController controller) throws IOException
    {
        Thread readerThread = new Thread(controller);
        readerThread.start();

        if (isASCII(filePath)) {
            readSTLASCII(filePath, controller);
        } else {
            readSTLBinary(filePath, controller);
        }

        try {
            readerThread.join();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread interrupted", interruptedException);
        }
    }

    public boolean isASCII (String filePath) throws IOException
    {
        File stlFile = new File(filePath);
        // Check if the file exists
        if (!stlFile.exists()) {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile))) {
            // Read the first line of the file
            String headerLine = reader.readLine();
            // Check if the file is empty
            if (headerLine == null) {
                throw new IOException(Strings.FILE_IS_EMPTY + filePath);
            }
            // Return true if the first line starts with "solid"
            return headerLine.trim().startsWith(STL_ASCII_START_TAG);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        } catch (IOException ioException) {
            throw new IOException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    public void readSTLASCII (String filePath, PolyhedronController controller) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check for the start of a new triangle struct
                if (line.startsWith(STL_ASCII_FACET_START_TAG)) {
                    // Read the normal and the triangle
                    Vector3d normal = readNormalASCII(line);
                    Triangle triangle = readTriangleASCII(reader, normal);
                    // Send the triangle to the controller
                    controller.addTriangle(triangle);
                }
            }
            // Set the reading finished flag to true
            controller.setReadingFinished(true);
        } catch (IOException ioException) {
            throw new IOException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    public Vector3d readNormalASCII (String line) {
        // Split the line into words by whitespaces (\\s+ is a regex for one or more whitespaces)
        // and remove leading and trailing whitespaces
        String[] words = line.trim().split(Strings.ANY_NUMBER_OF_WHITESPACES);
        // Check if the line has the correct number of words and starts with "facet normal"
        if (words.length != STL_ASCII_FACET_WORDCOUNT || !words[STL_ASCII_FACET_START_TAG_POS].equals(STL_ASCII_FACET_START_TAG) || !words[STL_ASCII_NORMAL_TAG_POS].equals(STL_ASCII_NORMAL_TAG)) {
            throw new IllegalArgumentException(Strings.INVALID_NORMAL_LINE + line);
        }
        // Read the normal from the words
        try {
            double x = Double.parseDouble(words[STL_ASCII_NORMAL_WORD_POS_X]);
            double y = Double.parseDouble(words[STL_ASCII_NORMAL_WORD_POS_Y]);
            double z = Double.parseDouble(words[STL_ASCII_NORMAL_WORD_POS_Z]);
            return new Vector3d(x, y, z);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(Strings.ERROR_PARSING_NORMAL + line);
        }
    }

    public Triangle readTriangleASCII (BufferedReader reader, Vector3d normal) throws IOException
    {
        Vertex[] vertices = new Vertex[Constants.TRIANGLE_VERTEX_COUNT];
        String line;

        // Skip the "outer loop" line
        try {
            line = reader.readLine();
            if (line == null || !line.trim().equals(STL_ASCII_TRIANGLE_START_TAG)) {
                throw new IllegalArgumentException(Strings.INVALID_TRIANGLE_LINE + line);
            }
        } catch (IOException ioException) {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_TRIANGLE + ioException.getMessage());
        }

        // Read the three vertices of the triangle
        for (int i = 0; i < 3; i++) {
            line = reader.readLine();
            vertices[i] = readVertexASCII(line);
        }

        return new Triangle(vertices[0], vertices[1], vertices[2], normal);
    }

    public Vertex readVertexASCII (String line) {
        // Split the line into words by whitespaces (\\s+ is a regex for one or more whitespaces)
        // and remove leading and trailing whitespaces
        String[] words = line.trim().split(Strings.ANY_NUMBER_OF_WHITESPACES);
        // Check if the line has the correct number of words and starts with "vertex"
        if (words.length != STL_ASCII_NORMAL_WORDCOUNT || !words[STL_ASCII_VERTEX_START_TAG_POS].equals(STL_ASCII_VERTEX_START_TAG)) {
            throw new IllegalArgumentException(Strings.INVALID_VERTEX_LINE + line);
        }

        // Read the vertex from the words
        try {
            double x = Double.parseDouble(words[STL_ASCII_VERTEX_WORD_POS_X]);
            double y = Double.parseDouble(words[STL_ASCII_VERTEX_WORD_POS_Y]);
            double z = Double.parseDouble(words[STL_ASCII_VERTEX_WORD_POS_Z]);
            return new Vertex(x, y, z);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(Strings.ERROR_PARSING_VERTEX + line);
        }
    }

    public void readSTLBinary (String filePath, PolyhedronController controller) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            // Skip the header of the file and read the number of triangleMesh
            fileInputStream.skip(STL_BINARY_HEADER_BYTE_SIZE);
            byte[] triangleCountBytes = new byte[STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE];
            fileInputStream.read(triangleCountBytes, Constants.NUMBER_ZERO, STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE);
            // Convert the byte array to an integer using little endian byte order
            int triangleCount = ByteBuffer.wrap(triangleCountBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

            for (int i = 0; i < triangleCount; i++) {
                controller.addTriangle(readTriangleBinary(fileInputStream));
            }
            // Set the reading finished flag to true
            controller.setReadingFinished(true);
        } catch (IOException ioException) {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    public Triangle readTriangleBinary (FileInputStream fileInputStream) {
        try {
            // Read the normal of the triangle
            byte[] normalBytes = new byte[STL_BINARY_NORMAL_BYTE_SIZE];
            fileInputStream.read(normalBytes, Constants.NUMBER_ZERO, STL_BINARY_NORMAL_BYTE_SIZE);
            Vector3d normal = readNormalBinary(normalBytes);

            // Read the three vertices of the triangle
            Vertex[] vertices = new Vertex[Constants.TRIANGLE_VERTEX_COUNT];
            for (int i = 0; i < 3; i++) {
                byte[] vertexBytes = new byte[STL_BINARY_TRIANGLES_BYTE_SIZE];
                fileInputStream.read(vertexBytes, Constants.NUMBER_ZERO, STL_BINARY_TRIANGLES_BYTE_SIZE);
                vertices[i] = readVertexBinary(vertexBytes);
            }

            // Skip the attribute byte count
            fileInputStream.skip(STL_BINARY_ATTR_BYTE_SIZE);

            return new Triangle(vertices[Constants.TRIANGLE_VERTEX1_INDEX], vertices[Constants.TRIANGLE_VERTEX2_INDEX], vertices[Constants.TRIANGLE_VERTEX3_INDEX], normal);
        } catch (IOException ioException) {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_TRIANGLE + ioException.getMessage());
        }

    }

    private Vertex readVertexBinary (byte[] bytes)
    {
        // Read the vertex from the byte array using little endian byte order
        float x = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_X, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float y = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_Y, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float z = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_Z, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vertex(x, y, z);
    }

    private Vector3d readNormalBinary (byte[] bytes)
    {
        // Read the normal from the byte array using little endian byte order
        float x = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_X, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float y = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_Y, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float z = ByteBuffer.wrap(bytes, STL_BINARY_OFFSET_Z, STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vector3d(x, y, z);
    }
}
