package com.example.stlviewer.model;

public class Polygon extends PolygonalChain
{
    public Polygon() {
        super();
    }

    public Polygon(int size) {
        super(size, size);
    }

    @Override
    public void addEdge(Edge edge) {
        if (!isClosed()) {
            super.addEdge(edge);
        } else {
            throw new IllegalArgumentException("The polygon is already closed.");
        }
    }
}
