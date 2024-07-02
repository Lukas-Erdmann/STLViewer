package com.example.stlviewer.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Polyhedron
{
    private ArrayList<Triangle> triangles;
    private double volume = 0;
    private double surfaceArea = 0;
    private double[] boundingBox;
    private Vertex center;

    public Polyhedron (ArrayList<Triangle> triangleArrayList) {
        this.triangles = triangleArrayList;
    }

    public Polyhedron () {
        this.triangles = new ArrayList<Triangle>();
    }

    public ArrayList<Triangle> getTriangles () {
        return triangles;
    }

    public void setVolume (double volume) {
        this.volume = volume;
    }

    public double getVolume () {
        return volume;
    }

    public void setSurfaceArea (double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public double getSurfaceArea () {
        return surfaceArea;
    }

    public void setBoundingBox (double[] boundingBox) {
        this.boundingBox = boundingBox;
    }

    public double[] getBoundingBox () {
        return boundingBox;
    }

    public void setCenter (Vertex center) {
        this.center = center;
    }

    public Vertex getCenter () {
        return center;
    }

    public int getTriangleCount() {
        return triangles.size();
    }

    @Override
    public String toString () {
        return "Polyhedron{" +
                "volume=" + volume +
                ", surfaceArea=" + surfaceArea +
                ", boundingBox=" + Arrays.toString(boundingBox) +
                ", center=" + center +
                ", triangles=" + triangles +
                '}';
    }
}
