package com.example.stlviewer.control;

import com.example.stlviewer.model.*;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.view.STLViewer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class STLViewerController
{
    /**
     * The masterController instance to manage the application.
     */
    private final masterController masterController;
    /**
     * The polyhedronController instance to manage the polyhedron data.
     */
    private PolyhedronController polyhedronController;
    /**
     * The STLViewer instance to display the STL model.
     */
    private final STLViewer stlViewer;
    /**
     * The P2PController instance to manage the peer-to-peer connection.
     */
    private P2PController p2pController;
    /**
     * The Rotate instance for the X axis.
     */
    private final Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    /**
     * The Rotate instance for the Y axis.
     */
    private final Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    /**
     * The Group instance to store the objects to rotate.
     */
    private final Group rotationGroup = new Group();
    /**
     * The boolean to check if the object is upside down.
     */
    private boolean objectIsUpsideDown = false;
    /**
     * The Translate instance for the mesh.
     */
    private final Translate translation = new Translate();
    /**
     * The Group instance to store the objects to translate.
     */
    private final Group translationGroup = new Group();
    /**
     * The anchor position for the mouse.
     */
    private double anchorX, anchorY;
    /**
     * The 3d anchor point for camera movement.
     */
    private Point3D anchorPoint;
    /**
     * The anchor angle reference for the mesh rotation.
     */
    private double anchorAngleX, anchorAngleY;
    /**
     * The anchor translation reference for the mesh translation.
     */
    private double anchorTranslateX, anchorTranslateY;
    /**
     * The longest side of the mesh.
     */
    private double longestSideLength;
    /**
     * The file path of the STL file.
     */
    private String filePath;
    /**
     * The zoom speed limit for the camera.
     */
    private final DoubleProperty zoomLimit = new SimpleDoubleProperty(100);
    /**
     * The zoom coefficient for the camera.
     */
    private final DoubleProperty zoomCoefficient = new SimpleDoubleProperty(0.0001);
    /**
     * The initial distance between the camera and the mesh.
     */
    private double initialDistance;
    /**
     * The boolean property to check if the mesh is loaded.
     */
    private final BooleanProperty isMeshLoaded = new SimpleBooleanProperty(false);
    /**
     * The current material applied to the 3D model.
     */
    private com.example.stlviewer.model.Material currentMaterial = new com.example.stlviewer.model.Material(Strings.DEFAULT_MATERIAL, 1, Strings.DEFAULT_COLOR);
    /**
     * The callback to execute when a file is loaded.
     */
    private Consumer<Void> onFileLoadedCallback;

    /**
     * Constructs a new STLViewerController instance.
     *
     * @param masterController - The masterController instance to manage the application.
     */
    public STLViewerController (masterController masterController)
    {
        this.masterController = masterController;
        this.stlViewer = new STLViewer(this);
        // Create the materials for the mesh
        createMaterials();
        // Set up listeners to update labels and P2P data
        setupListeners();
    }

    /**
     * Starts the STL viewer.
     * Precondition: The stage must be valid.
     * Postcondition: The viewer is started on the stage.
     *
     * @param stage - The stage to start the viewer on.
     */
    public void startSTLViewer (Stage stage)
    {
        stlViewer.start(stage);
    }

    /**
     * Updates the title of the main stage with the specified title.
     * Precondition: The stage must be initialized.
     * Postcondition: The title of the main stage is updated with the specified title.
     *
     * @param title - The new title for the main stage.
     */
    public void updateWindowTitle(String title) {
        Platform.runLater(() -> {
            stlViewer.getStage().setTitle(Strings.WINDOW_TITLE + title);
        });
    }

    /**
     * Opens a file dialog to select an STL file. It then sends the file path to the application controller to open the file.
     * After opening the file, the final model is given back to the viewer to display.
     * Precondition: The stage must be valid and the stlViewer must run.
     * Postcondition: The file is opened and given back to the viewer to display.
     *
     * @param stage - The stage to open the file dialog on.
     */
    public void openFile (Stage stage)
    {
        File stlFile = stlViewer.openFile(stage);

        // If a file is selected, open the file and display the model
        if (stlFile != null) {
            if (isMeshLoaded.get()) {
                clearScene();
            }
            polyhedronController = new PolyhedronController();
            filePath = stlFile.getAbsolutePath();

            Task<Void> loadFileTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    masterController.openFile(filePath, polyhedronController);
                    return null;
                }
            };

            loadFileTask.setOnSucceeded(event -> {
                stlViewer.displayModel(polyhedronController.getPolyhedron());
                updateWindowTitle(stlFile.getAbsolutePath());
                // Execute the callback if it is not null
                if (onFileLoadedCallback != null) {
                    onFileLoadedCallback.accept(null);
                }
            });

            new Thread(loadFileTask).start();
        }
    }

    /**
     * Sets the consumer callback to execute when a file is loaded.
     * Precondition: The callback must be valid.
     * Postcondition: The callback is set.
     *
     * @param callback  The callback to execute when a file is loaded.
     */
    public void setOnFileLoadedCallback(Consumer<Void> callback) {
        this.onFileLoadedCallback = callback;
    }

    /**
     * Renders the model in the STL viewer. It creates a mesh from the Polyhedron object and places it in the viewer.
     * It also applies initial transformations to the mesh and sets the camera to look at the mesh. It then polls user input.
     * Precondition: The polyhedron must be initialized.
     * Postcondition: The model is rendered in the viewer.
     *
     * @param polyhedron - The Polyhedron object to render.
     */
    public void renderModel (Polyhedron polyhedron)
    {
        // Create the mesh
        TriangleMesh polyhedronMesh = createMesh(polyhedron);

        // Place the mesh in the scene and set the material
        stlViewer.getMeshView().setMesh(polyhedronMesh);
        stlViewer.getMeshView().setMaterial(new PhongMaterial(Color.RED));

        // Apply initial transformations to place the mesh in the scene
        applyInitialTransformations();

        // Mark the mesh as loaded
        isMeshLoaded.set(true);

        // If the P2P controller is running, send the file path to the connected peer
        if (p2pController != null)
        {
            sendP2PData(collectP2PData(true));
        }

        // Poll user input
        pollMouseInput();
    }

    /**
     * Clears the scene by removing all objects from the mesh view and the main group.
     * Precondition: The stlViewer and its components must be initialized.
     * Postcondition: The scene is cleared.
     */
    public void clearScene ()
    {
        // Reset the mesh view
        stlViewer.getMeshView().setMesh(null);

        // Clear all children from the groups
        rotationGroup.getChildren().clear();
        translationGroup.getChildren().clear();
        stlViewer.getThreeDGroup().getChildren().clear(); // Ensure this removes all elements, including rotationGroup and translationGroup if they were added

        // Reset transformations if necessary
        resetView();

        // Mark the mesh as not loaded
        isMeshLoaded.set(false);
    }

    /**
     * Creates a TriangleMesh from the Polyhedron object. It adds the vertices and faces of the Polyhedron to the mesh.
     * It also adds texture coordinates to the mesh. It then returns the created TriangleMesh.
     * Precondition: The polyhedron must be initialized.
     * Postcondition: A TriangleMesh is created from the Polyhedron object.
     *
     * @param polyhedron - The Polyhedron object to create the mesh from.
     * @return The created TriangleMesh.
     */
    public TriangleMesh createMesh (Polyhedron polyhedron)
    {
        // Create a new TriangleMesh
        TriangleMesh mesh = new TriangleMesh();
        ObservableFloatArray points = mesh.getPoints();
        ObservableIntegerArray faces = mesh.getFaces();
        ObservableFloatArray texCoords = mesh.getTexCoords();

        // Add the vertices to the mesh
        for (Triangle triangle : polyhedron.getTriangles())
        {
            for (Vertex vertex : triangle.getVertices())
            {
                points.addAll(
                    // Y and Z are swapped to match the coordinate system
                    (float) (polyhedron.getCenter().getPosX() - vertex.getPosX()),
                    (float) (polyhedron.getCenter().getPosZ() - vertex.getPosZ()),
                    (float) (polyhedron.getCenter().getPosY() - vertex.getPosY())
                );
                // Add texture coordinates
                texCoords.addAll(0, 0);
            }
        }

        // Add the faces to the mesh
        for (int i = 0; i < polyhedron.getTriangleCount() * 3; i += 3)
        {
            faces.addAll(
                    i + Constants.FACE_FIRST, Constants.FACE_FIRST,
                    i + Constants.FACE_SECOND, Constants.FACE_SECOND,
                    i + Constants.FACE_THIRD, Constants.FACE_THIRD
            );
        }

        return mesh;
    }

    /**
     * Applies initial transformations to the mesh to place it in the scene. It translates the mesh to the center of the scene,
     * rotates the mesh to display it correctly, and adds the mesh to the objects to transform group. It then adds the group to the main group.
     * It also updates the view property labels and places the camera to look at the mesh.
     * Precondition: The mesh view and the camera must be initialized.
     * Postcondition: The mesh is placed in the scene and the camera is set to look at the mesh.
     */
    public void applyInitialTransformations ()
    {
        // Add the rotation handlers to the rotation group/mesh view
        rotationGroup.getTransforms().addAll(rotationX, rotationY);
        rotationGroup.getChildren().add(stlViewer.getMeshView());

        // Set the camera to look at the mesh
        placeCamera();

        // Add the rotation and translation groups to the main group
        stlViewer.getThreeDGroup().getChildren().addAll(rotationGroup, translationGroup);
    }

    /**
     * Places the camera to look at the mesh. It resets the camera position and calculates the distance to the mesh based on the mesh size.
     * It then sets the camera position and properties.
     * Precondition: The camera must be initialized.
     * Postcondition: The camera is placed to look at the mesh.
     */
    public void placeCamera ()
    {
        // Reset the camera position
        stlViewer.getPerspectiveCamera().getTransforms().clear();

        // Set up the translation group
        translationGroup.getTransforms().add(translation);
        translationGroup.getChildren().add(stlViewer.getPerspectiveCamera());

        // Calculate distance to the mesh based on the mesh size
        longestSideLength = findLongestSideLength(findMeshBounds());

        // Set the initial camera position
        translation.setZ(-longestSideLength * Constants.Z_DISTANCE_FACTOR);
        // Set the initial distance
        initialDistance = getDistanceBetweenCameraAndMeshView3D();

        // Set camera properties
        stlViewer.getPerspectiveCamera().setNearClip(0.001);
        stlViewer.getPerspectiveCamera().setFarClip(10000);
    }

    /**
     * Resets the view by resetting the transformations and placing the camera to look at the mesh.
     * Precondition: The camera and mesh must be initialized.
     * Postcondition: The view is reset.
     */
    public void resetView ()
    {
        // Reset the transformations
        rotationX.setAngle(0);
        rotationY.setAngle(0);
        translation.setX(0);
        translation.setY(0);
        translation.setZ(-longestSideLength * Constants.Z_DISTANCE_FACTOR);
        zoomLimit.set(100);
        zoomCoefficient.set(0.0001);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Polls the mouse input to rotate and translate the mesh and zoom in and out.
     * Precondition: The threeDView must be initialized.
     * Postcondition: The mesh is rotated, translated, or zoomed based on the mouse input.
     */
    public void pollMouseInput ()
    {
        stlViewer.getThreeDView().setOnMousePressed(this::onMousePressed);
        stlViewer.getThreeDView().setOnMouseDragged(this::onMouseDragged);
        stlViewer.getThreeDView().setOnScroll(this::zoom);
    }

    /**
     * Handles the mouse pressed event by storing the initial mouse position. Depending on the mouse button pressed,
     * it stores the initial rotation or the translation reference values.
     * Precondition: The mouse event must be valid.
     * Postcondition: The initial mouse position and rotation and translation values are stored.
     *
     * @param event - The mouse event.
     */
    public void onMousePressed (MouseEvent event)
    {
        // Store the initial mouse position
        anchorX = event.getSceneX();
        anchorY = event.getSceneY();
        // Invert the Y-axis for rotation, if the mesh is upside down
        objectIsUpsideDown = rotationX.getAngle() > 90 && rotationX.getAngle() < 270;
        // If the left mouse button is pressed, rotate the mesh
        if (event.isPrimaryButtonDown())
        {
            anchorAngleX = rotationX.getAngle();
            anchorAngleY = rotationY.getAngle();
        } else if (event.isSecondaryButtonDown())
        {
            // If the right mouse button is pressed, translate the mesh
            anchorTranslateX = translation.getX();
            anchorTranslateY = translation.getY();
            anchorPoint = mouseToWorldCoordinates(anchorX, anchorY,
                    stlViewer.getThreeDView().getWidth(), stlViewer.getThreeDView().getHeight());
        }
    }

    /**
     * Handles the mouse dragged event by calculating the mouse movement and rotating or translating the mesh based on the mouse movement.
     * Precondition: The mouse event must be valid.
     * Postcondition: The mesh is rotated or translated based on the mouse movement.
     *
     * @param event - The mouse event.
     */
    public void onMouseDragged (MouseEvent event)
    {
        // If the left mouse button is pressed, rotate the mesh
        if (event.isPrimaryButtonDown())
        {
            // Calculate the mouse movement
            double deltaX = event.getSceneX() - anchorX;
            double deltaY = anchorY - event.getSceneY();
            // Invert the Y-axis for rotation, if the mesh is upside down
            if (objectIsUpsideDown)
            {
                deltaX = -deltaX;
            }
            rotationX.setAngle(limitAngle(anchorAngleX - deltaY));
            rotationY.setAngle(limitAngle(anchorAngleY - deltaX));
        } else if (event.isSecondaryButtonDown()) // If the right mouse button is pressed, translate the mesh
        {
            Point3D anchorPointNow = mouseToWorldCoordinates(event.getSceneX(), event.getSceneY(),
                    stlViewer.getThreeDView().getWidth(), stlViewer.getThreeDView().getHeight());
            double deltaX = anchorPoint.getX() - anchorPointNow.getX();
            double deltaY = anchorPoint.getY() - anchorPointNow.getY();
            translation.setX(anchorTranslateX - deltaX);
            translation.setY(anchorTranslateY - deltaY);
        }
        sendP2PData(collectP2PData(false));
    }

    /**
     * Limits the possible rotation angle to 360 degrees. If the angle is less than 0, it is set to 360.
     * If the angle is greater than 360, it is set to 0.
     * Precondition: The angle must be a valid double.
     * Postcondition: The angle is 0 <= angle <= 360.
     *
     * @param angle - The angle to limit.
     * @return The limited angle.
     */
    public double limitAngle (double angle)
    {
        // Calculate how many times the angle is over 360 or under 0
        int over = Math.abs((int) (angle / 360));
        // Limit the angle to 0 <= angle <= 360
        if (angle < 0)
        {
            // If the angle is less than 0, set it to 360. The factor needs to be increased by 1.
            return 360 * (over + 1) + angle;
        } else if (angle >= 360)
        {
            // If the angle is greater than 360, set it to 0.
            return angle - 360 * over;
        }
        return angle;
    }

    /**
     * Handles the zoom event by moving the camera along the Z-axis based on the scroll direction.
     * The scroll speed is adjusted based on the distance between the camera and the mesh.
     * The scaling equation is: <i>v<sub>z</sub></i> = <i>v<sub>max</sub></i> - (<i>v<sub>max</sub></i>> * e<sup>-<i>kz</i></sup>)
     * <p>Precondition: The scroll event must be valid.
     * <p>Postcondition: The mesh is zoomed based on the scroll direction.
     *
     * @param event - The scroll event.
     */
    public void zoom (ScrollEvent event)
    {
        // Zoom the mesh based on the scroll direction
        double delta = event.getDeltaY();
        double distance = getDistanceBetweenCameraAndMeshView3D();
        double zoomSpeed = Math.max(1, zoomLimit.get() - (zoomLimit.get() * Math.exp(-zoomCoefficient.get() * distance)));

        if (delta > 0)
        {
            double newZ = translation.getZ() + zoomSpeed;
            // Assure the camera does not go through the mesh
            translation.setZ(Math.min(newZ, 0));
        } else
        {
            translation.setZ(translation.getZ() - zoomSpeed);
        }
        sendP2PData(collectP2PData(false));
    }

    /**
     * Translates the model based on the given axis and offset. Used by the TCP client to translate the model remotely.
     * Precondition: The axis must be valid and the offset must be a valid double.
     * Postcondition: The model is translated based on the axis and offset.
     *
     * @param axis   The axis to translate the model on.
     * @param offset The offset to translate the model by.
     */
    public void translateModel (String axis, double offset)
    {
        switch (axis.toLowerCase())
        {
            case Strings.AXIS_X:
                translation.setX(translation.getX() - offset);
                break;
            case Strings.AXIS_Y:
                translation.setY(translation.getY() + offset);
                break;
            case Strings.AXIS_Z:
                translation.setZ(translation.getZ() + offset);
                break;
            default:
                System.out.println(Strings.INVALID_AXIS + axis);
                break;
        }
        sendP2PData(collectP2PData(false));
    }

    public void translateModel (double offsetX, double offsetY, double offsetZ)
    {
        // Invert the X translation value to give the user the illusion of moving the model instead of the camera
        translation.setX(translation.getX() - offsetX);
        translation.setY(translation.getY() + offsetY);
        translation.setZ(translation.getZ() + offsetZ);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Rotates the model based on the given axis and degrees. Used by the TCP client to rotate the model remotely.
     * Precondition: The axis must be valid and the degrees must be a valid double.
     * Postcondition: The model is rotated based on the axis and degrees.
     *
     * @param axis    The axis to rotate the model on.
     * @param degrees The degrees to rotate the model by.
     */
    public void rotateModel (String axis, double degrees)
    {
        switch (axis.toLowerCase())
        {
            case Strings.AXIS_X:
                rotationX.setAngle(rotationX.getAngle() + degrees);
                break;
            case Strings.AXIS_Y:
                rotationY.setAngle(rotationY.getAngle() + degrees);
                break;
            default:
                System.out.println(Strings.INVALID_AXIS + axis);
                break;
        }
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the distance between the camera and the mesh view in 3D space. The position of the
     * mesh is calculated based on the bounds of the mesh view in the threeDGroup.
     * Precondition: The camera and mesh view must be initialized and not null.
     * Postcondition: The distance between the camera and the mesh view is returned.
     *
     * @return The distance between the camera and the mesh view.
     */
    public double getDistanceBetweenCameraAndMeshView3D()
    {
        // Get the center of the mesh in the threeDGroup
        Point3D meshCenterInThreeDGroup = findMeshCenter();

        // Convert perspectiveCamera's position to threeDGroup's coordinate system
        Point3D cameraPositionInThreeDGroup = new Point3D(
                translation.getX(),
                translation.getY(),
                translation.getZ()
        );
        return cameraPositionInThreeDGroup.distance(meshCenterInThreeDGroup);
    }

    /**
     * Finds the bounds of the mesh in the threeDGroup. The method gets the bounds of the mesh in the threeDGroup
     * and returns the maximum and minimum values of the mesh bounds.
     * Precondition: The mesh view must be initialized and not null.
     * Postcondition: The maximum and minimum values of the mesh bounds are returned.
     *
     * @return The maximum and minimum values of the mesh bounds.
     */
    public double[] findMeshBounds()
    {
        // Get the bounds of the mesh in the threeDGroup
        Bounds meshBoundsInThreeDGroup = rotationGroup.localToParent(stlViewer.getMeshView().getBoundsInLocal());
        // Get the maximum and minimum values of the mesh bounds
        double maxX = meshBoundsInThreeDGroup.getMaxX();
        double maxY = meshBoundsInThreeDGroup.getMaxY();
        double maxZ = meshBoundsInThreeDGroup.getMaxZ();
        double minX = meshBoundsInThreeDGroup.getMinX();
        double minY = meshBoundsInThreeDGroup.getMinY();
        double minZ = meshBoundsInThreeDGroup.getMinZ();
        return new double[]{maxX, maxY, maxZ, minX, minY, minZ};
    }

    /**
     * Finds the longest side length of the mesh. The method takes the bounds of the mesh as input and calculates
     * the longest side length of the mesh based on the bounds.
     * Precondition: The mesh view must be initialized and not null.
     * Postcondition: The longest side length of the mesh is returned.
     *
     * @return The longest side length of the mesh.
     */
    public double findLongestSideLength(double[] bounds)
    {
        double xLength = Math.abs(bounds[0] - bounds[3]);
        double yLength = Math.abs(bounds[1] - bounds[4]);
        double zLength = Math.abs(bounds[2] - bounds[5]);
        return Math.max(xLength, Math.max(yLength, zLength));
    }

    /**
     * Finds the center of the mesh in the threeDGroup. The method gets the bounds of the mesh in the threeDGroup
     * and calculates the center of the mesh based on the bounds.
     * Precondition: The mesh view must be initialized and not null.
     * Postcondition: The center of the mesh is returned.
     *
     * @return The center of the mesh.
     */
    public Point3D findMeshCenter()
    {
        // Get the bounds of the mesh in the threeDGroup
        Bounds meshBoundsInThreeDGroup = rotationGroup.localToParent(stlViewer.getMeshView().getBoundsInLocal());
        // Get the center of the mesh
        return new Point3D(
                (meshBoundsInThreeDGroup.getMinX() + meshBoundsInThreeDGroup.getMaxX()) / 2,
                (meshBoundsInThreeDGroup.getMinY() + meshBoundsInThreeDGroup.getMaxY()) / 2,
                (meshBoundsInThreeDGroup.getMinZ() + meshBoundsInThreeDGroup.getMaxZ()) / 2
        );
    }

    /**
     * Converts the mouse coordinates in the scene to world coordinates in the 3D view.
     * The method takes advantage of the known viewing angle and the camera z-position to calculate
     * the distance from the camera to the scene border along the X and Y axis. It then calculates
     * the normalized device coordinates (NDC) of the mouse position and the corresponding point
     * in world space in the z = 0 plane.
     * Precondition: The sceneX, sceneY, sceneWidth, and sceneHeight must be valid doubles.
     * Postcondition: The mouse coordinates are converted to world coordinates.
     *
     * @param sceneX      The X coordinate of the mouse in the scene.
     * @param sceneY      The Y coordinate of the mouse in the scene.
     * @param sceneWidth  The width of the scene.
     * @param sceneHeight The height of the scene.
     * @return The world coordinates of the mouse as a Point3D.
     */
    public Point3D mouseToWorldCoordinates(double sceneX, double sceneY, double sceneWidth, double sceneHeight) {
        // Get the viewing angle at the scene border
        double angleHalfFOV = Math.toRadians(stlViewer.getFieldOfView() * 0.5f);
        // Calculate the distance from (camX, camY, 0) to the scene border along the X and Y axis
        // The signs are switched because the camera moves instead of the model. Thus, all motions are inverted.
        // Normally y would increase downword and x would increase to the right.
        double distanceYHalfFOV = -Math.tan(angleHalfFOV) * translation.getZ();
        double distanceXHalfFOV = -distanceYHalfFOV * sceneWidth / sceneHeight;
        // Calculate the normalized device coordinates (NDC) of the mouse position
        double ndcX = 2 * sceneX / sceneWidth - 1;
        double ndcY = 1 - 2 * sceneY / sceneHeight;
        // Calculate the corresponding point in world space in the z = 0 plane
        return new Point3D(
                ndcX * distanceXHalfFOV,
                ndcY * distanceYHalfFOV,
                0
        );
    }

    public void setupListeners()
    {
        translation.xProperty().addListener((observable, oldValue, newValue) -> stlViewer.updateViewProperties());
        translation.yProperty().addListener((observable, oldValue, newValue) -> stlViewer.updateViewProperties());
        translation.zProperty().addListener((observable, oldValue, newValue) -> stlViewer.updateViewProperties());
        rotationX.angleProperty().addListener((observable, oldValue, newValue) -> stlViewer.updateViewProperties());
        rotationY.angleProperty().addListener((observable, oldValue, newValue) -> stlViewer.updateViewProperties());
        // TODO: Find out how to use Listeners to trigger the sendP2PData method
    }

    /**
     * Gets the file path of the STL file.
     * Precondition: None
     * Postcondition: The file path is returned.
     *
     * @return The file path of the STL file.
     */
    public String getFilePath ()
    {
        return filePath;
    }

    /**
     * Gets the rotation on the X axis.
     * Precondition: None
     * Postcondition: The rotation on the X axis is returned.
     *
     * @return The rotation on the X axis.
     */
    public Rotate getRotationX ()
    {
        return rotationX;
    }

    /**
     * Sets the rotation on the X axis.
     * Precondition: The angle must be a valid double.
     * Postcondition: The rotation on the X axis is set.
     *
     * @param angle - The angle to set.
     */
    public void setRotationX (double angle)
    {
        rotationX.setAngle(angle);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the rotation on the Y axis.
     * Precondition: None
     * Postcondition: The rotation on the Y axis is returned.
     *
     * @return The rotation on the Y axis.
     */
    public Rotate getRotationY ()
    {
        return rotationY;
    }

    /**
     * Sets the rotation on the Y axis.
     * Precondition: The angle must be a valid double.
     * Postcondition: The rotation on the Y axis is set.
     *
     * @param angle - The angle to set.
     */
    public void setRotationY (double angle)
    {
        rotationY.setAngle(angle);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the translation of the mesh.
     * Precondition: None
     * Postcondition: The translation is returned.
     *
     * @return The translation of the mesh.
     */
    public Translate getTranslation ()
    {
        return translation;
    }

    /**
     * Sets the translation of the mesh.
     * Precondition: The x, y, and z positions must be valid doubles.
     * Postcondition: The translation is set.
     *
     * @param xPos - The X position to set.
     * @param yPos - The Y position to set.
     * @param zPos - The Z position to set.
     */
    public void setTranslation(double xPos, double yPos, double zPos)
    {
        translation.setX(xPos);
        translation.setY(yPos);
        translation.setZ(zPos);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the zoom factor.
     * Precondition: None
     * Postcondition: The zoom factor is returned.
     *
     * @return The zoom factor.
     */
    public double getZoomLimit()
    {
        return zoomLimit.get();
    }

    /**
     * Sets the zoom factor.
     * Precondition: The zoom factor must be a valid double.
     * Postcondition: The zoom factor is set.
     *
     * @param zoomLimit - The zoom factor to set.
     */
    public void setZoomLimit(double zoomLimit)
    {
        this.zoomLimit.set(zoomLimit);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the zoom coefficient.
     * Precondition: None
     * Postcondition: The zoom coefficient is returned.
     *
     * @return The zoom coefficient.
     */
    public double getZoomCoefficient()
    {
        return zoomCoefficient.get();
    }

    /**
     * Sets the zoom coefficient.
     * Precondition: The zoom coefficient must be a valid double.
     * Postcondition: The zoom coefficient is set.
     *
     * @param zoomCoefficient - The zoom coefficient to set.
     */
    public void setZoomCoefficient(double zoomCoefficient)
    {
        this.zoomCoefficient.set(zoomCoefficient);
        sendP2PData(collectP2PData(false));
    }

    /**
     * Gets the isMeshLoaded property.
     * Precondition: None
     * Postcondition: The isMeshLoaded property is returned.
     *
     * @return The isMeshLoaded property.
     */
    public BooleanProperty isMeshLoadedProperty()
    {
        return isMeshLoaded;
    }

    /**
     * Sets the isMeshLoaded property.
     * Precondition: The isLoaded must be a valid boolean.
     * Postcondition: The isMeshLoaded property is set.
     *
     * @param isLoaded - The boolean to set.
     */
    public void setMeshLoaded(boolean isLoaded)
    {
        isMeshLoaded.set(isLoaded);
    }

    // --- P2P methods --- //

    /**
     * Sends the P2P data to the connected peer, if the P2P controller is running.
     * Precondition: The P2P controller must be running.
     * Postcondition: The data is sent to the connected peer.
     *
     * @param p2pPackage - The P2P package to send.
     */
    public void sendP2PData (P2PPackage p2pPackage)
    {
        if (p2pController != null)
        {
            p2pController.sendData(p2pPackage);
        }
    }

    /**
     * Collects the P2P data to send to the connected peer.
     * Precondition: None
     * Postcondition: The P2P data is collected and returned.
     *
     * @return The collected P2P data.
     */
    public P2PPackage collectP2PData (boolean sendPolyhedron)
    {
        if (sendPolyhedron)
        {
            return new P2PPackage(
                    polyhedronController.getPolyhedron(),
                    translation.getX(),
                    translation.getY(),
                    translation.getZ(),
                    rotationX.getAngle(),
                    rotationY.getAngle(),
                    zoomLimit.get(),
                    zoomCoefficient.get(),
                    currentMaterial
            );
        } else
        {
            return new P2PPackage(
                    null,
                    translation.getX(),
                    translation.getY(),
                    translation.getZ(),
                    rotationX.getAngle(),
                    rotationY.getAngle(),
                    zoomLimit.get(),
                    zoomCoefficient.get(),
                    currentMaterial
            );
        }
    }

    /**
     * Processes the P2P data by updating the model and the transformations.
     * Precondition: The data must be a P2PPackage.
     * Postcondition: The model and transformations are updated based on the data.
     *
     * @param data - The data to process.
     */
    public void processP2PData (Object data)
    {
        // If the data is a P2PPackage, process the package
        if (data instanceof P2PPackage p2pPackage)
        {
            System.out.println("[" + java.time.LocalTime.now() + "] [" + this + "] P2P package: " + p2pPackage);
            // runLater is used to update the JavaFX application thread and avoid concurrency issues
            Platform.runLater(() -> {
                // Only update the model if new
                if (!isMeshLoaded.get() && p2pPackage.getPolyhedron() != null)
                {
                    // Create a new polyhedron controller with the received polyhedron
                    polyhedronController = new PolyhedronController(p2pPackage.getPolyhedron());
                    // Clear the scene of the old model
                    clearScene();
                    // Display the model
                    stlViewer.displayModel(polyhedronController.getPolyhedron());
                }
                // Update the material separately if only it changed
                else if (!currentMaterial.equals(p2pPackage.getMaterial()))
                {
                    currentMaterial = p2pPackage.getMaterial();
                    stlViewer.updateMaterialData();
                }
                // Update translations if necessary
                if (p2pPackage.getTranslationX() != translation.getX())
                {
                    translation.setX(p2pPackage.getTranslationX());
                }
                if (p2pPackage.getTranslationY() != translation.getY())
                {
                    translation.setY(p2pPackage.getTranslationY());
                }
                if (p2pPackage.getTranslationZ() != translation.getZ())
                {
                    translation.setZ(p2pPackage.getTranslationZ());
                }

                // Update rotations if necessary
                if (p2pPackage.getRotationX() != rotationX.getAngle())
                {
                    rotationX.setAngle(p2pPackage.getRotationX());
                }
                if (p2pPackage.getRotationY() != rotationY.getAngle())
                {
                    rotationY.setAngle(p2pPackage.getRotationY());
                }

                // Update the zoom parameters if necessary
                if (p2pPackage.getZoomLimit() != zoomLimit.get())
                {
                    zoomLimit.set(p2pPackage.getZoomLimit());
                }
                if (p2pPackage.getZoomCoefficient() != zoomCoefficient.get())
                {
                    zoomCoefficient.set(p2pPackage.getZoomCoefficient());
                }
            });
        } else
        {
            System.out.println(Strings.INVALID_P2P_PACKAGE);
        }
    }

    /**
     * Setter for the P2PController instance.
     * @param p2pController - The P2PController instance to set.
     */
    public void setP2PController (P2PController p2pController)
    {
        this.p2pController = p2pController;
    }

    public void createMaterials() {
        String[][] materialData = {
                {"Steel", "7900", "#888B8D", "Steel is a strong alloy of iron and carbon.\nDensity: 7900 kg/m³"},
                {"Aluminium", "2710", "#D0D5D9", "Aluminium is a lightweight metal with good thermal and electrical conductivity.\nDensity: 2710 kg/m³"},
                {"Copper", "8960", "#B77729", "Copper is a ductile metal with high thermal and electrical conductivity.\nDensity: 8960 kg/m³"},
                {"Brass", "8600", "#AC9F3C", "Brass is an alloy of copper and zinc.\nDensity: 8600 kg/m³"},
                {"Gold", "19320", "#CBA135", "Gold is a precious metal with high thermal and electrical conductivity.\nDensity: 19320 kg/m³"},
                {"Silver", "10490", "#C0C0C0", "Silver is a precious metal with high thermal and electrical conductivity.\nDensity: 10490 kg/m³"},
                {"Chrome", "7190", "#DBE2E9", "Chrome is a shiny metal with good corrosion resistance.\nDensity: 7190 kg/m³"},
                {"Titanium", "4506", "#878681", "Titanium is a strong, lightweight metal with good corrosion resistance.\nDensity: 4506 kg/m³"},
                {"Plastic (PLA)", "1240", "#FF9913", "PLA is a biodegradable plastic made from renewable resources.\nDensity: 1240 kg/m³"}
        };
        // TODO: Implement a way to add custom materials
        // TODO: Import material data from JSON or CSV file

        ArrayList<Material> materials = new ArrayList<>();
        for (String[] data : materialData) {
            Material material = new Material(data[0], Integer.parseInt(data[1]), data[2]);
            material.setDescription(data[3]);
            materials.add(material);
        }
        stlViewer.setMaterials(materials);
    }

    public double calculateWeight (Material material)
    {
        return polyhedronController.calculateWeight(material.getDensity());
    }

    /**
     * Gets the current material applied to the 3D model.
     *
     * @return The current material applied to the 3D model.
     */
    public com.example.stlviewer.model.Material getCurrentMaterial() {
        return currentMaterial;
    }

    /**
     * Sets the current material applied to the 3D model and triggers the sendP2PData method.
     *
     * @param currentMaterial - The current material applied to the 3D model.
     */
    public void setCurrentMaterial(com.example.stlviewer.model.Material currentMaterial) {
        this.currentMaterial = currentMaterial;
        if (masterController.getUserOperationMode().equals("P2P")) {
            sendP2PData(collectP2PData(false));
        }
    }
}