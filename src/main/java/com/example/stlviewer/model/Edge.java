package com.example.stlviewer.model;

import javax.vecmath.Vector3d;

/**
 * The Edge class represents an edge in a 3D space.
 */
public class Edge extends Vector3d
{
    /**
     * The start vertex of the edge.
     */
    private Vertex startVertex;
    /**
     * The end vertex of the edge.
     */
    private Vertex endVertex;

    /**
     * Creates a new edge with the given start and end vertex. The direction of the edge is from the start to the end vertex.
     * Also calls the super constructor of the Vector3d class to make it a 3D vector.
     *
     * @param start - The start vertex of the edge.
     * @param end - The end vertex of the edge.
     */
    public Edge(Vertex start, Vertex end)
    {
        super(end.getPosX() - start.getPosX(), end.getPosY() - start.getPosY(), end.getPosZ() - start.getPosZ());
        this.startVertex = start;
        this.endVertex = end;
    }

    /**
     * Returns the start vertex of the edge.
     *
     * @return The start vertex of the edge.
     */
    public Vertex getStartVertex ()
    {
        return startVertex;
    }

    /**
     * Returns the end vertex of the edge.
     *
     * @return The end vertex of the edge.
     */
    public Vertex getEndVertex ()
    {
        return endVertex;
    }

    /**
     * Used to identify if two edges are equal. Two edges are equal if their start and end vertices are equal.
     * Overrides the equals method of the Object class.
     * Precondition: The two edges must exist.
     * Postcondition: The equality of the two edges is returned.
     *
     * @param refEdge - The edge to compare to.
     * @return True if the edges are equal, false otherwise.
     */
    @Override
    public boolean equals(Object refEdge)
    {
        if (refEdge instanceof Edge)
        {
            Edge edge = (Edge) refEdge;
            return startVertex.equals(edge.startVertex) && endVertex.equals(edge.endVertex);
        }
        return false;
    }

    /**
     * Returns the string representation of the edge.
     *
     * @return The string representation of the edge.
     */
    @Override
    public String toString()
    {
        return "Edge{" + startVertex + " -> " + endVertex + "}";
    }
}
