package com.example.stlviewer.model;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class Polyhedron
{
    private ArrayList<Triangle> triangles;
    private double volume = 0;
    private double surfaceArea = 0;

    private ArrayList<ArrayList<Integer>> adjacencyList;
    private int idCounter = 0;

    public Polyhedron (ArrayList<Triangle> triangleArrayList) {
        this.triangles = triangleArrayList;
    }

    public Polyhedron () {
        this.triangles = new ArrayList<Triangle>();
    }

    /**
     * Add a triangle to the list of triangles. If the triangle is already in the list, don't add it.
     * If the triangle is a neighbor of another triangle, add it to the adjacency list.
     * @param triangle  Triangle to add
     */
    public void addTriangle (Triangle triangle)
    {
        boolean isAlreadyAdded = false;
        for (Triangle t : this.triangles) {
            // Check if the triangle is already in the list by comparing the vertices
            // If 0 or 1 vertices are the same, then the triangles aren't the neighbors
            // If 2 vertices are the same, then the triangles are neighbors
            // If 3 vertices are the same, then the triangle is already in the list
            int sameVertices = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (t.getVertices().get(i).equals(triangle.getVertices().get(j))) {
                        sameVertices++;
                    }
                }
            }
            // If the triangle is already in the list, don't add it
            if (sameVertices == 3) {
                throw new IllegalArgumentException("Triangle is already in the list");
            }
            // In all other cases, add the triangle to the list and increment the id counter
            this.triangles.add(triangle);
            idCounter++;
            triangle.setId(idCounter);
            // If the triangles are neighbors, add them to the adjacency list
            if (sameVertices == 2) {
                adjacencyList.get(t.getId()).add(triangle.getId());
                adjacencyList.get(triangle.getId()).add(t.getId());
            }
        }
    }

    /**
     * Calculate the volume of the polyhedron. This is done by summing the volumes of all the
     * tetrahedra formed with an arbitrary point within the polyhedron and each triangle in
     * the polyhedron.
     * @return
     */
    public double calculateVolume () {
        // The arbitrary point is the origin
        Vertex arbitraryPoint = new Vertex(0, 0, 0);
        // For each triangle in the polyhedron, calculate the volume of the tetrahedron formed
        for (Triangle triangle : triangles) {
            if (triangle.pointsAwayFromReferenceVertex(arbitraryPoint)) {
                // If the triangle points away from the reference vertex, add the volume
                this.volume += triangle.calculateVolumeWithReferenceVertex(arbitraryPoint);
            } else {
                // If the triangle points towards the reference vertex, subtract the volume
                this.volume -= triangle.calculateVolumeWithReferenceVertex(arbitraryPoint);
            }
        }
        return this.volume;
    }

    /**
     * Calculate the surface area of the polyhedron. This is done by summing the areas of all the
     * triangleMesh in the polyhedron.
     * @return
     */
    public double calculateSurfaceArea () {
        for (Triangle triangle : triangles) {
            this.surfaceArea += triangle.getArea();
        }
        return this.surfaceArea;
    }

    public double[] defineBoundingBox () {
        double minValueX = Double.MAX_VALUE;
        double minValueY = Double.MAX_VALUE;
        double minValueZ = Double.MAX_VALUE;
        double maxValueX = Double.MIN_VALUE;
        double maxValueY = Double.MIN_VALUE;
        double maxValueZ = Double.MIN_VALUE;

        for (Triangle triangle : triangles) {
            for (Vertex vertex : triangle.getVertices()) {
                if (vertex.getPosX() < minValueX) {
                    minValueX = vertex.getPosX();
                }
                if (vertex.getPosY() < minValueY) {
                    minValueY = vertex.getPosY();
                }
                if (vertex.getPosZ() < minValueZ) {
                    minValueZ = vertex.getPosZ();
                }
                if (vertex.getPosX() > maxValueX) {
                    maxValueX = vertex.getPosX();
                }
                if (vertex.getPosY() > maxValueY) {
                    maxValueY = vertex.getPosY();
                }
                if (vertex.getPosZ() > maxValueZ) {
                    maxValueZ = vertex.getPosZ();
                }
            }
        }
        return new double[] {minValueX, minValueY, minValueZ, maxValueX, maxValueY, maxValueZ};
    }

    public ArrayList<Triangle> getTriangles () {
        return triangles;
    }
}
