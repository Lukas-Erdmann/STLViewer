package com.example.stlviewer.model;

import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import javafx.scene.paint.PhongMaterial;

/**
 * The Material class represents a material with a name, description, density, and color and optionally specular properties.
 */
public class Material extends PhongMaterial implements java.io.Serializable{
    private final String name;
    private String description;
    private final double density;

    /**
     * Constructs a Material with the specified name, density, and color.
     *
     * @param name      The name of the material.
     * @param density   The density of the material.
     * @param colorHex  The color of the material in hexadecimal format.
     */
    public Material (String name, double density, String colorHex)
    {
        this.name = name;
        this.description = name;
        this.density = density;
        setDiffuseColor(javafx.scene.paint.Color.web(colorHex));
    }

    /**
     * Constructs a Material with the specified name, density, color, specular color, and specular power.
     *
     * @param name              The name of the material.
     * @param density           The density of the material.
     * @param colorHex          The color of the material in hexadecimal format.
     * @param specularColorHex  The specular color of the material in hexadecimal format.
     * @param specularPower     The specular power of the material.
     */
    public Material (String name, double density, String colorHex, String specularColorHex, double specularPower)
    {
        this.name = name;
        this.description = name;
        this.density = density;
        setDiffuseColor(javafx.scene.paint.Color.web(colorHex));
        setSpecularColor(javafx.scene.paint.Color.web(specularColorHex));
        setSpecularPower(specularPower);
    }

    /**
     * Constructs a Material from the specified material string. The material string is a comma-separated string
     * containing the name, description, density, color, and optionally specular color and specular power.
     *
     * @param materialString    The material string.
     */
    public Material (String materialString)
    {
        String[] materialData = materialString.split(Strings.COMMA);
        this.name = materialData[Constants.MATERIAL_STRING_POS_NAME];
        this.description = materialData[Constants.MATERIAL_TO_STRING_DESC];
        this.density = Double.parseDouble(materialData[Constants.MATERIAL_TO_STRING_DENSITY]);
        setDiffuseColor(javafx.scene.paint.Color.web(materialData[Constants.MATERIAL_TO_STRING_COLOR]));
        if (materialData.length > Constants.MATERIAL_TO_STRING_SPECULAR_COLOR)
        {
            setSpecularColor(javafx.scene.paint.Color.web(materialData[Constants.MATERIAL_TO_STRING_SPECULAR_COLOR]));
            setSpecularPower(Double.parseDouble(materialData[Constants.MATERIAL_TO_STRING_SPECULAR_POWER]));
        }
    }

    /**
     * Gets the name of the material.
     *
     * @return  The name of the material.
     */
    public String getName ()
    {
        return name;
    }

    /**
     * Gets the description of the material.
     *
     * @return  The description of the material.
     */
    public String getDescription ()
    {
        return description;
    }

    /**
     * Sets the description of the material.
     *
     * @param description   The description of the material.
     */
    public void setDescription (String description)
    {
        this.description = description;
    }

    /**
     * Gets the density of the material.
     *
     * @return  The density of the material.
     */
    public double getDensity ()
    {
        return density;
    }

    /**
     * Converts the material to a string. The string is a comma-separated string containing the name, description,
     * density, color, and optionally specular color and specular power and can be used to recreate the material.
     *
     * @return  The material as a string.
     */
    public String materialToString ()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(Strings.COMMA).append(description).append(Strings.COMMA).append(density).append(Strings.COMMA).append(getDiffuseColor().toString());
        if (getSpecularColor() != null)
        {
            stringBuilder.append(Strings.COMMA).append(getSpecularColor().toString()).append(Strings.COMMA).append(getSpecularPower());
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the string representation of the material.
     *
     * @return The string representation of the material.
     */
    @Override
    public String toString ()
    {
        // TODO: Put into interface
        return Strings.MATERIAL_TO_STRING_1 + name
                + Strings.MATERIAL_TO_STRING_2 + description
                + Strings.MATERIAL_TO_STRING_3 + density
                + Strings.MATERIAL_TO_STRING_4 + getDiffuseColor().toString()
                + Strings.CURLY_BRACKET_RIGHT;
    }
}
