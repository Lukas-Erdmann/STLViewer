package com.example.stlviewer.model;

import javax.vecmath.Vector3d;

public class Edge extends Vector3d
{
    private Vertex startVertex;
    private Vertex endVertex;

    public Edge(Vertex start, Vertex end)
    {
        this.startVertex = start;
        this.endVertex = end;
    }

    public Vertex getStartVertex ()
    {
        return startVertex;
    }

    public Vertex getEndVertex ()
    {
        return endVertex;
    }

    public boolean equals(Object refEdge)
    {
        if (refEdge instanceof Edge)
        {
            Edge edge = (Edge) refEdge;
            return startVertex.equals(edge.startVertex) && endVertex.equals(edge.endVertex);
        }
        return false;
    }

    public String toString()
    {
        return "Edge{" + startVertex + " -> " + endVertex + "}";
    }
}
