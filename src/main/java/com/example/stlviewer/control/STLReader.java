package com.example.stlviewer.control;

import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.RuntimeHandler;

import javax.vecmath.Vector3d;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.example.stlviewer.util.RuntimeHandler.logMessage;

/**
 * The STLReader class is responsible for reading STL files in ASCII and binary format.
 * It can read the files sequentially or in parallel.
 */
public class STLReader
{
    /**
     * The file path of the STL file.
     */
    private String filePath;

    /**
     * Reads an STL file from the given file path. The file can be in ASCII or binary format.
     * The file is either read sequentially or in parallel.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param filePath     - The file path of the STL file.
     * @param controller   - The PolyhedronController instance to send the triangles to.
     * @param parallelized - True if the file should be read in parallel, false otherwise.
     * @throws IOException - If an error occurs while reading the file.
     */
    public void readSTLFile (String filePath, PolyhedronController controller, boolean parallelized) throws IOException
    {
        this.filePath = filePath;
        if (parallelized)
        {
            readSTLFileParallelized(controller);
        } else
        {
            readSTLFileSequential(controller);
        }
    }

    /**
     * Reads an STL file sequentially from the given file path. The file can be in ASCII or binary format.
     * The time taken to read the file is measured and printed to the console.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param controller - The PolyhedronController instance to send the triangles to.
     * @throws IOException - If an error occurs while reading the file.
     */
    public void readSTLFileSequential (PolyhedronController controller) throws IOException
    {
        RuntimeHandler runtimeHandler = new RuntimeHandler();
        runtimeHandler.startTimer();
        if (isASCII())
        {
            logMessage(Strings.SOUT_READING_ASCII_FILE);
            readSTLASCII(controller, false);
        } else
        {
            logMessage(Strings.SOUT_READING_BINARY_FILE);
            readSTLBinary(controller, false);
        }
        // Calculate volume, surface area, and other properties of the polyhedron
        controller.calculatePolyhedronProperties();

        runtimeHandler.stopTimer();
        logMessage(Strings.SOUT_ELAPSED_TIME, runtimeHandler.getElapsedTime());
    }

    /**
     * Reads an STL file in parallel from the given file path. The file can be in ASCII or binary format.
     * The time taken to read the file is measured and printed to the console.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param controller - The PolyhedronController instance to send the triangles to.
     * @throws IOException - If an error occurs while reading the file.
     */
    public void readSTLFileParallelized (PolyhedronController controller) throws IOException
    {
        RuntimeHandler runtimeHandler = new RuntimeHandler();
        runtimeHandler.startTimer();
        Thread readerThread = new Thread(controller);
        readerThread.start();

        if (isASCII())
        {
            logMessage(Strings.SOUT_READING_ASCII_FILE);
            readSTLASCII(controller, true);
        } else
        {
            logMessage(Strings.SOUT_READING_BINARY_FILE);
            readSTLBinary(controller, true);
        }

        try
        {
            readerThread.join();
        } catch (InterruptedException interruptedException)
        {
            Thread.currentThread().interrupt();
            throw new IOException(Strings.THREAD_WAS_INTERRUPTED, interruptedException);
        }
        runtimeHandler.stopTimer();
        logMessage(Strings.SOUT_ELAPSED_TIME, runtimeHandler.getElapsedTime());
    }

