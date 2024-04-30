package com.example.stlviewer.model;

import java.util.ArrayList;

public class Triangles extends ArrayList<Triangle>
{
    private ArrayList<ArrayList<Integer>> adjacencyList;
    private int idCounter = 0;

    public Triangles (int initialCapacity)
    {
        super(initialCapacity);
        adjacencyList = new ArrayList<>();
    }

    /**
     * Add a triangle to the list of triangles. If the triangle is already in the list, don't add it.
     * If the triangle is a neighbor of another triangle, add it to the adjacency list.
     * @param triangle  Triangle to add
     */
    public void addTriangle (Triangle triangle)
    {
        boolean isAlreadyAdded = false;
        for (Triangle t : this) {
            // Check if the triangle is already in the list by comparing the vertices
            // If 0 or 1 vertices are the same, then the triangles aren't the neighbors
            // If 2 vertices are the same, then the triangles are neighbors
            // If 3 vertices are the same, then the triangle is already in the list
            // TODO: Assure that the result can't be 4 or more due to non-unique vertices
            int sameVertices = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (t.getVertices()[i].equals(triangle.getVertices()[j])) {
                        sameVertices++;
                    }
                }
            }
            // If the triangle is already in the list, don't add it
            if (sameVertices == 3) {
                isAlreadyAdded = true;
                break;
            }
            // In all other cases, add the triangle to the list and increment the id counter
            this.add(triangle);
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
     * Remove a triangle from the list of triangles by its id. If the triangle is a neighbor of another triangle,
     * remove it from the adjacency list.
     * @param triangleID    ID of the triangle to remove
     */
    public void removeTriangle (int triangleID) {
        // Remove the triangle from the list
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getId() == triangleID) {
                this.remove(i);
                break;
            }
        }
        // Remove the triangle from the adjacency list
        for (int i = 0; i < adjacencyList.size(); i++) {
            for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                if (adjacencyList.get(i).get(j) == triangleID) {
                    adjacencyList.get(i).remove(j);
                    break;
                }
            }
        }
    }

    /**
     * Check if the list of triangles is tesselated. A tesselated list of triangles has each triangle connected to 3
     * neighbors.
     * @return  List of triangles that are not tesselated
     */
    public ArrayList<Integer> checkTesselation () {
        ArrayList<Integer> nonTesselatedTriangles = new ArrayList<>();
        // Check if each triangle has 3 neighbors
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (adjacencyList.get(i).size() != 3) {
                nonTesselatedTriangles.add(i);
            }
        }
        return nonTesselatedTriangles;
    }

    public ArrayList<Integer> checkNormals () {
        ArrayList<Integer> wrongNormals = new ArrayList<>();
        // Check if each edge has a corresponding edge with the value but opposite sign
        // TODO: Implement this method
        return wrongNormals;
    }
}