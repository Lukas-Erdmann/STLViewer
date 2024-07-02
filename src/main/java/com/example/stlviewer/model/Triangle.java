package com.example.stlviewer.model;

import javax.vecmath.Vector3d;

public class Triangle extends Face implements Comparable<Triangle>
{
    private Vertex centroid;
    private double area;
    private int id;

    public Triangle (Vertex v1, Vertex v2, Vertex v3, Vector3d normal)
    {
        super(3);
        // Set the vertices
        vertices.add(0, v1);
        vertices.add(1, v2);
        vertices.add(2, v3);

        // Set the normal
        edges.add(0, new Edge(v2, v1));
        edges.add(1, new Edge(v3, v2));
        edges.add(2, new Edge(v1, v3));

        // The normal is the cross product of two edges in counter-clockwise order
        this.getNormal().cross(edges.get(0), edges.get(1));
        // Normalize the normal, bringing it to unit length (length = 1)
        this.getNormal().normalize();
        // Calculate the area of the triangle
        this.calculateArea();

        //System.out.println("Triangle created: " + this);
    }

    private void calculateArea ()
    {
        // Calculate the area of the triangle using Heron's formula
        double a = edges.get(0).length();
        double b = edges.get(1).length();
        double c = edges.get(2).length();

        // Calculate the semi-perimeter
        double s = (a + b + c) / 2;

        // Calculate the area
        this.area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    public double calculateVolumeWithReferenceVertex (Vertex refVertex) {
        // The volume of a tetrahedron is given by the formula:
        // V = 1/6 * |(a - d) . ((b - d) x (c - d))|
        // where a, b, c are the vertices of the triangle and d is the reference vertex
        Vector3d a = new Vector3d(vertices.get(0).getPosX() - refVertex.getPosX(),
                                  vertices.get(0).getPosY() - refVertex.getPosY(),
                                  vertices.get(0).getPosZ() - refVertex.getPosZ());
        Vector3d b = new Vector3d(vertices.get(1).getPosX() - refVertex.getPosX(),
                                  vertices.get(1).getPosY() - refVertex.getPosY(),
                                  vertices.get(1).getPosZ() - refVertex.getPosZ());
        Vector3d c = new Vector3d(vertices.get(2).getPosX() - refVertex.getPosX(),
                                  vertices.get(2).getPosY() - refVertex.getPosY(),
                                  vertices.get(2).getPosZ() - refVertex.getPosZ());
        // Calculate the cross product of b and c
        Vector3d crossProduct = new Vector3d();
        crossProduct.cross(b, c);
        // Calculate the scalar triple product of a, b, and c
        double scalarTripleProduct = a.dot(crossProduct);
        // Divide the absolute value of the scalar triple product by 6 to get the volume of the tetrahedron
        return Math.abs(scalarTripleProduct) / 6;
    }

    public Vertex getCentroid ()
    {
        if (centroid == null)
        {
            double x = (vertices.get(0).getPosX() + vertices.get(1).getPosX() + vertices.get(2).getPosX()) / 3;
            double y = (vertices.get(0).getPosY() + vertices.get(1).getPosY() + vertices.get(2).getPosY()) / 3;
            double z = (vertices.get(0).getPosZ() + vertices.get(1).getPosZ() + vertices.get(2).getPosZ()) / 3;
            centroid = new Vertex(x, y, z);
        }
        return centroid;
    }

    public boolean pointsAwayFromReferenceVertex (Vertex refVertex) {
        // Calculate the dot product of the normal of the triangle and the vector from the reference vertex to the centroid
        Vertex centroid = getCentroid();
        Vector3d referenceToCentroid = new Vector3d(refVertex.getPosX() - centroid.getPosX(),
                                                    refVertex.getPosY() - centroid.getPosY(),
                                                    refVertex.getPosZ() - centroid.getPosZ());
        double dotProduct = getNormal().dot(referenceToCentroid);
        // If the dot product is positive, the normal points away from the reference vertex
        return dotProduct > 0;
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
        // Return the vertices of the triangle with a string builder
        StringBuilder builder = new StringBuilder();
        builder.append("Triangle{");
        for (Edge edge : edges)
        {
            builder.append(edge.toString());
            builder.append(", ");
        }
        builder.append("normal = ");
        builder.append(getNormal().toString());
        builder.append(", area = ");
        builder.append(area);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int compareTo (Triangle other)
    {
        return Double.compare(this.area, other.area);
    }
}