    /**
     * Checks if the file at the given file path is in ASCII format.
     * Precondition: The file path must be valid.
     * Postcondition: Returns true if the file is in ASCII format, false otherwise.
     *
     * @return True if the file is in ASCII format, false otherwise.
     * @throws IOException - If an error occurs while reading the file.
     */
    public boolean isASCII () throws IOException
    {
        File stlFile = new File(filePath);
        // Check if the file exists
        if (!stlFile.exists())
        {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile)))
        {
            // Read the first line of the file
            String headerLine = reader.readLine();
            // Check if the file is empty
            if (headerLine == null)
            {
                throw new IOException(Strings.FILE_IS_EMPTY + filePath);
            }
            // Return true if the first line starts with "solid"
            return headerLine.trim().startsWith(Strings.STL_ASCII_START_TAG);
        } catch (FileNotFoundException fileNotFoundException)
        {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        } catch (IOException ioException)
        {
            throw new IOException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    /**
     * Reads an STL file in ASCII format from the given file path. If the parallelized flag is set to true,
     * the triangles are added to the controller's queue. Otherwise, the triangles are added directly to the controller.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param controller - The PolyhedronController instance to send the triangles to.
     * @throws IOException - If an error occurs while reading the file.
     */
    public void readSTLASCII (PolyhedronController controller, boolean parallelized) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                // Check for the start of a new triangle struct
                if (line.trim().startsWith(Strings.STL_ASCII_FACET_START_TAG))
                {
                    // Read the normal and the triangle
                    Vector3d normal = readNormalASCII(line);
                    Triangle triangle = readTriangleASCII(reader, normal);
                    // Send the triangle to the controller
                    if (parallelized)
                    {
                        controller.addTriangleToQueue(triangle);
                    } else
                    {
                        controller.addTriangle(triangle);
                    }
                }
            }
            // Set the reading finished flag to true
            controller.setReadingFinished(true);
        } catch (FileNotFoundException fileNotFoundException)
        {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        } catch (IOException ioException)
        {
            throw new IOException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    /**
     * Reads the normal of a triangle from a line in an ASCII STL file.
     * Precondition: The line must be a valid normal line in an ASCII STL file.
     * Postcondition: The normal is read from the line and returned as a Vector3d.
     *
     * @param line - The line containing the normal of the triangle.
     * @return The normal of the triangle as a Vector3d.
     */
    public Vector3d readNormalASCII (String line)
    {
        // Split the line into words by whitespaces (\\s+ is a regex for one or more whitespaces)
        // and remove leading and trailing whitespaces
        String[] words = line.trim().split(Strings.ANY_NUMBER_OF_WHITESPACES);
        // Check if the line has the correct number of words and starts with "facet normal"
        if (words.length != Constants.STL_ASCII_FACET_WORDCOUNT || !words[Constants.STL_ASCII_FACET_START_TAG_POS].equals(Strings.STL_ASCII_FACET_START_TAG) || !words[Constants.STL_ASCII_NORMAL_TAG_POS].equals(Strings.STL_ASCII_NORMAL_TAG))
        {
            throw new IllegalArgumentException(Strings.INVALID_NORMAL_LINE + line);
        }
        // Read the normal from the words
        try
        {
            double x = Double.parseDouble(words[Constants.STL_ASCII_NORMAL_WORD_POS_X]);
            double y = Double.parseDouble(words[Constants.STL_ASCII_NORMAL_WORD_POS_Y]);
            double z = Double.parseDouble(words[Constants.STL_ASCII_NORMAL_WORD_POS_Z]);
            return new Vector3d(x, y, z);
        } catch (NumberFormatException numberFormatException)
        {
            throw new IllegalArgumentException(Strings.ERROR_PARSING_NORMAL + line);
        }
    }

    /**
     * Reads a triangle from a buffered reader in an ASCII STL file.
     * Precondition: The reader must be initialized and the next line must be the start of a triangle.
     * Postcondition: The triangle is read from the reader and returned.
     *
     * @param reader - The BufferedReader to read the triangle from.
     * @param normal - The normal of the triangle.
     * @return The triangle read from the reader.
     * @throws IOException - If an error occurs while reading the triangle.
     */
    public Triangle readTriangleASCII (BufferedReader reader, Vector3d normal) throws IOException
    {
        Vertex[] vertices = new Vertex[Constants.TRIANGLE_VERTEX_COUNT];
        String line;

        // Skip the "outer loop" line
        try
        {
            line = reader.readLine();
            if (line == null || !line.trim().equals(Strings.STL_ASCII_TRIANGLE_START_TAG))
            {
                throw new IllegalArgumentException(Strings.INVALID_TRIANGLE_LINE + line);
            }
        } catch (IOException ioException)
        {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_TRIANGLE + ioException.getMessage());
        }

        // Read the three vertices of the triangle
        for (int i = 0; i < 3; i++)
        {
            line = reader.readLine();
            vertices[i] = readVertexASCII(line);
        }

        return new Triangle(vertices[0], vertices[1], vertices[2], normal);
    }

    /**
     * Reads a vertex from a line in an ASCII STL file.
     * Precondition: The line must be a valid vertex line in an ASCII STL file.
     * Postcondition: The vertex is read from the line and returned.
     *
     * @param line - The line containing the vertex.
     * @return The vertex read from the line.
     */
    public Vertex readVertexASCII (String line)
    {
        // Split the line into words by whitespaces (\\s+ is a regex for one or more whitespaces)
        // and remove leading and trailing whitespaces
        String[] words = line.trim().split(Strings.ANY_NUMBER_OF_WHITESPACES);
        // Check if the line has the correct number of words and starts with "vertex"
        if (words.length != Constants.STL_ASCII_NORMAL_WORDCOUNT || !words[Constants.STL_ASCII_VERTEX_START_TAG_POS].equals(Constants.STL_ASCII_VERTEX_START_TAG))
        {
            throw new IllegalArgumentException(Strings.INVALID_VERTEX_LINE + line);
        }

        // Read the vertex from the words
        try
        {
            double x = Double.parseDouble(words[Constants.STL_ASCII_VERTEX_WORD_POS_X]);
            double y = Double.parseDouble(words[Constants.STL_ASCII_VERTEX_WORD_POS_Y]);
            double z = Double.parseDouble(words[Constants.STL_ASCII_VERTEX_WORD_POS_Z]);
            return new Vertex(x, y, z);
        } catch (NumberFormatException numberFormatException)
        {
            throw new IllegalArgumentException(Strings.ERROR_PARSING_VERTEX + line);
        }
    }

    /**
     * Reads an STL file in binary format from the given file path. If the parallelized flag is set to true,
     * the triangles are added to the controller's queue. Otherwise, the triangles are added directly to the controller.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param controller   - The PolyhedronController instance to send the triangles to.
     * @param parallelized - True if the file should be read in parallel, false otherwise.
     */
    public void readSTLBinary (PolyhedronController controller, boolean parallelized) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath))
        {
            // Skip the header of the file and read the number of triangleMesh
            fileInputStream.skip(Constants.STL_BINARY_HEADER_BYTE_SIZE);
            byte[] triangleCountBytes = new byte[Constants.STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE];
            fileInputStream.read(triangleCountBytes, Constants.NUMBER_ZERO, Constants.STL_BINARY_TRIANGLE_COUNT_BYTE_SIZE);
            // Convert the byte array to an integer using little endian byte order
            int triangleCount = ByteBuffer.wrap(triangleCountBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

            for (int i = 0; i < triangleCount; i++)
            {
                if (parallelized)
                {
                    controller.addTriangleToQueue(readTriangleBinary(fileInputStream));
                } else
                {
                    controller.addTriangle(readTriangleBinary(fileInputStream));
                }
            }
            // Set the reading finished flag to true
            controller.setReadingFinished(true);
        } catch (FileNotFoundException fileNotFoundException)
        {
            throw new FileNotFoundException(Strings.FILE_NOT_FOUND + filePath);
        } catch (IOException ioException)
        {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_FILE + filePath);
        }
    }

    /**
     * Reads a triangle from a file input stream in binary format.
     * Precondition: The file input stream must be initialized and the next bytes must be the start of a triangle.
     * Postcondition: The triangle is read from the file input stream and returned.
     *
     * @param fileInputStream - The FileInputStream to read the triangle from.
     * @return The triangle read from the file input stream.
     */
    public Triangle readTriangleBinary (FileInputStream fileInputStream)
    {
        try
        {
            // Read the normal of the triangle
            byte[] normalBytes = new byte[Constants.STL_BINARY_NORMAL_BYTE_SIZE];
            fileInputStream.read(normalBytes, Constants.NUMBER_ZERO, Constants.STL_BINARY_NORMAL_BYTE_SIZE);
            Vector3d normal = readNormalBinary(normalBytes);

            // Read the three vertices of the triangle
            Vertex[] vertices = new Vertex[Constants.TRIANGLE_VERTEX_COUNT];
            for (int i = 0; i < 3; i++)
            {
                byte[] vertexBytes = new byte[Constants.STL_BINARY_TRIANGLES_BYTE_SIZE];
                fileInputStream.read(vertexBytes, Constants.NUMBER_ZERO, Constants.STL_BINARY_TRIANGLES_BYTE_SIZE);
                vertices[i] = readVertexBinary(vertexBytes);
            }

            // Skip the attribute byte count
            fileInputStream.skip(Constants.STL_BINARY_ATTR_BYTE_SIZE);

            return new Triangle(vertices[Constants.TRIANGLE_VERTEX1_INDEX], vertices[Constants.TRIANGLE_VERTEX2_INDEX], vertices[Constants.TRIANGLE_VERTEX3_INDEX], normal);
        } catch (IOException ioException)
        {
            throw new IllegalArgumentException(Strings.ERROR_WHILE_READING_TRIANGLE + ioException.getMessage());
        }

    }

    /**
     * Reads a vertex from a byte array in binary format. Each vertex is represented by 12 bytes, 4 bytes for each coordinate.
     * As the coordinates are stored in little endian byte order, they are read accordingly.
     * Precondition: The byte array must be initialized and contain the vertex data.
     * Postcondition: The vertex is read from the byte array and returned.
     *
     * @param bytes - The byte array containing the vertex data.
     * @return The vertex read from the byte array.
     */
    private Vertex readVertexBinary (byte[] bytes)
    {
        // Read the vertex from the byte array using little endian byte order
        float x = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_X, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float y = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_Y, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float z = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_Z, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vertex(x, y, z);
    }

    /**
     * Reads a normal from a byte array in binary format. Each normal is represented by 12 bytes, 4 bytes for each coordinate.
     * As the coordinates are stored in little endian byte order, they are read accordingly.
     * Precondition: The byte array must be initialized and contain the normal data.
     * Postcondition: The normal is read from the byte array and returned as a Vector3d.
     *
     * @param bytes - The byte array containing the normal data.
     * @return The normal read from the byte array as a Vector3d.
     */
    private Vector3d readNormalBinary (byte[] bytes)
    {
        // Read the normal from the byte array using little endian byte order
        float x = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_X, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float y = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_Y, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float z = ByteBuffer.wrap(bytes, Constants.STL_BINARY_OFFSET_Z, Constants.STL_BINARY_VERTEX_BYTE_SIZE).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vector3d(x, y, z);
    }
}
