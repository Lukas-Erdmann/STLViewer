package com.example.stlviewer.model;

import javax.vecmath.Vector3d;

public class Triangle implements Comparable<Triangle>
{
    private Vertex[] vertices = new Vertex[3];
    private Vector3d[] edges = new Vector3d[3];
    private Vector3d normal = new Vector3d(0, 0, 0);
    private Vector3d unitNormal = new Vector3d(0, 0, 0);
    private double area;
    private int id;

    public Triangle (Vertex v1, Vertex v2, Vertex v3, Vector3d normal)
    {
        // Set the vertices
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        // TODO: Check if the vertices are unique

        // Set the normal
        edges[0] = new Vector3d(v2.getPosX() - v1.getPosX(), v2.getPosY() - v1.getPosY(), v2.getPosZ() - v1.getPosZ());
        edges[1] = new Vector3d(v3.getPosX() - v2.getPosX(), v3.getPosY() - v2.getPosY(), v3.getPosZ() - v2.getPosZ());
        edges[2] = new Vector3d(v1.getPosX() - v3.getPosX(), v1.getPosY() - v3.getPosY(), v1.getPosZ() - v3.getPosZ());

        // Validate the normal
        // TODO: This should be done numerically, but for now we will just recalculate it

        // The normal is the cross product of two edges in counter-clockwise order
        this.normal.cross(edges[0], edges[1]);
        // Normalize the normal, bringing it to unit length (length = 1)
        this.unitNormal = this.normal;
        this.unitNormal.normalize();
    }

    public void calculateArea ()
    {
        // Calculate the area of the triangle using the cross product of two edges
        area = 0.5 * normal.length();
    }

    public Vertex[] getVertices ()
    {
        return vertices;
    }

    public Vector3d[] getEdges ()
    {
        return edges;
    }

    public Vector3d getNormal ()
    {
        return normal;
    }

    /**
     * Get the vertex at the specified index. The index should be between 0 and 2.
     * @param index
     * @return
     */
    public Vertex getVertex (int index)
    {
        return vertices[index];
    }

    /**
     * Get the edge at the specified index. The index should be between 0 and 2.
     * @param index
     * @return
     */
    public Vector3d getEdge (int index)
    {
        return edges[index];
    }

    public double getArea ()
    {
        return area;
    }

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {
        this.id = id;
    }

    @Override
    public String toString ()
    {
        return "Triangle{" +
                "vertices=" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] +
                ", normal=" + normal +
                '}';
    }

    @Override
    public int compareTo (Triangle other)
    {
        if (this.area < other.area)
        {
            return -1;
        }
        else if (this.area > other.area)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
