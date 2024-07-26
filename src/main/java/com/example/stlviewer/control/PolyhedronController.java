package com.example.stlviewer.control;

import com.example.stlviewer.model.Edge;
import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.stlviewer.util.RuntimeHandler.logMessage;

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
    private AtomicInteger idCounter = new AtomicInteger(Constants.NUMBER_ZERO);
    /**
     * The adjacency list for the polyhedron.
     */
    private ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();

    private ArrayList<Edge> degenerateEdges = new ArrayList<>();

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
     * Run method for the PolyhedronController thread. This method is responsible for adding all the triangles
     * to the polyhedron and calculating the volume, surface area, bounding box and center of the polyhedron.
     * It runs in a loop until the reading is finished and the blocking queue is empty. The triangles are added
     * to the polyhedron using an executor service to parallelize the process. When the blocking queue is empty, the
     * executor service is shut down and checked if all tasks are completed. The center of the polyhedron is then
     * calculated.
     * <p>Pre-condition: The polyhedron is not null.
     * <p>Post-condition: The polyhedron is complete and all the attributes are calculated.
     */
    @Override
    public void run ()
    {
        // The thread runs in a loop until the reader reports that reading the file is finished
        while (true)
        {
            try
            {
                if (isReadingFinished && blockingQueue.isEmpty())
                {
                    // When the polyhedron is complete, calculate the center
                    defineCenter();
                    // Wait for all tasks to complete
                    executorService.shutdown();
                    try {
                        if (executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                            logMessage(Strings.FINISHED_COMPILING_POLYHEDRON);
                        } else {
                            throw new InterruptedException(Strings.TIMEOUT_OCCURRED_BEFORE_POLYHEDRON_WAS_FINISHED);
                        }
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        System.out.println(Strings.THREAD_WAS_INTERRUPTED);
                    }
                    logMessage(Strings.NUMBER_TRIANGLES_READ, polyhedron.getTriangles().size());
                    break;
                } else if (!blockingQueue.isEmpty())
                {
                    Triangle triangle = blockingQueue.take();
                    executorService.submit(() -> processTriangle(triangle));
                    idCounter.incrementAndGet();
                } else
                {
                    Thread.sleep(Constants.THREAD_SLEEP_MILLIS);
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Process the triangle by adding it to the polyhedron, volume and surface area, and expanding the bounding box.
     * This method is synchronized to prevent concurrent modification of the polyhedron.
     * <p>Pre-condition: The triangle is not null and the polyhedron is not null.
     * <p>Post-condition: The triangle is added to the polyhedron and the attributes are updated.
     *
     * @param triangle - The triangle to be processed.
     */
    private void processTriangle(Triangle triangle) {
        synchronized (polyhedron) {
            // Add the triangle to the polyhedron
            addTriangle(triangle);
            triangle.setId(idCounter.get());
            // Add the volume of the tetrahedron to the polyhedron
            polyhedron.addVolume(calculateVolumeOfTetrahedron(triangle, new Vertex(Constants.NUMBER_ZERO, Constants.NUMBER_ZERO, Constants.NUMBER_ZERO)));
            // Add the surface area of the triangle to the polyhedron
            polyhedron.addSurfaceArea(triangle.getArea());
            // Expand the bounding box to include the triangle
            expandBoundingBox(triangle);
        }
    }

    /**
     * Populate the adjacency list of the polyhedron. The adjacency list is a list of lists of that represent the
     * triangles that are adjacent to a particular triangle in the polyhedron. The list stores the IDs of the triangles
     * to save memory. The adjacency list is populated by iterating over all the triangles in the polyhedron and adding
     * the adjacent triangles to the list. The adjacency list is used to fix simple holes in the polyhedron.
     * <p>Pre-condition: The polyhedron is not null and it has triangles.
     * <p>Post-condition: The adjacency list of the polyhedron is populated.
     */
    public void populateAdjacencyList() {
        // Initialize the adjacency list with the triangles
        for (int i = 0; i < polyhedron.getTriangles().size(); i++) {
            adjacencyList.add(new ArrayList<>());
            // Add all the triangles to the adjacency list
            adjacencyList.get(i).add(polyhedron.getTriangles().get(i).getId());
        }
        for (int i = 0; i < polyhedron.getTriangles().size(); i++) {
            for (int j = 0; j < polyhedron.getTriangles().size(); j++) {
                if (i != j) {
                    // If the triangle j is adjacent to triangle i, add it to the adjacency list of triangle i
                    if (polyhedron.getTriangles().get(i).isAdjacentTo(polyhedron.getTriangles().get(j))) {
                        adjacencyList.get(i).add(polyhedron.getTriangles().get(j).getId());
                    }
                }
            }
        }
    }

    /**
     * Fix simple holes in the polyhedron. This is done by finding the degenerate edges and the missing triangles.
     * The degenerate edges are found by finding the triangles that have only 2 adjacent triangles. The missing triangles
     * are found by finding three degenerate edges that form a triangle. The missing triangle is then formed by the three
     * vertices of the degenerate edges. The missing triangles are then added to the polyhedron.
     * <p>Pre-condition: The polyhedron is not null.
     * <p>Post-condition: The simple holes in the polyhedron are fixed.
     */
    public void fixSimpleHoles () {
        // TODO: Make this work for sequential reading and make it more reliable
        // Find the degenerate edges
        findDegenerateEdges();
        // Find the missing triangles
        ArrayList<Triangle> missingTriangles = findMissingTriangles();
        // Add the missing triangles to the polyhedron
        for (Triangle triangle : missingTriangles) {
            addTriangle(triangle);
        }
        logMessage(Strings.NUMBER_MISSING_TRIANGLES_ADDED, missingTriangles.size());
    }

    /**
     * Find the degenerate edges in the polyhedron. The degenerate edges are the edges that are not shared with any other
     * triangle. This is done by finding the triangles that have only 2 adjacent triangles. The degenerate edges are then
     * found by adding all the edges of the triangle to the degenerate edges list. If the edge is already in the degenerate
     * edges list, it is removed, as it is not a degenerate edge. If the edge is not in the degenerate edges list, it is added.
     * <p>Pre-condition: The polyhedron is not null.
     * <p>Post-condition: The degenerate edges of the polyhedron are found.
     */
    public void findDegenerateEdges () {
        // Iterate over all the triangles in the adjacency list
        for (int i = 0; i < adjacencyList.size(); i++) {
            // If the triangle has only 2 adjacent triangles, it is a degenerate triangle. This means that one of it's
            // edges isn't shared with any other triangle, making it a degenerate edge.
            if (adjacencyList.get(i).size() == 3) {
                logMessage("Degenerate Triangle found: " + polyhedron.getTriangleByID(adjacencyList.get(i).getFirst()).getId());
                Triangle triangle = polyhedron.getTriangleByID(adjacencyList.get(i).getFirst());
                for (int j = 0; j < Constants.TRIANGLE_VERTEX_COUNT; j++) {
                    // First all the edges of the triangle are added to the degenerate edges list
                    Edge edge = triangle.getEdges().get(j);
                    // Add the edge to the degenerate edges list if it isn't shared with any other adjacent triangle
                    boolean isShared = false;
                    for (int k = 1; k < adjacencyList.get(i).size(); k++)
                    {
                        Triangle adjacentTriangle = polyhedron.getTriangleByID(adjacencyList.get(i).get(k));
                        if (adjacentTriangle.getEdges().contains(edge))
                        {
                            isShared = true;
                            break;
                        }
                    }
                    // If the edge isn't shared with any other adjacent triangle, add it to the degenerate edges list
                    if (!isShared)
                    {
                        degenerateEdges.add(edge);
                    }
                }
            }
        }
        logMessage(Strings.NUMBER_DEGENERATE_EDGES_FOUND, degenerateEdges.size());
    }

    /**
     * Find the missing triangles in the polyhedron. The missing triangles are the triangles that are formed by three
     * degenerate edges. This is done by finding three degenerate edges that form a triangle, meaning they share three
     * vertices between them. The missing triangle is then formed by the three vertices. The missing triangles are then
     * added to the missing triangles list. The degenerate edges are then removed from the degenerate edges list.
     * <p>This method can only fix holes with three edges. Complexer holes are not fixed, as they require to also
     * build missing edges.
     * <p>Pre-condition: The polyhedron is not null.
     * <p>Post-condition: The missing triangles of the polyhedron are found.
     *
     * @return - The missing triangles of the polyhedron.
     */
    public ArrayList<Triangle> findMissingTriangles () {
        ArrayList<Triangle> missingTriangles = new ArrayList<>();
        // Find missing triangles by finding three degenerate edges that form a triangle, meaning they share three
        // vertices between them. The missing triangle is then formed by the three vertices.
        for (int i = 0; i < degenerateEdges.size(); i++) {
            for (int j = 0; j < degenerateEdges.size(); j++) {
                for (int k = 0; k < degenerateEdges.size(); k++) {
                    if (i != j && i != k && j != k) {
                        if (degenerateEdges.get(i).isAdjacentTo(degenerateEdges.get(j)) &&
                                degenerateEdges.get(i).isAdjacentTo(degenerateEdges.get(k)) &&
                                degenerateEdges.get(j).isAdjacentTo(degenerateEdges.get(k))) {
                            // The missing triangle is formed by the three vertices of the degenerate edges
                            Vertex vertex1 = degenerateEdges.get(i).getStartVertex();
                            Vertex vertex2 = degenerateEdges.get(i).getEndVertex();
                            Vertex vertex3 = degenerateEdges.get(j).getStartVertex().equals(vertex1) ||
                                    degenerateEdges.get(j).getStartVertex().equals(vertex2) ?
                                    degenerateEdges.get(j).getEndVertex() : degenerateEdges.get(j).getStartVertex();
                            // If the missing triangle is not already in the missing triangles list, add it
                            Triangle missingTriangle = new Triangle(vertex1, vertex2, vertex3);
                            if (!missingTriangles.contains(missingTriangle)) {
                                missingTriangles.add(missingTriangle);
                            }
                            // Remove the degenerate edges from the degenerate edges list
                            // Consequence: This method can only fix holes with three edges
                            // Remove list element with the highest index first to prevent index out of bounds
                            // TODO: Find a more efficient way to remove the degenerate edges
                            if (i > j && i > k) {
                                degenerateEdges.remove(i);
                                if (j > k) {
                                    degenerateEdges.remove(j);
                                    degenerateEdges.remove(k);
                                } else {
                                    degenerateEdges.remove(k);
                                    degenerateEdges.remove(j);
                                }
                            } else if (j > i && j > k) {
                                degenerateEdges.remove(j);
                                if (i > k) {
                                    degenerateEdges.remove(i);
                                    degenerateEdges.remove(k);
                                } else {
                                    degenerateEdges.remove(k);
                                    degenerateEdges.remove(i);
                                }
                            } else {
                                degenerateEdges.remove(k);
                                if (i > j) {
                                    degenerateEdges.remove(i);
                                    degenerateEdges.remove(j);
                                } else {
                                    degenerateEdges.remove(j);
                                    degenerateEdges.remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }
        return missingTriangles;
    }

    /**
     * Remove duplicate triangles from the polyhedron. This is done by creating a new list of triangles and adding
     * the triangles to the new list only if they are not already in the list. The new list is then set as the list
     * of triangles in the polyhedron. The number of duplicate triangles removed is returned.
     * <p>Pre-condition: The polyhedron is not null.
     * <p>Post-condition: The duplicate triangles are removed from the polyhedron.
     *
     * @return  The number of duplicate triangles removed.
     */
    public String removeDuplicateTriangles() {
        int duplicateCount = Constants.NUMBER_ZERO;
        ArrayList<Triangle> uniqueTriangles = new ArrayList<>();
        for (Triangle triangle : polyhedron.getTriangles()) {
            if (!uniqueTriangles.contains(triangle)) {
                uniqueTriangles.add(triangle);
            }
        }
        polyhedron.setTriangles(uniqueTriangles);
        return Strings.NUMBER_DUPLICATE_TRIANGLES_REMOVED + duplicateCount;
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
        double volume = Constants.NUMBER_ZERO;
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
        // TODO: Make the factor settable by the user
        polyhedron.setWeight(density * polyhedron.getVolume() * Constants.FACTOR_KGM3_TO_KGMM3);
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
            polyhedron.setBoundingBox(new double[Constants.BOUNDING_BOX_SIZE]);
            // Initialize the bounding box with the maximum possible values
            for (int i = 0; i < Constants.BOUNDING_BOX_SIZE / Constants.NUMBER_TWO; i++) {
                polyhedron.getBoundingBox()[i] = Double.MAX_VALUE;
            }
            for (int i = Constants.BOUNDING_BOX_SIZE / Constants.NUMBER_TWO; i < Constants.BOUNDING_BOX_SIZE; i++) {
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
        if (triangle != null)
        {
            polyhedron.getTriangles().add(triangle);
        }
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
        if (triangle != null) {
            blockingQueue.add(triangle);
        }
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

    /**
     * Gets the id counter.
     *
     * @return The id counter.
     */
    public int getIdCounter ()
    {
        return idCounter.get();
    }

    /**
     * Returns if the reading is finished.
     *
     * @return - The flag to indicate that the reading is finished.
     */
    public boolean isReadingFinished()
    {
        return isReadingFinished;
    }

    /**
     * Set the readingFinished flag to true.
     *
     * @param readingFinished - The flag to indicate that the reading is finished.
     */
    public void setReadingFinished (boolean readingFinished)
    {
        logMessage(Strings.SOUT_READING_FINISHED);
        this.isReadingFinished = readingFinished;
    }
}
