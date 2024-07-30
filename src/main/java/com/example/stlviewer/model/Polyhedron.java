package com.example.stlviewer.model;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.MathUtil;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Polyhedron class represents a polyhedron in a 3D space. It is a solid that is bounded by a finite number of
 * polygons. In this case, the polygons are triangles. The class contains a list of triangles that make up the polyhedron.
 * The class also contains the volume, surface area, bounding box, and center of the polyhedron.
 */
public class Polyhedron implements Serializable
{
    /**
     * The map of triangles that make up the polyhedron. The key is the ID of the triangle and the value is the
     * triangle itself. The map is thread-safe.
     */
    private ConcurrentHashMap<Integer, Triangle> triangles;
    /**
     * The volume of the polyhedron.
     */
    private double volume = Constants.N_ZERO;
    /**
     * The surface area of the polyhedron.
     */
    private double surfaceArea = Constants.N_ZERO;
    /**
     * The weight of the polyhedron.
     */
    private double weight = Constants.N_ZERO;
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
     * @param triangleMap - The list of triangles that make up the polyhedron.
     */
    public Polyhedron (ConcurrentHashMap<Integer, Triangle> triangleMap)
    {
        this.triangles = triangleMap;
    }

    /**
     * Creates a new polyhedron with an empty list of triangles.
     */
    public Polyhedron ()
    {
        this.triangles = new ConcurrentHashMap<>();
    }

    /**
     * Sets the list of triangles that make up the polyhedron.
     *
     * @param triangles The list of triangles that make up the polyhedron.
     */
    public void setTriangles (ConcurrentHashMap<Integer, Triangle> triangles)
    {
        this.triangles = triangles;
    }

    /**
     * Gets the list of triangles that make up the polyhedron.
     *
     * @return The list of triangles that make up the polyhedron.
     */
    public ConcurrentHashMap<Integer, Triangle> getTriangles ()
    {
        return triangles;
    }

    /**
     * Gets a triangle in the polyhedron by its ID.
     *
     * @param id    The ID of the triangle.
     * @return      The triangle with the given ID.
     */
    public Triangle getTriangle (int id)
    {
        return triangles.get(id);
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
     * Sets the volume of the polyhedron. Used when the volume of the polyhedron is calculated in one go.
     *
     * @param volume The volume of the polyhedron.
     */
    public void setVolume (double volume)
    {
        this.volume = volume;
    }

    /**
     * Adds the volume to the polyhedron. Used when the area of a triangle is calculated and added to the polyhedron.
     *
     * @param volume The volume of the polyhedron.
     */
    public void addVolume (double volume)
    {
        this.volume += volume;
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
     * Adds the surface area to the polyhedron.
     *
     * @param surfaceArea The surface area of the polyhedron.
     */
    public void addSurfaceArea (double surfaceArea)
    {
        this.surfaceArea += surfaceArea;
    }

    /**
     * Gets the weight of the polyhedron.
     *
     * @return The weight of the polyhedron.
     */
    public double getWeight ()
    {
        return weight;
    }

    /**
     * Sets the weight of the polyhedron.
     *
     * @param weight The weight of the polyhedron.
     */
    public void setWeight (double weight)
    {
        this.weight = weight;
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

    public String getBoundingBoxString()
    {
        return  Strings.BOUNDING_BOX_XMIN + MathUtil.roundToThreeDigits(boundingBox[0]) +
                Strings.BOUNDING_BOX_YMIN + MathUtil.roundToThreeDigits(boundingBox[1]) +
                Strings.BOUNDING_BOX_ZMIN + MathUtil.roundToThreeDigits(boundingBox[2]) +
                Strings.BOUNDING_BOX_XMAX + MathUtil.roundToThreeDigits(boundingBox[3]) +
                Strings.BOUNDING_BOX_YMAX + MathUtil.roundToThreeDigits(boundingBox[4]) +
                Strings.BOUNDING_BOX_ZMAX + MathUtil.roundToThreeDigits(boundingBox[5]) +
                Strings.BLOCKY_BRACKET_RIGHT;
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
     * @return  The string representation of the polyhedron.
     */
    @Override
    public String toString ()
    {
        return Strings.POLYHEDRON_TOSTRING +
                Strings.POLYHEDRON_TOSTRING_2 + MathUtil.roundToThreeDigits(volume) +
                Strings.POLYHEDRON_TOSTRING_3 + MathUtil.roundToThreeDigits(surfaceArea) +
                Strings.POLYHEDRON_TOSTRING_4 + getBoundingBoxString() +
                Strings.POLYHEDRON_TOSTRING_5 + center +
                Strings.POLYHEDRON_TOSTRING_6 + triangles +
                Strings.CURLY_BRACKET_RIGHT;
    }
}
