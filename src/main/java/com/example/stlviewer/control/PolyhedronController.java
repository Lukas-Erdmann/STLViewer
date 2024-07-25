package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Controller class for the Polyhedron object. This class is responsible for calculating the volume,
 * surface area, bounding box and center of the polyhedron.
 */
public class PolyhedronController implements Runnable
{

    /**
     * The Polyhedron object to be controlled.
     */
    private Polyhedron polyhedron;
    /**
     * The counter for the triangle IDs.
     */
    private int idCounter = 0;
    /**
     * The adjacency list for the polyhedron.
     */
    private ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();

    /**
     * The blocking queue for the triangles.
     */
    private BlockingQueue<Triangle> blockingQueue = new LinkedBlockingQueue<>();
    /**
     * The flag to indicate that the reading is finished.
     */
    private volatile boolean isReadingFinished = false;
    /**
     * The executor service that handles adding triangles to the polyhedron.
     */
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Constructor for the PolyhedronController class.
     *
     * @param polyhedron - The Polyhedron object to be controlled.
     */
    public PolyhedronController (Polyhedron polyhedron)
    {
        this.polyhedron = polyhedron;
        isReadingFinished = true;
    }

    /**
     * Constructor for the PolyhedronController class.
     */
    public PolyhedronController ()
    {
        this.polyhedron = new Polyhedron();
    }

