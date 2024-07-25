package com.example.stlviewer.model;

import javafx.scene.paint.PhongMaterial;

public class Material extends PhongMaterial implements java.io.Serializable{
    private final String name;
    private String description;
    private final double density;

    public Material (String name, double density, String colorHex)
    {
        this.name = name;
        this.description = name;
        this.density = density;
        setDiffuseColor(javafx.scene.paint.Color.web(colorHex));
    }

    public Material (String name, double density, String colorHex, String specularColorHex, double specularPower)
    {
        this.name = name;
        this.description = name;
        this.density = density;
        setDiffuseColor(javafx.scene.paint.Color.web(colorHex));
        setSpecularColor(javafx.scene.paint.Color.web(specularColorHex));
        setSpecularPower(specularPower);
    }

    public Material (String materialString)
    {
        String[] materialData = materialString.split(",");
        this.name = materialData[0];
        this.description = materialData[1];
        this.density = Double.parseDouble(materialData[2]);
        setDiffuseColor(javafx.scene.paint.Color.web(materialData[3]));
        if (materialData.length > 4)
        {
            setSpecularColor(javafx.scene.paint.Color.web(materialData[4]));
            setSpecularPower(Double.parseDouble(materialData[5]));
        }
    }

    public String getName ()
    {
        return name;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public double getDensity ()
    {
        return density;
    }

    public String materialToString ()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(",").append(description).append(",").append(density).append(",").append(getDiffuseColor().toString());
        if (getSpecularColor() != null)
        {
            stringBuilder.append(",").append(getSpecularColor().toString()).append(",").append(getSpecularPower());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString ()
    {
        return "Material{name='" + name + "', description='" + description + "', density=" + density + ", color=" + getDiffuseColor().toString() + "}";
    }
}
