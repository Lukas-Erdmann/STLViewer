package com.example.stlviewer.control;

import com.example.stlviewer.model.P2PPackage;
import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.view.STLViewer;
import javafx.application.Platform;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
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

import static com.example.stlviewer.util.Math.findMaxDouble;

public class STLViewerController
{
    public static final double ZOOM_MULTIPLIER = 0.1;
    /**
     * The masterController instance to manage the application.
     */
    private final masterController masterController;
    /**
     * The STLViewer instance to display the STL model.
     */
    private final STLViewer stlViewer;
    /**
     * The Rotate instance for the X axis.
     */
    private final Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    /**
     * The Rotate instance for the Y axis.
     */
    private final Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    /**
     * The boolean to check if the object is upside down.
     */
    private boolean objectIsUpsideDown = false;
    /**
     * The Translate instance for the mesh.
     */
    private final Translate translation = new Translate();
    /**
     * The Group instance to store the objects to transform.
     */
    private final Group objectsToTransform = new Group();
    /**
     * The P2PController instance to manage the peer-to-peer connection.
     */
    private P2PController p2pController;
    /**
     * The boolean to check if the model is new.
     */
    private boolean newModel = true;
    /**
     * The anchor position for the mouse.
     */
    private double anchorX, anchorY;
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
    private double longestSide;
    /**
     * The file path of the STL file.
     */
    private String filePath;

