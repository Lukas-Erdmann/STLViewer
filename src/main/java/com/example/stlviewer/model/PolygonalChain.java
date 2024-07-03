package com.example.stlviewer.model;

import com.example.stlviewer.res.Strings;

import java.util.ArrayList;

/**
 * The PolygonalChain class represents a polygonal chain in a 3D space. It is a chain of edges that are connected
 * by their vertices. The class contains a list of edges and vertices that make up the chain.
 */
public class PolygonalChain
{
    /**
     * The list of edges that make up the chain.
     */
    protected ArrayList<Edge> edges;
    /**
     * The list of vertices that make up the chain.
     */
    protected ArrayList<Vertex> vertices;
    /**
     * A boolean that indicates if the chain is closed, making it a polygon.
     */
    private boolean isClosed;

    /**
     * Creates a new polygonal chain with an empty list of edges and vertices.
     */
    public PolygonalChain ()
    {
        this.edges = new ArrayList<Edge>();
        this.vertices = new ArrayList<Vertex>();
    }

    /**
     * Creates a new polygonal chain with the given size of edges and vertices.
     *
     * @param sizeEdges - The size of the list of edges.
     * @param sizeVertices - The size of the list of vertices.
     */
    public PolygonalChain (int sizeEdges, int sizeVertices)
    {
        this.edges = new ArrayList<Edge>(sizeEdges);
        this.vertices = new ArrayList<Vertex>(sizeVertices);
    }

    /**
     * Adds an edge to the chain. If the chain is empty, the edge is added to the chain. If the chain is not empty,
     * the edge is added to the chain if the start vertex of the edge is the last vertex of the chain. If the end vertex
     * of the edge is already in the chain, the chain is closed.
     * Precondition: The edge must exist.
     * Postcondition: The edge is added to the chain.
     *
     * @param edge - The edge to add.
     */
    public void addEdge(Edge edge)
    {
        if (vertices.isEmpty())
        {
            vertices.add(edge.getStartVertex());
            vertices.add(edge.getEndVertex());
        } else
        {
            if (edges.contains(edge))
            {
                throw new IllegalArgumentException(Strings.THE_EDGE_IS_ALREADY_IN_THE_CHAIN);
            } else if (vertices.contains(edge.getStartVertex()))
            {
                if (vertices.contains(edge.getEndVertex()))
                {
                    isClosed = true;
                } else
                {
                    vertices.add(edge.getEndVertex());
                }
            } else
            {
                throw new IllegalArgumentException(Strings.THE_START_VERTEX_OF_THE_EDGE_IS_NOT_THE_LAST_VERTEX_OF_THE_CHAIN);
            }
        }

        this.edges.add(edge);
    }

    /**
     * Returns the edge at the given index.
     *
     * @param index - The index of the edge.
     * @return The edge at the given index.
     */
    public Edge getEdge(int index)
    {
        return edges.get(index);
    }

    /**
     * Returns the list of edges that make up the chain.
     *
     * @return The list of edges that make up the chain.
     */
    public ArrayList<Edge> getEdges()
    {
        return edges;
    }

    /**
     * Returns the vertex at the given index.
     *
     * @param index - The index of the vertex.
     * @return The vertex at the given index.
     */
    public Vertex getVertex(int index)
    {
        return vertices.get(index);
    }

    /**
     * Returns the list of vertices that make up the chain.
     *
     * @return The list of vertices that make up the chain.
     */
    public ArrayList<Vertex> getVertices ()
    {
        return vertices;
    }

    /**
     * Returns the number of vertices in the chain.
     *
     * @return The number of vertices in the chain.
     */
    public int getVertexCount()
    {
        return vertices.size();
    }

    /**
     * Returns the number of edges in the chain.
     *
     * @return The number of edges in the chain.
     */
    public int getEdgeCount()
    {
        return edges.size();
    }

    /**
     * Returns a boolean that indicates if the chain is closed.
     *
     * @return True if the chain is closed, false otherwise.
     */
    public boolean isClosed()
    {
        return isClosed;
    }

    /**
     * Returns a string representation of the polygonal chain.
     *
     * @return A string representation of the polygonal chain.
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(Strings.POLYGONAL_CHAIN_TOSTRING);
        for (Edge edge : edges)
        {
            builder.append(edge.toString());
            builder.append(Strings.COMMA_SPACE);
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(Strings.CURLY_BRACKET_RIGHT);
        return builder.toString();
    }
}
