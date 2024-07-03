package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;

import java.io.Serializable;
import java.util.Arrays;

public class P2PPackage implements Serializable
{
    /**
     * The Polyhedron object to be transmitted.
     */
    private final Polyhedron polyhedron;
    /**
     * The translation values for the Polyhedron object.
     */
    private final double translationX, translationY, translationZ;
    /**
     * The rotation values for the Polyhedron object.
     */
    private final double rotationX, rotationY;
    /**
     * The zoom factor for camera view.
     */
    private final double zoom;
    /**
     * The anchor angles for rotation.
     */
    private final double anchorAngleX, anchorAngleY;

    /**
     * Constructs a P2PPackage with the specified Polyhedron and transformation properties.
     *
     * @param polyhedron - The Polyhedron object.
     * @param translateX - The X translation value.
     * @param translateY - The Y translation value.
     * @param translateZ - The Z translation value.
     * @param rotateX - The X rotation angle.
     * @param rotateY - The Y rotation angle.
     * @param zoom - The zoom factor.
     * @param anchorAngleX - The anchor angle for X-axis rotation.
     * @param anchorAngleY - The anchor angle for Y-axis rotation.
     */
    public P2PPackage (Polyhedron polyhedron, double translateX, double translateY, double translateZ, double rotateX,
                       double rotateY, double zoom, double anchorAngleX, double anchorAngleY)
    {
        this.polyhedron = polyhedron;
        this.translationX = translateX;
        this.translationY = translateY;
        this.translationZ = translateZ;
        this.rotationX = rotateX;
        this.rotationY = rotateY;
        this.zoom = zoom;
        this.anchorAngleX = anchorAngleX;
        this.anchorAngleY = anchorAngleY;
    }

    /**
     * Gets the Polyhedron object.
     *
     * @return The Polyhedron object.
     */
    public Polyhedron getPolyhedron ()
    {
        return polyhedron;
    }

    /**
     * Gets the X translation value.
     *
     * @return The X translation value.
     */
    public double getTranslationX ()
    {
        return translationX;
    }

    /**
     * Gets the Y translation value.
     *
     * @return The Y translation value.
     */
    public double getTranslationY ()
    {
        return translationY;
    }

    /**
     * Gets the Z translation value.
     *
     * @return The Z translation value.
     */
    public double getTranslationZ ()
    {
        return translationZ;
    }

    /**
     * Gets the X rotation angle.
     *
     * @return The X rotation angle.
     */
    public double getRotationX ()
    {
        return rotationX;
    }

    /**
     * Gets the Y rotation angle.
     *
     * @return The Y rotation angle.
     */
    public double getRotationY ()
    {
        return rotationY;
    }

    /**
     * Gets the Z rotation angle.
     *
     * @return The Z rotation angle.
     */
    public double getZoom ()
    {
        return zoom;
    }

    /**
     * Gets the anchor angle for X-axis rotation.
     *
     * @return The anchor angle for X-axis rotation.
     */
    public double getAnchorAngleX ()
    {
        return anchorAngleX;
    }

    /**
     * Gets the anchor angle for Y-axis rotation.
     *
     * @return The anchor angle for Y-axis rotation.
     */
    public double getAnchorAngleY ()
    {
        return anchorAngleY;
    }

    /**
     * Returns a string representation of the P2PPackage.
     *
     * @return A string representation of the P2PPackage.
     */
    @Override
    public String toString ()
    {
        return Strings.P2PPACKAGE_TOSTRING +
                Strings.P2PPACKAGE_TOSTRING_2 + polyhedron +
                Strings.P2PPACKAGE_TOSTRING_3 + Arrays.toString(new double[]{translationX, translationY, translationZ}) +
                Strings.P2PPACKAGE_TOSTRING_4 + Arrays.toString(new double[]{rotationX, rotationY}) +
                Strings.P2PPACKAGE_TOSTRING_5 + zoom +
                Strings.P2PPACKAGE_TOSTRING_6 + Arrays.toString(new double[]{anchorAngleX, anchorAngleY}) +
                Strings.CURLY_BRACKET_RIGHT;
    }
}
