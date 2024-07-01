package com.example.stlviewer.model;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class Face extends Polygon
{
    private Vector3d normal;

    public Face(ArrayList<Vertex> vertices, ArrayList<Edge> edges, Vector3d normal) {
        this.vertices = vertices;
        this.edges = edges;
        this.normal = normal;
    }

    public Face(int size) {
        super(size);
        normal = new Vector3d();
    }

    public void setNormal(Vector3d normal) {
        this.normal = normal;
    }

    public Vector3d getNormal() {
        return normal;
    }
}
