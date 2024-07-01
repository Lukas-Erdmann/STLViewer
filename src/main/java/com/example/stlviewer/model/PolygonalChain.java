package com.example.stlviewer.model;

import java.util.ArrayList;

public class PolygonalChain
{
    protected ArrayList<Edge> edges;
    protected ArrayList<Vertex> vertices;
    private boolean isClosed;

    public PolygonalChain ()
    {
        this.edges = new ArrayList<Edge>();
        this.vertices = new ArrayList<Vertex>();
    }

    public PolygonalChain (int sizeEdges, int sizeVertices)
    {
        this.edges = new ArrayList<Edge>(sizeEdges);
        this.vertices = new ArrayList<Vertex>(sizeVertices);
    }

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
                throw new IllegalArgumentException("The edge is already in the chain.");
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
                throw new IllegalArgumentException("The start vertex of the edge is not the last vertex of the chain.");
            }
        }

        this.edges.add(edge);
    }

    public Edge getEdge(int index)
    {
        return edges.get(index);
    }

    public ArrayList<Edge> getEdges()
    {
        return edges;
    }

    public Vertex getVertex(int index)
    {
        return vertices.get(index);
    }

    public ArrayList<Vertex> getVertices ()
    {
        return vertices;
    }

    public int getVertexCount()
    {
        return vertices.size();
    }

    public int getEdgeCount()
    {
        return edges.size();
    }

    public boolean isClosed()
    {
        return isClosed;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PolygonalChain{");
        for (Edge edge : edges)
        {
            builder.append(edge.toString());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("}");
        return builder.toString();
    }
}
