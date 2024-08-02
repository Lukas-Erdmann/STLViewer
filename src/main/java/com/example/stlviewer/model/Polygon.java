package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;

import java.io.Serializable;

/**
 * The Polygon class represents a polygon in a 3D space. It is a polygonal chain that is closed.
 * Because of this, the addEdge method is overridden to prevent adding more edges after the polygon is closed.
 * Because the start and end vertex of the polygon are the same, the number of vertices is equal to the number of edges.
 * The class extends the PolygonalChain class.
 */
public class Polygon extends PolygonalChain implements Serializable
{

    /**
     * Creates a new polygon with the given size.
     */
    public Polygon ()
    {
        super();
    }

    /**
     * Creates a new polygon with the given size.
     *
     * @param size - The size of the polygon.
     */
    public Polygon (int size)
    {
        super(size, size);
    }

    /**
     * Adds an edge to the polygon. If the polygon is already closed, an exception is thrown.
     * Precondition: The edge must exist.
     * Postcondition: The edge is added to the polygon.
     *
     * @param edge - The edge to add.
     */
    @Override
    public void addEdge (Edge edge)
    {
        if (!isClosed())
        {
            super.addEdge(edge);
        } else
        {
            throw new IllegalArgumentException(Strings.EXCEPTION_POLYGON_ALREADY_CLOSED);
        }
    }
}
