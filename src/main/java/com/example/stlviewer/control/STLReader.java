package com.example.stlviewer.control;

import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.MathUtil;
import com.example.stlviewer.util.RuntimeHandler;

import javax.vecmath.Vector3d;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;

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
     * The byte buffer in which the binary file is read.
     */
    private ByteBuffer byteBuffer;
    /**
     * The number of triangles in the binary file according to the file header.
     */
    private int triangleCount;

    // -- File validation data --
    /**
     * A flag to indicate if the file is corrupted.
     */
    private boolean fileIsCorrupted = false;
    /**
     * The reference to compare the attribute bytes of each triangle to. Used when reading binary files.
     */
    private byte[] attributeBytesReference;
    /**
     * A flag to indicate if the first triangle is being read. Used when reading binary files.
     */
    private boolean firstTriangle = true;
    /**
     * The index of the current triangle being read. Used when reading binary files.
     */
    private int currentTriangleIndex;

    /**
     * Reads an STL file from the given file path. The file can be in ASCII or binary format.
     * The file is either read sequentially or in parallel.
     * Precondition: The file path must be valid. The controller must be initialized.
     * Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param filePath     The file path of the STL file.
     * @param controller   The PolyhedronController instance to send the triangles to.
     * @param parallelized True if the file should be read in parallel, false otherwise.
     * @throws IOException If an error occurs while reading the file.
     */
    public void readSTLFile (String filePath, PolyhedronController controller, boolean parallelized) throws IOException
    {
        this.filePath = filePath;

        RuntimeHandler runtimeHandler = new RuntimeHandler();
        runtimeHandler.startTimer();

        if (parallelized)
        {
            readSTLFileParallelized(controller);
        } else
        {
            readSTLFileSequential(controller);
        }
        // If the file is corrupted, run additional validation checks and repairs
        if (fileIsCorrupted)
        {
            // Populate the adjacency list of the polyhedron
            controller.populateAdjacencyList();
            // Remove duplicate triangles
            logMessage(controller.removeDuplicateTriangles());
            // Fix simple holes in the mesh
            controller.fixSimpleHoles();

            // Recalculate the volume, surface area, and other properties of the polyhedron
            controller.calculatePolyhedronProperties();
        }

        runtimeHandler.stopTimer();
        logMessage(Strings.SOUT_ELAPSED_TIME, runtimeHandler.getElapsedTime());
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
            throw new FileNotFoundException(Strings.EXCEPTION_FILE_NOT_FOUND + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile)))
        {
            // Read the first line of the file
            String headerLine = reader.readLine();
            // Check if the file is empty
            if (headerLine == null)
            {
                throw new IOException(Strings.EXCEPTION_FILE_IS_EMPTY + filePath);
            }
            // Return true if the first line starts with "solid"
            return headerLine.trim().startsWith(Strings.STL_ASCII_START_TAG);
        } catch (FileNotFoundException fileNotFoundException)
        {
            throw new FileNotFoundException(Strings.EXCEPTION_FILE_NOT_FOUND + filePath);
        } catch (IOException ioException)
        {
            throw new IOException(Strings.EXCEPTION_WHILE_READING_FILE + ioException.getMessage());
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
            throw new FileNotFoundException(Strings.EXCEPTION_FILE_NOT_FOUND + filePath);
        } catch (IOException ioException)
        {
            throw new IOException(Strings.EXCEPTION_WHILE_READING_FILE + filePath);
        } catch (IllegalArgumentException illegalArgumentException)
        {
            throw new IllegalArgumentException(Strings.EXCEPTION_ERROR_WHILE_READING_TRIANGLE + illegalArgumentException.getMessage());
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
            throw new IllegalArgumentException(Strings.EXCEPTION_INVALID_NORMAL_LINE + line);
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
            throw new IllegalArgumentException(Strings.EXCEPTION_ERROR_PARSING_NORMAL + line);
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
                throw new IllegalArgumentException(Strings.EXCEPTION_INVALID_TRIANGLE_LINE + line);
            }
        } catch (IOException ioException)
        {
            throw new IllegalArgumentException(Strings.EXCEPTION_ERROR_WHILE_READING_TRIANGLE + ioException.getMessage());
        }

        // Read the three vertices of the triangle
        for (int i = 0; i < Constants.TRIANGLE_VERTEX_COUNT; i++)
        {
            line = reader.readLine();
            vertices[i] = readVertexASCII(line);
        }

        return new Triangle(vertices[Constants.TRIANGLE_VERTEX1_INDEX], vertices[Constants.TRIANGLE_VERTEX2_INDEX], vertices[Constants.TRIANGLE_VERTEX3_INDEX], normal);
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
        if (words.length != Constants.STL_ASCII_NORMAL_WORDCOUNT || !words[Constants.STL_ASCII_VERTEX_START_TAG_POS].equals(Strings.STL_ASCII_VERTEX_START_TAG))
        {
            throw new IllegalArgumentException(Strings.EXCEPTION_INVALID_VERTEX_LINE + line);
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
            throw new IllegalArgumentException(Strings.EXCEPTION_ERROR_PARSING_VERTEX + line);
        }
    }

    /**
     * Reads an STL file in binary format from the given file path. If the parallelized flag is set to true,
     * the triangles are added to the controller's queue. Otherwise, the triangles are added directly to the controller.
     * After adding the triangles, the file is checked for discrepancies in the number of triangles read.
     * <p>Precondition: The file path must be valid. The controller must be initialized.
     * <p>Postcondition: The triangles are read from the file and sent to the controller.
     *
     * @param controller - The PolyhedronController instance to send the triangles to.
     * @param parallelized - True if the file should be read in parallel, false otherwise.
     * @throws IOException - If an error occurs while reading the file.
     */
    public void readSTLBinary (PolyhedronController controller, boolean parallelized) throws IOException
    {
        // Read the file into a byte buffer
        try
        {
            byteBuffer = ByteBuffer.wrap(Files.readAllBytes(Paths.get(filePath)));
        } catch (InvalidPathException invalidPathException)
        {
            throw new IOException(Strings.EXCEPTION_INVALID_FILE_PATH + filePath);
        } catch (OutOfMemoryError outOfMemoryError)
        {
            throw new IOException(Strings.FILE_TOO_LARGE);
        }
        // Set the byte order to little endian
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // Skip the header of the file and read the number of triangleMesh
        byteBuffer.position(Constants.STL_BINARY_HEADER_BYTE_SIZE);
        triangleCount = byteBuffer.getInt();
        logMessage(Strings.TRIANGLE_COUNT_D, triangleCount);
        for (currentTriangleIndex = 0; currentTriangleIndex < triangleCount; currentTriangleIndex++)
        {
            if (parallelized)
            {
                controller.addTriangleToQueue(readTriangleBinary());
            } else
            {
                controller.addTriangle(readTriangleBinary());
            }
        }
        // If the number of triangles indicated by triangleCount were read, the file should be fully read
        // If not, the file is corrupted. The discrepancy is calculated and an exception is thrown
        if (byteBuffer.hasRemaining() && byteBuffer.remaining() % Constants.TRIANGLE_BYTE_SIZE == Constants.N_ZERO)
        {
            fileIsCorrupted = true;
            logMessage(Strings.TRIANGLE_COUNT_DISCREPANCY, byteBuffer.remaining());
        }

        // Set the reading finished flag to true
        controller.setReadingFinished(true);
    }

    /**
     * Reads a triangle from the byte buffer that contains the binary STL file. First reads the normal of the triangle,
     * then reads the three vertices of the triangle. Stores the attribute bytes of the first triangle as a reference
     * and compares the attribute bytes of the following triangles to the reference. If the attribute bytes are not equal,
     * the file may be corrupted. The fileIsCorrupted flag is set to true the program tries to recover by finding the
     * reference attribute bytes and skipping the current triangle.
     * <p>Precondition: The byte buffer must be initialized and the next bytes must be the start of a triangle.
     * <p>Postcondition: The triangle is read from the byte buffer and returned.
     *
     * @return The triangle read from the byte buffer or null if the triangle is corrupted.
     * @throws IllegalArgumentException - If an error occurs while reading the triangle.
     */
    private Triangle readTriangleBinary () throws IllegalArgumentException {
        // Read the normal of the triangle
        Vector3d normal = new Vector3d(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());

        // Read the three vertices of the triangle
        Vertex[] vertices = new Vertex[Constants.TRIANGLE_VERTEX_COUNT];
        for (int i = 0; i < Constants.TRIANGLE_VERTEX_COUNT; i++)
        {
            vertices[i] = new Vertex(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
        }

        // Skip the attribute byte count
        if (firstTriangle) {
            // The first triangle is read, and it's attribute byte count is stored as a reference
            attributeBytesReference = new byte[Constants.STL_BINARY_ATTR_BYTE_SIZE];
            byteBuffer.get(attributeBytesReference);
            firstTriangle = false;
        } else {
            // Read the attribute byte count of the current triangle
            byte[] attributeBytes = new byte[Constants.STL_BINARY_ATTR_BYTE_SIZE];
            byteBuffer.get(attributeBytes);
            // Compare the attribute byte count of the current triangle to the reference
            // If they are not equal, the file is probably corrupted
            if (!Arrays.equals(attributeBytesReference, attributeBytes)) {
                // Set the fileIsCorrupted flag to true
                fileIsCorrupted = true;
                logMessage(Strings.ATTRIBUTE_BYTE_DISCREPANCY);
                // Try to recover by finding reference attribute bytes and skipping the current triangle
                recoverToNextTriangle();
                return null;
            }
        }

        Triangle triangle;
        try
        {
            triangle = new Triangle(vertices[Constants.TRIANGLE_VERTEX1_INDEX], vertices[Constants.TRIANGLE_VERTEX2_INDEX], vertices[Constants.TRIANGLE_VERTEX3_INDEX], normal);
        } catch (IllegalArgumentException illegalArgumentException)
        {
            // Set the fileIsCorrupted flag to true
            fileIsCorrupted = true;
            logMessage(Strings.EXCEPTION_ERROR_WHILE_READING_TRIANGLE + illegalArgumentException.getMessage());
            // Discern if the exception was caused by a wrong vertex or a wrong normal
            triangle = discernTriangleDamage(vertices, normal);
        }

        return triangle;
    }

    /**
     * Discerns what caused the difference between the normal and the calculated normal of the triangle by comparing
     * the dot products of the normal and the edges of the triangle. If the normal is wrong, all dot products will be
     * non-zero. If one of the vertices is wrong, the dot product of the normal and the edge not connected to the vertex
     * will be zero. Two wrong vertices are undetectable, as this is not discernible from a wrong normal.
     * <p> If the normal is considered wrong, the normal is recalculated from the vertices. Otherwise, the method logs
     * the error and returns null.
     * <p>Precondition: The vertices and the normal must be initialized.
     * <p>Postcondition: The triangle is returned with the correct normal or null if the triangle is corrupted.
     *
     * @param vertices  The vertices of the triangle.
     * @param normal    The normal of the triangle.
     * @return          The triangle with the correct normal or null if the triangle is corrupted.
     */
    private Triangle discernTriangleDamage (Vertex[] vertices, Vector3d normal)
    {
        // A difference between the normal and the calculated normal of the triangle can be caused by either a wrong
        // normal or a wrong vertex. If the normal wrong, all dot products of the normal and the edges of the triangle
        // will be non-zero. If one of the vertices is wrong, the dot product of the normal and the edge not connected
        // to the vertex will be zero.
        // Two wrong vertices are undetectable, as this is not discernible from a wrong normal.

        // Construct the edges of the triangle
        Vector3d edge1 = new Vector3d(vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosX() - vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosX(),
                vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosY() - vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosY(),
                vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosZ() - vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosZ());
        Vector3d edge2 = new Vector3d(vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosX() - vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosX(),
                vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosY() - vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosY(),
                vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosZ() - vertices[Constants.TRIANGLE_VERTEX2_INDEX].getPosZ());
        Vector3d edge3 = new Vector3d(vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosX() - vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosX(),
                vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosY() - vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosY(),
                vertices[Constants.TRIANGLE_VERTEX1_INDEX].getPosZ() - vertices[Constants.TRIANGLE_VERTEX3_INDEX].getPosZ());

        // Calculate the dot products of the normal and the edges
        int nonZeroDotProducts = Constants.N_ZERO;
        if (MathUtil.roundToDigits(normal.dot(edge1), Constants.DOT_PRODUCT_PRECISION) != Constants.N_ZERO) nonZeroDotProducts++;
        if (MathUtil.roundToDigits(normal.dot(edge2), Constants.DOT_PRODUCT_PRECISION) != Constants.N_ZERO) nonZeroDotProducts++;
        if (MathUtil.roundToDigits(normal.dot(edge3), Constants.DOT_PRODUCT_PRECISION) != Constants.N_ZERO) nonZeroDotProducts++;

        // If all dot products are non-zero, the normal is wrong
        if (nonZeroDotProducts == Constants.TRIANGLE_VERTEX_COUNT)
        {
            logMessage(Strings.TRIANGLE_WRONG_NORMAL_OR_VERTICES);
            logMessage(Strings.CALCULATING_NORMAL_FROM_VERTICES);
            return new Triangle(vertices[Constants.TRIANGLE_VERTEX1_INDEX], vertices[Constants.TRIANGLE_VERTEX2_INDEX], vertices[Constants.TRIANGLE_VERTEX3_INDEX]);
        } else if (nonZeroDotProducts == Constants.TRIANGLE_VERTEX_COUNT - Constants.N_ONE)
        {
            logMessage(Strings.TRIANGLE_WRONG_VERTEX);
        }
        return null;
    }

    /**
     * Tries to recover from a corrupted triangle by finding the reference attribute bytes and skipping the current triangle.
     * The method jumps back to the start of the corrupted triangle and skips up to 100 bytes one by one.
     * If the attribute bytes of the next triangle are found, the recovery is successful. The method logs the recovery
     * and the number of triangles that were likely discarded. If the attribute bytes are not found, an exception is thrown.
     *
     * <p>Precondition: The byte buffer must be initialized and the current triangle must be corrupted.
     * <p>Postcondition: The byte buffer is positioned at the start of the next triangle.
     *
     * @throws IllegalArgumentException - If an error occurs while recovering the next triangle.
     */
    private void recoverToNextTriangle () {
        logMessage(Strings.RECOVERING_TO_NEXT_TRIANGLE);
        int bytesSkipped = 0;
        // Jump back to the start of the corrupted triangle
        byteBuffer.position(byteBuffer.position() - Constants.TRIANGLE_BYTE_SIZE);
        try {
            // Skip 100 bytes and check if the attribute bytes of the next triangle are found
            while (bytesSkipped <= Constants.BYTES_TO_SKIP) {
                // Read the next byte
                int nextByte = byteBuffer.get();
                bytesSkipped++;
                // If the next byte is the first byte of the attribute bytes, check the next byte
                if (nextByte == attributeBytesReference[Constants.N_ZERO]) {
                    // Read the next byte
                    int nextNextByte = byteBuffer.get();
                    bytesSkipped++;
                    // If the next byte is the second byte of the attribute bytes, the attribute bytes are found
                    if (nextNextByte == attributeBytesReference[Constants.N_ONE]) {
                        // Log the recovery and return
                        logMessage(Strings.RECOVERED_TO_NEXT_TRIANGLE, bytesSkipped);
                        logMessage(Strings.LIKELY_DISCARDED_TRIANGLES_D, Constants.N_ONE + bytesSkipped / Constants.TRIANGLE_BYTE_SIZE);
                        // Increment the triangle counter if more than 50 bytes were skipped
                        if (bytesSkipped > Constants.TRIANGLE_BYTE_SIZE) {
                            currentTriangleIndex++;
                        }
                        return;
                    }
                }
            }
            // If the attribute bytes are not found, throw an exception
            throw new IllegalArgumentException(Strings.EXCEPTION_ATTRIBUTE_BYTES_NOT_FOUND);
        } catch (Exception e) {
            throw new IllegalArgumentException(Strings.EXCEPTION_ERROR_WHILE_READING_TRIANGLE + e.getMessage());
        }
    }
}