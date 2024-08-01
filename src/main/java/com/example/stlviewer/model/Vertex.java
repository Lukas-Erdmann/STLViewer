package com.example.stlviewer.model;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.util.MathUtil;

import java.io.Serializable;

/**
 * The Vertex class represents a vertex in a 3D space. The class implements the Comparable interface to compare
 * vertices based on their distance from the origin.
 */
public class Vertex implements Comparable<Vertex>, Serializable
{
    /**
     * The x-coordinate of the vertex.
     */
    private double posX;
    /**
     * The y-coordinate of the vertex.
     */
    private double posY;
    /**
     * The z-coordinate of the vertex.
     */
    private double posZ;

    /**
     * Creates a new vertex with the given x, y, and z coordinates.
     *
     * @param posX - The x-coordinate of the vertex.
     * @param posY - The y-coordinate of the vertex.
     * @param posZ - The z-coordinate of the vertex.
     */
    public Vertex (double posX, double posY, double posZ)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    /**
     * Returns the x-coordinate of the vertex.
     *
     * @return The x-coordinate of the vertex.
     */
    public double getPosX ()
    {
        return posX;
    }

    /**
     * Sets the x-coordinate of the vertex.
     *
     * @param posX - The x-coordinate of the vertex.
     */
    public void setPosX (double posX)
    {
        this.posX = posX;
    }

    /**
     * Returns the y-coordinate of the vertex.
     *
     * @return The y-coordinate of the vertex.
     */
    public double getPosY ()
    {
        return posY;
    }

    /**
     * Sets the y-coordinate of the vertex.
     *
     * @param posY - The y-coordinate of the vertex.
     */
    public void setPosY (double posY)
    {
        this.posY = posY;
    }

    /**
     * Returns the z-coordinate of the vertex.
     *
     * @return The z-coordinate of the vertex.
     */
    public double getPosZ ()
    {
        return posZ;
    }

    /**
     * Sets the z-coordinate of the vertex.
     *
     * @param posZ - The z-coordinate of the vertex.
     */
    public void setPosZ (double posZ)
    {
        this.posZ = posZ;
    }

    /**
     * Returns the String representation of the vertex.
     *
     * @return The String representation of the vertex.
     */
    @Override
    public String toString ()
    {
        return Strings.VERTEX_TOSTRING + MathUtil.roundToThreeDigits(posX) + Strings.COMMA_SPACE +
                MathUtil.roundToThreeDigits(posY) + Strings.COMMA_SPACE +
                MathUtil.roundToThreeDigits(posZ) + Strings.CURLY_BRACKET_RIGHT;
    }

    /**
     * Compares two vertices based on their distance from the origin.
     * Overrides the compareTo method of the Comparable interface.
     * Precondition: The two vertices must exist.
     * Postcondition: The comparison of the two vertices based on their distance from the origin is returned.
     *
     * @param refVertex - The vertex to compare to.
     * @return The comparison of the two vertices based on their distance from the origin.
     */
    @Override
    public int compareTo (Vertex refVertex)
    {
        //Compare the two vertices based on their distance from the origin
        double distance1 = Math.sqrt(posX * posX + posY * posY + posZ * posZ);
        double distance2 = Math.sqrt(refVertex.posX * refVertex.posX + refVertex.posY * refVertex.posY + refVertex.posZ * refVertex.posZ);
        return Double.compare(distance1, distance2);
    }

    /**
     * Used to identify if two vertices are equal. Two vertices are equal if their x, y, and z coordinates are equal.
     * Overrides the equals method of the Object class.
     * Precondition: The two vertices must exist.
     * Postcondition: The equality of the two vertices is returned.
     *
     * @param refVertex - The vertex to compare to.
     * @return True if the vertices are equal, false otherwise.
     */
    @Override
    public boolean equals (Object refVertex)
    {
        if (refVertex instanceof Vertex vertex)
        {
            return Double.compare(vertex.posX, posX) == Constants.N_ZERO &&
                    Double.compare(vertex.posY, posY) == Constants.N_ZERO &&
                    Double.compare(vertex.posZ, posZ) == Constants.N_ZERO;
        }
        return false;
    }
}
