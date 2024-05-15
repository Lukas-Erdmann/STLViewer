package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Triangles;
import com.example.stlviewer.model.Vertex;

import javax.vecmath.Vector3d;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class STLReader
{
    public Polyhedron readSTLFile (String filePath) throws IOException
    {
        if (isASCII(filePath)) {
            return readSTLASCII(filePath);
        } else {
            return readSTLBinary(filePath);
        }
    }

    public boolean isASCII (String filePath) throws IOException
    {
        File stlFile = new File(filePath);
        // Check if the file exists
        if (!stlFile.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile))) {
            // Read the first line of the file
            String headerLine = reader.readLine();
            // Check if the file is empty
            if (headerLine == null) {
                throw new IOException("File is empty: " + filePath);
            }
            // Return true if the first line starts with "solid"
            return headerLine.trim().startsWith("solid");
        } catch (FileNotFoundException fileNotFoundException) {
            throw new FileNotFoundException("File was not found: " + filePath);
        } catch (IOException ioException) {
            throw new IOException("Error while reading file: " + filePath);
        }
    }

    public Polyhedron readSTLASCII (String filePath) throws IOException
    {
        Triangles triangles = new Triangles();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check for the start of a new triangle struct
                if (line.startsWith("facet normal")) {
                    // Read the normal and the triangle
                    Vector3d normal = readNormalASCII(line);
                    Triangle triangle = readTriangleASCII(reader, normal);
                    // Add the triangle to the list of triangles
                    triangles.addTriangle(triangle);
                }
            }
        } catch (IOException ioException) {
            throw new IOException("Error while reading file: " + filePath);
        }
        return new Polyhedron(triangles);
    }

    public Vector3d readNormalASCII (String line) {
        // Split the line into words by whitespaces (\\s+ is a regex for one or more whitespaces)
        // and remove leading and trailing whitespaces
        String[] words = line.trim().split("\\s+");
        // Check if the line has the correct number of words and starts with "facet normal"
        if (words.length != 5 || !words[0].equals("facet") || !words[1].equals("normal")) {
            throw new IllegalArgumentException("Invalid normal line: " + line);
        }
        // Read the normal from the words
        try {
            double x = Double.parseDouble(words[2]);
            double y = Double.parseDouble(words[3]);
            double z = Double.parseDouble(words[4]);
            return new Vector3d(x, y, z);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("Error parsing normal: " + line);
        }
    }

    public Triangle readTriangleASCII (BufferedReader reader, Vector3d normal) throws IOException
    {
        Vertex[] vertices = new Vertex[3];
        String line;

        // Skip the "outer loop" line
        try {
            line = reader.readLine();
            if (line == null || !line.trim().equals("outer loop")) {
                throw new IllegalArgumentException("Invalid triangle line: " + line);
            }
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Error while reading triangle: " + ioException.getMessage());
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
        String[] words = line.trim().split("\\s+");
        // Check if the line has the correct number of words and starts with "vertex"
        if (words.length != 4 || !words[0].equals("vertex")) {
            throw new IllegalArgumentException("Invalid vertex line: " + line);
        }

        // Read the vertex from the words
        try {
            double x = Double.parseDouble(words[1]);
            double y = Double.parseDouble(words[2]);
            double z = Double.parseDouble(words[3]);
            return new Vertex(x, y, z);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("Error parsing vertex: " + line);
        }
    }

    public Polyhedron readSTLBinary (String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            // Skip the header of the file and read the number of triangles
            fileInputStream.skip(80);
            byte[] triangleCountBytes = new byte[4];
            fileInputStream.read(triangleCountBytes, 0, 4);
            // Convert the byte array to an integer using little endian byte order
            // TODO: Verify this method
            int triangleCount = ByteBuffer.wrap(triangleCountBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

            Triangles triangles = new Triangles();
            for (int i = 0; i < triangleCount; i++) {
                Triangle triangle = readTriangleBinary(fileInputStream);
                triangles.addTriangle(triangle);
            }
            return new Polyhedron(triangles);
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Error while reading file: " + filePath);
        }
    }

    public Triangle readTriangleBinary (FileInputStream fileInputStream) {
        try {
            // Read the normal of the triangle
            byte[] normalBytes = new byte[12];
            fileInputStream.read(normalBytes, 0, 12);
            Vector3d normal = readNormalBinary(normalBytes);

            // Read the three vertices of the triangle
            Vertex[] vertices = new Vertex[3];
            for (int i = 0; i < 3; i++) {
                byte[] vertexBytes = new byte[12];
                fileInputStream.read(vertexBytes, 0, 12);
                vertices[i] = readVertexBinary(vertexBytes);
            }

            // Skip the attribute byte count
            fileInputStream.skip(2);

            return new Triangle(vertices[0], vertices[1], vertices[2], normal);
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Error while reading triangle: " + ioException.getMessage());
        }

    }

    private Vertex readVertexBinary (byte[] bytes)
    {
        // Read the vertex from the byte array using little endian byte order
        double x = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        double y = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        double z = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vertex(x, y, z);
    }

    private Vector3d readNormalBinary (byte[] bytes)
    {
        // Read the normal from the byte array using little endian byte order
        double x = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        double y = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        double z = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Vector3d(x, y, z);
    }
}
