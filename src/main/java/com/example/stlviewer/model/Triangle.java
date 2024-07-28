package com.example.stlviewer.model;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.MathUtil;

import javax.vecmath.Vector3d;
import java.io.Serializable;

import static com.example.stlviewer.util.RuntimeHandler.logMessage;

/**
 * The Triangle class represents a triangle in a 3D space. It is a polygon that lies in a plane and has 3 vertices and
 * 3 edges. Perpendicular to the plane of the triangle is the normal vector. The class extends the Face class.
 * It also implements the Comparable interface to compare the area of two triangles.
 */
public class Triangle extends Face implements Comparable<Triangle>, Serializable
{
    /**
     * The centroid of the triangle. The centroid is the point where the medians of the triangle intersect.
     */
    private Vertex centroid;
    /**
     * The area of the triangle.
     */
    private double area;
    /**
     * The ID of the triangle unique to the list of triangles in the polyhedron.
     */
    private int id;

    /**
     * Creates a new triangle with the given vertices and normal vector. The normal vector is calculated as the cross
     * product of two edges in counter-clockwise order. The normal vector is then normalized to unit length.
     * The area of the triangle is calculated using Heron's formula.
     *
     * @param v1     - The first vertex of the triangle.
     * @param v2     - The second vertex of the triangle.
     * @param v3     - The third vertex of the triangle.
     * @param normal - The normal vector of the triangle.
     */
    public Triangle (Vertex v1, Vertex v2, Vertex v3, Vector3d normal)
    {
        super(Constants.TRIANGLE_VERTEX_COUNT);
        // Set the vertices
        vertices.add(0, v1);
        vertices.add(1, v2);
        vertices.add(2, v3);

        // Set the normal
        edges.add(0, new Edge(v2, v1));
        edges.add(1, new Edge(v3, v2));
        edges.add(2, new Edge(v1, v3));

        // The normal is the cross product of two edges in counter-clockwise order
        this.getNormal().cross(edges.get(0), edges.get(1));
        // Normalize the normal, bringing it to unit length (length = 1)
        this.getNormal().normalize();
        // Compare the normal of the triangle with the normal passed as an argument
        // The comparison normal via cross product of the calculated normal and the passed normal
        // If the length of the comparison normal is greater than the rounding tolerance, the normals are not equal
        Vector3d comparisonNormal = new Vector3d();
        comparisonNormal.cross(this.getNormal(), normal);
        if (comparisonNormal.length() > Constants.NORMAL_DIFFERENCE_ROUNDING_TOLERANCE)
        {
            //throw new IllegalArgumentException("Calculated triangle and passed normal difference exceeds rounding error tolerance.");
        }
        // Calculate the area of the triangle
        this.calculateArea();
    }

    public Triangle (Vertex v1, Vertex v2, Vertex v3)
    {
        super(3);
        // Set the vertices
        vertices.add(0, v1);
        vertices.add(1, v2);
        vertices.add(2, v3);

        // Set the normal
        edges.add(0, new Edge(v2, v1));
        edges.add(1, new Edge(v3, v2));
        edges.add(2, new Edge(v1, v3));

        // The normal is the cross product of two edges in counter-clockwise order
        this.getNormal().cross(edges.get(0), edges.get(1));
        // Normalize the normal, bringing it to unit length (length = 1)
        this.getNormal().normalize();
        // Calculate the area of the triangle
        this.calculateArea();
    }

