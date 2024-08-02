package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The P2PPackage class represents a package that is sent between two clients in a peer-to-peer connection. It contains
 * a Polyhedron object and transformation properties.
 */
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
     * The zoom parameters.
     */
    private final double zoomLimit, zoomCoefficient;
    /**
     * The model material as a String.
     */
    private final String material;

    /**
     * Constructs a P2PPackage with the specified Polyhedron and transformation properties.
     *
     * @param polyhedron        The Polyhedron object.
     * @param translateX        The X translation value.
     * @param translateY        The Y translation value.
     * @param translateZ        The Z translation value.
     * @param rotateX           The X rotation angle.
     * @param rotateY           The Y rotation angle.
     * @param zoomLimit         The Z rotation angle.
     * @param zoomCoefficient   The Z rotation angle.
     * @param material          The model material.
     */
    public P2PPackage (Polyhedron polyhedron, double translateX, double translateY, double translateZ, double rotateX,
                       double rotateY,  double zoomLimit, double zoomCoefficient, Material material)
    {
        this.polyhedron = polyhedron;
        this.translationX = translateX;
        this.translationY = translateY;
        this.translationZ = translateZ;
        this.rotationX = rotateX;
        this.rotationY = rotateY;
        this.zoomLimit = zoomLimit;
        this.zoomCoefficient = zoomCoefficient;
        this.material = material.materialToString();
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
     * Gets the zoom limit.
     *
     * @return The zoom limit.
     */
    public double getZoomLimit() {
        return zoomLimit;
    }

    /**
     * Gets the zoom coefficient.
     *
     * @return The zoom coefficient.
     */
    public double getZoomCoefficient() {
        return zoomCoefficient;
    }

    /**
     * Gets the model material.
     *
     * @return The model material.
     */
    public Material getMaterial ()
    {
        return new Material(material);
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
                Strings.P2PPACKAGE_TOSTRING_5 + Arrays.toString(new double[]{zoomLimit, zoomCoefficient}) +
                Strings.COMMA_SPACE + new Material(material) +
                Strings.CURLY_BRACKET_RIGHT;
    }
}