    /**
     * Constructs a new STLViewerController instance.
     *
     * @param masterController - The masterController instance to manage the application.
     */
    public STLViewerController (masterController masterController)
    {
        this.masterController = masterController;
        this.stlViewer = new STLViewer(this);
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
     * Setter for the P2PController instance.
     * @param p2pController
     */
    public void setP2PController (P2PController p2pController)
    {
        this.p2pController = p2pController;
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
        if (data instanceof P2PPackage)
        {
            // Cast the data to a P2PPackage
            P2PPackage p2pPackage = (P2PPackage) data;
            // runLater is used to update the JavaFX application thread and avoid concurrency issues
            Platform.runLater(() -> {
                // Only update the model if new
                if (newModel)
                {
                    // Set the new model flag to false
                    newModel = false;
                    // Display the model
                    stlViewer.displayModel(p2pPackage.getPolyhedron());
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

                // Update the zoom if necessary
                if (p2pPackage.getZoom() != stlViewer.getPerspectiveCamera().getTranslateZ())
                {
                    stlViewer.getPerspectiveCamera().setTranslateZ(p2pPackage.getZoom());
                }

                // Update the view properties
                stlViewer.updateViewProperties();
            });
        } else
        {
            System.out.println(Strings.INVALID_P2P_PACKAGE);
        }
    }

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
    public P2PPackage collectP2PData ()
    {
        return new P2PPackage(
                masterController.getPolyhedronController().getPolyhedron(),
                translation.getX(), translation.getY(), translation.getZ(),
                rotationX.getAngle(), rotationY.getAngle(),
                stlViewer.getPerspectiveCamera().getTranslateZ(),
                anchorAngleX, anchorAngleY
        );
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
        if (stlFile != null)
        {
            filePath = stlFile.getAbsolutePath();
            masterController.openFile(filePath);
            stlViewer.displayModel(masterController.getPolyhedronController().getPolyhedron());
            // If the P2P controller is running, send the file path to the connected peer
            if (p2pController != null)
            {
                sendP2PData(collectP2PData());
            }
        }
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
        // Clear the scene
        clearScene();

        // Create the mesh
        TriangleMesh polyhedronMesh = createMesh(polyhedron);

        // Place the mesh in the scene and set the material
        stlViewer.getMeshView().setMesh(polyhedronMesh);
        stlViewer.getMeshView().setMaterial(new PhongMaterial(Color.RED));

        // Apply initial transformations to place the mesh in the scene
        applyInitialTransformations();

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
        stlViewer.getMeshView().getTransforms().clear();
        stlViewer.getThreeDGroup().getChildren().removeAll();
        objectsToTransform.getChildren().removeAll();
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
                        (float) (vertex.getPosX() - polyhedron.getCenter().getPosX()),
                        (float) (vertex.getPosY() - polyhedron.getCenter().getPosY()),
                        (float) (vertex.getPosZ() - polyhedron.getCenter().getPosZ())
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
        // Place the mesh in the center of the scene
        objectsToTransform.getTransforms().addAll(
                // Rotate the mesh to display it correctly
                rotationX,
                rotationY
        );
        objectsToTransform.getChildren().add(stlViewer.getMeshView());
        stlViewer.getThreeDGroup().getChildren().addAll(objectsToTransform, stlViewer.getPerspectiveCamera());

        stlViewer.updateViewProperties();

        // Set the camera to look at the mesh
        placeCamera();
    }

    /**
     * Places the camera to look at the mesh. It resets the camera position and calculates the distance to the mesh based on the mesh size.
     * It then sets the camera position and properties.
     * Precondition: The camera must be initialized.
     * Postcondition: The camera is placed to look at the mesh.
     */
    public void placeCamera ()
    {
        // Get the scene center from subscene dimensions
        int centerX = (Constants.WINDOW_WIDTH - Constants.INFOBAR_WIDTH) / 2;
        int centerY = Constants.WINDOW_HEIGHT / 2;

        // Reset the camera position
        stlViewer.getPerspectiveCamera().getTransforms().clear();

        // Calculate distance to the mesh based on the mesh size
        // TODO: Use Bounds class to get the size of the mesh and delete the custom method
        longestSide = findMaxDouble(masterController.getPolyhedronController().getPolyhedron().getBoundingBox());

        // Set the initial camera position
        stlViewer.getPerspectiveCamera().setTranslateX(-centerX);
        stlViewer.getPerspectiveCamera().setTranslateY(-centerY);
        stlViewer.getPerspectiveCamera().setTranslateZ(-longestSide * 2);
        // Add the translation property to the camera
        stlViewer.getPerspectiveCamera().getTransforms().add(translation);

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
        translation.setZ(-longestSide * 2);
        stlViewer.updateViewProperties();
        sendP2PData(collectP2PData());
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
        // Calculate the mouse movement
        double deltaX = event.getSceneX() - anchorX;
        double deltaY = anchorY - event.getSceneY();

        // If the left mouse button is pressed, rotate the mesh
        if (event.isPrimaryButtonDown())
        {
            // Invert the Y-axis for rotation, if the mesh is upside down
            if (objectIsUpsideDown)
            {
                deltaX = -deltaX;
            }
            rotationX.setAngle(limitAngle(anchorAngleX - deltaY));
            rotationY.setAngle(limitAngle(anchorAngleY - deltaX));
        } else if (event.isSecondaryButtonDown())
        {
            translation.setX(anchorTranslateX - deltaX);
            translation.setY(anchorTranslateY + deltaY);
        }
        stlViewer.updateViewProperties();
        sendP2PData(collectP2PData());
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
     * Handles the zoom event by zooming the mesh based on the scroll direction.
     * It scales the zoom speed based on the mesh size.
     * Precondition: The scroll event must be valid.
     * Postcondition: The mesh is zoomed based on the scroll direction.
     *
     * @param event - The scroll event.
     */
    public void zoom (ScrollEvent event)
    {
        // Zoom the mesh based on the scroll direction
        double delta = event.getDeltaY();
        // Get the bounds of the mesh in the scene's coordinate system
        Bounds meshBounds = stlViewer.getMeshView().localToScene(stlViewer.getMeshView().getBoundsInLocal());
        // Calculate the center point of the mesh
        Point3D meshCenter = new Point3D(
                (meshBounds.getMinX() + meshBounds.getMaxX()) / 2,
                (meshBounds.getMinY() + meshBounds.getMaxY()) / 2,
                (meshBounds.getMinZ() + meshBounds.getMaxZ()) / 2
        );
        // Calculate the distance between the camera and the mesh's center
        Point3D cameraPosition = new Point3D(
                translation.getX(),
                translation.getY(),
                translation.getZ()
        );
        double distance = meshCenter.distance(cameraPosition);
        double zoomFactor = 0.2 + distance / 1000;
        System.out.println("Distance: " + distance + "; MeshCenter: " + meshCenter + "; CameraPosition: " + cameraPosition);
        double zoomSpeed = longestSide * zoomFactor;

        if (delta > 0)
        {
            translation.setZ(translation.getZ() + zoomSpeed);
        } else
        {
            translation.setZ(translation.getZ() - zoomSpeed);
        }

        // Apply the new translation
        stlViewer.updateViewProperties();
        sendP2PData(collectP2PData());
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
                translation.setX(translation.getX() + offset);
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
        sendP2PData(collectP2PData());
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
        sendP2PData(collectP2PData());
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
}
