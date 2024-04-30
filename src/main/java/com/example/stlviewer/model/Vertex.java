package com.example.stlviewer.model;

public class Vertex implements Comparable<Vertex>
{
    private double posX;
    private double posY;
    private double posZ;

    public Vertex (double posX, double posY, double posZ)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public double getPosX ()
    {
        return posX;
    }

    public double getPosY ()
    {
        return posY;
    }

    public double getPosZ ()
    {
        return posZ;
    }

    public void setPosX (double posX)
    {
        this.posX = posX;
    }

    public void setPosY (double posY)
    {
        this.posY = posY;
    }

    public void setPosZ (double posZ)
    {
        this.posZ = posZ;
    }

    @Override
    public String toString ()
    {
        return "Vertex{" +
                "x=" + posX +
                ", y=" + posY +
                ", z=" + posZ +
                '}';
    }

    @Override
    public int compareTo (Vertex refVertex)
    {
        //Compare the two vertices based on their distance from the origin
        double distance1 = Math.sqrt(posX * posX + posY * posY + posZ * posZ);
        double distance2 = Math.sqrt(refVertex.posX * refVertex.posX + refVertex.posY * refVertex.posY + refVertex.posZ * refVertex.posZ);
        return Double.compare(distance1, distance2);
    }

    @Override
    public boolean equals (Object refVertex)
    {
        if (this == refVertex)
        {
            return true;
        }
        if (refVertex == null || getClass() != refVertex.getClass())
        {
            return false;
        }
        Vertex vertex = (Vertex) refVertex;
        return Double.compare(vertex.posX, posX) == 0 &&
                Double.compare(vertex.posY, posY) == 0 &&
                Double.compare(vertex.posZ, posZ) == 0;
    }
}