    /**
     * Calculates the area of the triangle using Heron's formula.
     * Precondition: The triangle must exist.
     * Postcondition: The area of the triangle is calculated.
     */
    private void calculateArea ()
    {
        // Calculate the area of the triangle using Heron's formula
        double a = edges.get(0).length();
        double b = edges.get(1).length();
        double c = edges.get(2).length();

        // Calculate the semi-perimeter
        double s = (a + b + c) / 2;

        // Calculate the area
        this.area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    /**
     * Calculates the volume of the tetrahedron formed by the triangle and a reference vertex.
     * The volume of a tetrahedron is given by the formula:
     * V = 1/6 * |(a - d) . ((b - d) x (c - d))|
     * where a, b, c are the vertices of the triangle and d is the reference vertex.
     * Precondition: The triangle and reference vertex must exist.
     * Postcondition: The volume of the tetrahedron is calculated.
     *
     * @param refVertex - The reference vertex.
     * @return The volume of the tetrahedron.
     */
    public double calculateVolumeWithReferenceVertex (Vertex refVertex)
    {
        // where a, b, c are the vertices of the triangle and d is the reference vertex
        Vector3d a = new Vector3d(vertices.get(0).getPosX() - refVertex.getPosX(),
                vertices.get(0).getPosY() - refVertex.getPosY(),
                vertices.get(0).getPosZ() - refVertex.getPosZ());
        Vector3d b = new Vector3d(vertices.get(1).getPosX() - refVertex.getPosX(),
                vertices.get(1).getPosY() - refVertex.getPosY(),
                vertices.get(1).getPosZ() - refVertex.getPosZ());
        Vector3d c = new Vector3d(vertices.get(2).getPosX() - refVertex.getPosX(),
                vertices.get(2).getPosY() - refVertex.getPosY(),
                vertices.get(2).getPosZ() - refVertex.getPosZ());
        // Calculate the cross product of b and c
        Vector3d crossProduct = new Vector3d();
        crossProduct.cross(b, c);
        // Calculate the scalar triple product of a, b, and c
        double scalarTripleProduct = a.dot(crossProduct);
        // Divide the absolute value of the scalar triple product by 6 to get the volume of the tetrahedron
        return Math.abs(scalarTripleProduct) / 6;
    }

    /**
     * Returns the centroid of the triangle. The centroid is the point where the medians of the triangle intersect.
     * The centroid is calculated as the average of the x, y, and z coordinates of the vertices.
     * Precondition: The triangle must exist.
     * Postcondition: The centroid of the triangle is returned.
     *
     * @return The centroid of the triangle.
     */
    public Vertex getCentroid ()
    {
        if (centroid == null)
        {
            double x = (vertices.get(0).getPosX() + vertices.get(1).getPosX() + vertices.get(2).getPosX()) / 3;
            double y = (vertices.get(0).getPosY() + vertices.get(1).getPosY() + vertices.get(2).getPosY()) / 3;
            double z = (vertices.get(0).getPosZ() + vertices.get(1).getPosZ() + vertices.get(2).getPosZ()) / 3;
            centroid = new Vertex(x, y, z);
        }
        return centroid;
    }

    /**
     * Checks if the normal of the triangle points away from a reference vertex.
     * The normal of the triangle points away from the reference vertex if the dot product of the normal and the vector
     * from the reference vertex to the centroid of the triangle is positive.
     * Precondition: The triangle and reference vertex must exist.
     * Postcondition: True is returned if the normal points away from the reference vertex, false otherwise.
     *
     * @param refVertex - The reference vertex.
     * @return True if the normal points away from the reference vertex, false otherwise.
     */
    public boolean pointsAwayFromReferenceVertex (Vertex refVertex)
    {
        // Calculate the dot product of the normal of the triangle and the vector from the reference vertex to the centroid
        Vertex centroid = getCentroid();
        Vector3d referenceToCentroid = new Vector3d(refVertex.getPosX() - centroid.getPosX(),
                refVertex.getPosY() - centroid.getPosY(),
                refVertex.getPosZ() - centroid.getPosZ());
        double dotProduct = getNormal().dot(referenceToCentroid);
        // If the dot product is positive, the normal points away from the reference vertex
        return dotProduct > 0;
    }

    /**
     * Returns the area of the triangle.
     *
     * @return The area of the triangle.
     */
    public double getArea ()
    {
        return area;
    }

    /**
     * Returns the ID of the triangle.
     *
     * @return The ID of the triangle.
     */
    public int getId ()
    {
        return id;
    }

    /**
     * Sets the ID of the triangle.
     *
     * @param id - The ID of the triangle.
     */
    public void setId (int id)
    {
        this.id = id;
    }

    /**
     * Returns the string representation of the triangle.
     *
     * @return The string representation of the triangle.
     */
    @Override
    public String toString ()
    {
        // Return the vertices of the triangle with a string builder
        StringBuilder builder = new StringBuilder();
        builder.append(Strings.TRIANGLE_TOSTRING);
        builder.append(Strings.TRIANGLE_TOSTRING_2);
        builder.append(MathUtil.roundToDigits(area, 2)).append(Strings.COMMA_SPACE);
        for (Edge edge : edges)
        {
            builder.append(edge.toString());
            builder.append(Strings.COMMA_SPACE);
        }
        builder.append(Strings.TRIANGLE_TOSTRING_3);
        builder.append(getNormalString());
        builder.append(Strings.CURLY_BRACKET_RIGHT);
        return builder.toString();
    }

    @Override
    public int compareTo (Triangle other)
    {
        return Double.compare(this.area, other.area);
    }

    @Override
    public boolean equals (Object refTriangle)
    {
        if (refTriangle instanceof Triangle triangle)
        {
            return vertices.containsAll(triangle.vertices);
        }
        return false;
    }

    public boolean isAdjacentTo (Triangle triangle)
    {
        int count = 0;
        for (Vertex vertex : vertices)
        {
            if (triangle.vertices.contains(vertex))
            {
                count++;
            }
        }
        return count == 2;
    }

    public boolean isSameAs (Triangle triangle)
    {
        return vertices.containsAll(triangle.vertices);
    }
}
