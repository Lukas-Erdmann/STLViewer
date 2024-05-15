package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;

public class PolyhedronController
{
    private Polyhedron polyhedron;

    public PolyhedronController (Polyhedron polyhedron)
    {
        this.polyhedron = polyhedron;
    }

    public double calculateVolume ()
    {
        return this.polyhedron.calculateVolume();
    }

    public double calculateSurfaceArea ()
    {
        return this.polyhedron.calculateSurfaceArea();
    }
}
