package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Polyhedron class represents a polyhedron in a 3D space. It is a solid that is bounded by a finite number of
 * polygons. In this case, the polygons are triangles. The class contains a list of triangles that make up the polyhedron.
 * The class also contains the volume, surface area, bounding box, and center of the polyhedron.
 */
public class Polyhedron implements Serializable
{
    /**
     * The list of triangles that make up the polyhedron.
     */
    private ArrayList<Triangle> triangles;
    /**
     * The volume of the polyhedron.
     */
    private double volume = 0;
    /**
     * The surface area of the polyhedron.
     */
    private double surfaceArea = 0;
    /**
     * The bounding box of the polyhedron. The bounding box is an array of 6 doubles that represent the minimum and
     * maximum x, y, and z coordinates of the polyhedron.
     */
    private double[] boundingBox;
    /**
     * The center of the polyhedron.
     */
    private Vertex center;

    /**
     * Creates a new polyhedron with the given list of triangles.
     *
     * @param triangleArrayList - The list of triangles that make up the polyhedron.
     */
    public Polyhedron (ArrayList<Triangle> triangleArrayList)
    {
        this.triangles = triangleArrayList;
    }

    /**
     * Creates a new polyhedron with an empty list of triangles.
     */
    public Polyhedron ()
    {
        this.triangles = new ArrayList<Triangle>();
    }

    /**
     * Gets the list of triangles that make up the polyhedron.
     *
     * @return The list of triangles that make up the polyhedron.
     */
    public ArrayList<Triangle> getTriangles ()
    {
        return triangles;
    }

    /**
     * Gets the volume of the polyhedron.
     *
     * @return The volume of the polyhedron.
     */
    public double getVolume ()
    {
        return volume;
    }

    /**
     * Sets the volume of the polyhedron.
     *
     * @param volume The volume of the polyhedron.
     */
    public void setVolume (double volume)
    {
        this.volume = volume;
    }

    /**
     * Gets the surface area of the polyhedron.
     *
     * @return The surface area of the polyhedron.
     */
    public double getSurfaceArea ()
    {
        return surfaceArea;
    }

    /**
     * Sets the surface area of the polyhedron.
     *
     * @param surfaceArea The surface area of the polyhedron.
     */
    public void setSurfaceArea (double surfaceArea)
    {
        this.surfaceArea = surfaceArea;
    }

    /**
     * Gets the bounding box of the polyhedron.
     *
     * @return The bounding box of the polyhedron.
     */
    public double[] getBoundingBox ()
    {
        return boundingBox;
    }

    /**
     * Sets the bounding box of the polyhedron.
     *
     * @param boundingBox The bounding box of the polyhedron.
     */
    public void setBoundingBox (double[] boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    /**
     * Gets the center of the polyhedron.
     *
     * @return The center of the polyhedron.
     */
    public Vertex getCenter ()
    {
        return center;
    }

    /**
     * Sets the center of the polyhedron.
     *
     * @param center The center of the polyhedron.
     */
    public void setCenter (Vertex center)
    {
        this.center = center;
    }

    /**
     * Gets the number of triangles in the polyhedron.
     *
     * @return The number of triangles in the polyhedron.
     */
    public int getTriangleCount ()
    {
        return triangles.size();
    }

    /**
     * Returns the string representation of the polyhedron.
     *
     * @return
     */
    @Override
    public String toString ()
    {
        return Strings.POLYHEDRON_TOSTRING +
                Strings.POLYHEDRON_TOSTRING_2 + volume +
                Strings.POLYHEDRON_TOSTRING_3 + surfaceArea +
                Strings.POLYHEDRON_TOSTRING_4 + Arrays.toString(boundingBox) +
                Strings.POLYHEDRON_TOSTRING_5 + center +
                Strings.POLYHEDRON_TOSTRING_6 + triangles +
                Strings.CURLY_BRACKET_RIGHT;
    }
}
