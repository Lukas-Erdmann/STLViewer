package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.MathUtil;

import javax.vecmath.Vector3d;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Face class represents a face in a 3D space. It is a polygon that lies in a plane and has a normal vector.
 * The normal vector is perpendicular to the plane of the face. The class extends the Polygon class.
 */
public class Face extends Polygon implements Serializable
{
    /**
     * The normal vector of the face.
     */
    private Vector3d normal;

    /**
     * Creates a new face with the given vertices, edges, and normal vector.
     *
     * @param vertices - The vertices of the face.
     * @param edges    - The edges of the face.
     * @param normal   - The normal vector of the face.
     */
    public Face (ArrayList<Vertex> vertices, ArrayList<Edge> edges, Vector3d normal)
    {
        this.vertices = vertices;
        this.edges = edges;
        this.normal = normal;
    }

    /**
     * Returns the normal vector of the face.
     *
     * @return The normal vector of the face.
     */
    public Face (int size)
    {
        super(size);
        normal = new Vector3d();
    }

    /**
     * Returns the normal vector of the face.
     *
     * @return The normal vector of the face.
     */
    public Vector3d getNormal ()
    {
        return normal;
    }

    /**
     * Sets the normal vector of the face.
     *
     * @param normal - The normal vector of the face.
     */
    public void setNormal (Vector3d normal)
    {
        this.normal = normal;
    }

    /**
     * Returns the normal vector of the face as a string.
     *
     * @return The normal vector of the face as a string.
     */
    public String getNormalString()
    {
        return MathUtil.roundToThreeDigits(normal.getX()) + Strings.SPACE + MathUtil.roundToThreeDigits(normal.getY()) +
                Strings.SPACE + MathUtil.roundToThreeDigits(normal.getZ());
    }
}
