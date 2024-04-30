package com.example.stlviewer.model;

public class Polyhedron
{
    private Triangles triangles;
    private double volume;
    private double surfaceArea;

    public Polyhedron (Triangles triangles)
    {
        this.triangles = triangles;
    }

    /**
     * Calculate the volume of the polyhedron. This is done by summing the volumes of all the
     * tetrahedra formed with an arbitrary point within the polyhedron and each triangle in
     * the polyhedron.
     * @return
     */
    public double calculateVolume () {
        // TODO: Find algorithm to calculate the volume of a polyhedron regardless of its shape (Convex or concave)
        // You could use a raycast to count the intersections with the shape's surface.
        // A uneven number of intersections means the point is inside the shape, and an even number means it's outside.
        return this.volume;
    }

    /**
     * Calculate the surface area of the polyhedron. This is done by summing the areas of all the
     * triangles in the polyhedron.
     * @return
     */
    public double calculateSurfaceArea () {
        for (Triangle triangle : triangles) {
            this.surfaceArea += triangle.getArea();
        }
        return this.surfaceArea;
    }

    public Triangles getTriangles ()
    {
        return triangles;
    }
}