    /**
     * Run method for the PolyhedronController class. This method is responsible for adding all the triangles
     * to the polyhedron and calculating the volume, surface area, bounding box and center of the polyhedron.
     * It runs in a loop until the reading is finished and the blocking queue is empty.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The polyhedron is complete and all the attributes are calculated.
     */
    @Override
    public void run ()
    {
        while (true)
        {
            try
            {
                if (isReadingFinished && blockingQueue.isEmpty())
                {
                    // When the polyhedron is complete, calculate the center
                    defineCenter();
                    executorService.shutdown();
                    break;
                } else if (!blockingQueue.isEmpty())
                {
                    Triangle triangle = blockingQueue.take();
                    executorService.submit(() -> processTriangle(triangle));
                } else
                {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processTriangle(Triangle triangle) {
        // Add the triangle to the polyhedron
        addTriangle(triangle);
        // Calculate the adjacency list
        //calculateAdjacencyList(triangle);
        // Add the volume of the tetrahedron to the polyhedron
        calculateVolumeOfTetrahedron(triangle, new Vertex(0, 0, 0));
        // Add the surface area of the triangle to the polyhedron
        polyhedron.addSurfaceArea(triangle.getArea());
        // Expand the bounding box to include the triangle
        expandBoundingBox(triangle);
    }

    /**
     * Set the readingFinished flag to true.
     *
     * @param readingFinished - The flag to indicate that the reading is finished.
     */
    public void setReadingFinished (boolean readingFinished)
    {
        System.out.println(Strings.SOUT_READING_FINISHED);
        this.isReadingFinished = readingFinished;
    }

    /**
     * Calculate the volume of the polyhedron. This is done by summing the volumes of all the
     * tetrahedra formed with an arbitrary point within the polyhedron and each triangle in
     * the polyhedron.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The volume of the polyhedron is calculated.
     */
    public void calculateVolume ()
    {
        double volume = 0;
        // The arbitrary point is chosen to be the first vertex of the first triangle
        Vertex refPoint = polyhedron.getTriangles().getFirst().getVertices().getFirst();
        // For each triangle in the polyhedron, calculate the volume of the tetrahedron formed
        for (Triangle triangle : polyhedron.getTriangles())
        {
            volume += calculateVolumeOfTetrahedron(triangle, refPoint);
        }
        polyhedron.setVolume(volume);
    }

    /**
     * Calculate the volume of the tetrahedron formed by the triangle and a reference vertex.
     * The volume is calculated using the scalar triple product.
     * Pre-condition: The triangle and the reference vertex are not null.
     * Post-condition: The volume of the tetrahedron is calculated.
     *
     * @param triangle      - The triangle to form the tetrahedron.
     * @param arbitraryPoint - The reference vertex to form the tetrahedron.
     * @return - The volume of the tetrahedron.
     */
    private double calculateVolumeOfTetrahedron(Triangle triangle, Vertex arbitraryPoint) {
        double volume = triangle.calculateVolumeWithReferenceVertex(arbitraryPoint);
        if (triangle.pointsAwayFromReferenceVertex(arbitraryPoint))
        {
            // If the triangle points away from the reference vertex, add the volume
            volume = -volume;
        }
        return volume;
    }

    /**
     * Calculate the surface area of the polyhedron. This is done by summing the areas of all the
     * triangleMesh in the polyhedron.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The surface area of the polyhedron is calculated.
     */
    public void calculateSurfaceArea ()
    {
        for (Triangle triangle : polyhedron.getTriangles())
        {
            polyhedron.setSurfaceArea(triangle.getArea() + polyhedron.getSurfaceArea());
        }
    }

    /**
     * Calculate the weight of the polyhedron. This is done by multiplying the density of the material
     * by the volume of the polyhedron.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The weight of the polyhedron is calculated.
     *
     * @param density - The density of the material.
     */
    public double calculateWeight (double density)
    {
        polyhedron.setWeight(density * polyhedron.getVolume());
        return polyhedron.getWeight();
    }

    /**
     * Calculates the bounding box of the polyhedron. This is done by finding the minimum and maximum
     * x, y, and z values of the vertices of the polyhedron. If the bounding box has already been defined,
     * it is returned without recalculating it.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The bounding box of the polyhedron is defined.
     */
    public void defineBoundingBox ()
    {
        // For each triangle in the polyhedron, check if the vertices are within the bounding box
        // If they are not, update the bounding box
        for (Triangle triangle : polyhedron.getTriangles())
        {
            expandBoundingBox(triangle);
        }
    }

    /**
     * Expands the bounding box of the polyhedron to include the vertices of the triangle.
     * Pre-condition: The triangle is not null.
     * Post-condition: The bounding box of the polyhedron is expanded.
     *
     * @param triangle - The triangle to expand the bounding box.
     */
    private void expandBoundingBox(Triangle triangle) {
        // If the bounding box has not been initialized, initialize it
        if (polyhedron.getBoundingBox() == null) {
            polyhedron.setBoundingBox(new double[6]);
            // Initialize the bounding box with the maximum possible values
            for (int i = 0; i < 3; i++) {
                polyhedron.getBoundingBox()[i] = Double.MAX_VALUE;
            }
            for (int i = 3; i < 6; i++) {
                polyhedron.getBoundingBox()[i] = Double.MIN_VALUE;
            }
        }

        for (Vertex vertex : triangle.getVertices())
        {
            if (vertex.getPosX() < polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_X_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_X_INDEX] = vertex.getPosX();
            }
            if (vertex.getPosY() < polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Y_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Y_INDEX] = vertex.getPosY();
            }
            if (vertex.getPosZ() < polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Z_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Z_INDEX] = vertex.getPosZ();
            }
            if (vertex.getPosX() > polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_X_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_X_INDEX] = vertex.getPosX();
            }
            if (vertex.getPosY() > polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Y_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Y_INDEX] = vertex.getPosY();
            }
            if (vertex.getPosZ() > polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Z_INDEX])
            {
                polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Z_INDEX] = vertex.getPosZ();
            }
        }
    }

    /**
     * Defines the center of the polyhedron. This is done by calculating the center of the bounding box.
     * If the center has already been defined, it is returned without recalculating it.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The center of the polyhedron is defined.
     */
    public void defineCenter ()
    {
        if (polyhedron.getCenter() == null)
        {
            // Calculate the center of the bounding box
            double centerX = (polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_X_INDEX] +
                    polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_X_INDEX]) / Constants.NUMBER_TWO;
            double centerY = (polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Y_INDEX] +
                    polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Y_INDEX]) / Constants.NUMBER_TWO;
            double centerZ = (polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MIN_Z_INDEX] +
                    polyhedron.getBoundingBox()[Constants.BOUNDING_BOX_MAX_Z_INDEX]) / Constants.NUMBER_TWO;
            polyhedron.setCenter(new Vertex(centerX, centerY, centerZ));
        } else
        {
            // If the center has already been defined, return it
        }
    }

    /**
     * Calls the methods to calculate the volume, surface area, bounding box and center of the polyhedron.
     * This method is used if the triangles were added to the polyhedron without using the blocking queue.
     */
    public void calculatePolyhedronProperties() {
        calculateVolume();
        calculateSurfaceArea();
        defineBoundingBox();
        defineCenter();
    }

    /**
     * Add a triangle to the polyhedron.
     * Pre-condition: The polyhedron is not null.
     * Post-condition: The triangle is added to the polyhedron.
     *
     * @param triangle - The triangle to be added to the polyhedron.
     */
    public void addTriangle (Triangle triangle)
    {
        polyhedron.getTriangles().add(triangle);
    }

    /**
     * Add a triangle to the blocking queue.
     * Pre-condition: The blocking queue is not null.
     * Post-condition: The triangle is added to the blocking queue.
     *
     * @param triangle - The triangle to be added to the blocking queue.
     */
    public void addTriangleToQueue (Triangle triangle)
    {
        blockingQueue.add(triangle);
    }

    /**
     * Getter for the polyhedron.
     *
     * @return - The polyhedron.
     */
    public Polyhedron getPolyhedron ()
    {
        return polyhedron;
    }

    /**
     * Reinstantiate the polyhedron. Used to clear the polyhedron.
     * <p>
     * Post-condition: The polyhedron is reinstantiated.
     */
    public void clearPolyhedron ()
    {
        polyhedron = new Polyhedron();
    }
}
