package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PolyhedronController implements Runnable{

    private Polyhedron polyhedron;
    private int idCounter = 0;
    private ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();

    // BlockingQueue to hold objects for calculation
    private BlockingQueue<Triangle> blockingQueue = new LinkedBlockingQueue<>();
    private volatile boolean isReadingFinished = false;

    public PolyhedronController (Polyhedron polyhedron)
    {
        this.polyhedron = polyhedron;
    }

    public PolyhedronController() {
        this.polyhedron = new Polyhedron();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(isReadingFinished && blockingQueue.isEmpty()){
                    // When the polyhedron is complete, calculate the volume, surface area, bounding box and center
                    System.out.println("Calculating volume, surface area, bounding box and center");
                    calculateVolume();
                    calculateSurfaceArea();
                    defineBoundingBox();
                    defineCenter();
                    System.out.println("Polyhedron data: " + polyhedron.toString());
                    break;
                } else if (!blockingQueue.isEmpty()) {
                    Triangle triangle = blockingQueue.take();
                    addTriangle(triangle);
                }else{
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setReadingFinished(boolean readingFinished){
        System.out.println("Reading finished");
        this.isReadingFinished = readingFinished;
    }

    /**
     * Calculate the volume of the polyhedron. This is done by summing the volumes of all the
     * tetrahedra formed with an arbitrary point within the polyhedron and each triangle in
     * the polyhedron.
     * @return
     */
    public double calculateVolume ()
    {
        // The arbitrary point is the origin
        Vertex arbitraryPoint = polyhedron.getTriangles().get(0).getVertices().get(0);
        // For each triangle in the polyhedron, calculate the volume of the tetrahedron formed
        for (Triangle triangle : polyhedron.getTriangles()) {
            if (triangle.pointsAwayFromReferenceVertex(arbitraryPoint)) {
                // If the triangle points away from the reference vertex, add the volume
                double newVolume = polyhedron.getVolume() - triangle.calculateVolumeWithReferenceVertex(arbitraryPoint);
                polyhedron.setVolume(newVolume);
            } else {
                // If the triangle points towards the reference vertex, subtract the volume
                double newVolume = polyhedron.getVolume() + triangle.calculateVolumeWithReferenceVertex(arbitraryPoint);
                polyhedron.setVolume(newVolume);
            }
        }
        return polyhedron.getVolume();
    }

    /**
     * Calculate the surface area of the polyhedron. This is done by summing the areas of all the
     * triangleMesh in the polyhedron.
     * @return
     */
    public double calculateSurfaceArea () {
        for (Triangle triangle : polyhedron.getTriangles()) {
            polyhedron.setSurfaceArea(triangle.getArea() + polyhedron.getSurfaceArea());
        }
        return polyhedron.getSurfaceArea();
    }



    public double[] defineBoundingBox () {
        if (polyhedron.getBoundingBox() == null) {
            polyhedron.setBoundingBox(new double[6]);
            // Initialize the bounding box with the maximum possible values
            for (int i = 0; i < 6; i++) {
                polyhedron.getBoundingBox()[i] = Double.MAX_VALUE;
            }
            // For each triangle in the polyhedron, check if the vertices are within the bounding box
            // If they are not, update the bounding box
            for (Triangle triangle : polyhedron.getTriangles()) {
                for (Vertex vertex : triangle.getVertices()) {
                    if (vertex.getPosX() < polyhedron.getBoundingBox()[0]) {
                        polyhedron.getBoundingBox()[0] = vertex.getPosX();
                    }
                    if (vertex.getPosY() < polyhedron.getBoundingBox()[1]) {
                        polyhedron.getBoundingBox()[1] = vertex.getPosY();
                    }
                    if (vertex.getPosZ() < polyhedron.getBoundingBox()[2]) {
                        polyhedron.getBoundingBox()[2] = vertex.getPosZ();
                    }
                    if (vertex.getPosX() > polyhedron.getBoundingBox()[3]) {
                        polyhedron.getBoundingBox()[3] = vertex.getPosX();
                    }
                    if (vertex.getPosY() > polyhedron.getBoundingBox()[4]) {
                        polyhedron.getBoundingBox()[4] = vertex.getPosY();
                    }
                    if (vertex.getPosZ() > polyhedron.getBoundingBox()[5]) {
                        polyhedron.getBoundingBox()[5] = vertex.getPosZ();
                    }
                }
            }
            return polyhedron.getBoundingBox();
        } else {
            // If the bounding box has already been defined, return it
            return polyhedron.getBoundingBox();
        }
    }

    public Vertex defineCenter () {
        if (polyhedron.getCenter() == null) {
            // Calculate the center of the bounding box
            double centerX = (polyhedron.getBoundingBox()[0] + polyhedron.getBoundingBox()[3]) / 2;
            double centerY = (polyhedron.getBoundingBox()[1] + polyhedron.getBoundingBox()[4]) / 2;
            double centerZ = (polyhedron.getBoundingBox()[2] + polyhedron.getBoundingBox()[5]) / 2;
            polyhedron.setCenter(new Vertex(centerX, centerY, centerZ));
            return polyhedron.getCenter();
        } else {
            // If the center has already been defined, return it
            return polyhedron.getCenter();
        }
    }

    public void addTriangle (Triangle triangle)
    {
        polyhedron.getTriangles().add(triangle);
    }

    public void addTriangleToQueue (Triangle triangle) {
        blockingQueue.add(triangle);
    }

    public Polyhedron getPolyhedron ()
    {
        return polyhedron;
    }
}
