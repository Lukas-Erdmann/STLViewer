package com.example.stlviewer.control;

import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.model.Triangle;
import com.example.stlviewer.model.Vertex;
import com.example.stlviewer.res.Strings;
import com.example.stlviewer.view.STLViewer;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.File;

import static com.example.stlviewer.util.Math.findMaxDouble;

public class STLViewerController
{
    public static final int FACE_FIRST = 0;
    public static final int FACE_SECOND = 1;
    public static final int FACE_THIRD = 2;
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 900;
    public static final int INFOBAR_WIDTH = 200;
    public static final double ZOOM_MULTIPLIER = 0.05;
    private final STLViewer stlViewer;
    private final ApplicationController applicationController;
    private final Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    private final Translate translation = new Translate();
    private final Group objectsToTransform = new Group();
    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    private double anchorTranslateX, anchorTranslateY;
    private double longestSide;
    private String filePath;

    public STLViewerController(ApplicationController applicationController)
    {
        this.applicationController = applicationController;
        this.stlViewer = new STLViewer(this);
    }

    public void startSTLViewer(Stage stage)
    {
        stlViewer.start(stage);
    }

    public void openFile(Stage stage) {
        File stlFile = stlViewer.openFile(stage);

        if (stlFile != null) {
            filePath = stlFile.getAbsolutePath();
            applicationController.openFile(filePath);
            stlViewer.displayModel(applicationController.getPolyhedronController().getPolyhedron());
        }
    }

    public void renderModel (Polyhedron polyhedron) {
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

    public void clearScene() {
        // Reset the mesh view
        stlViewer.getMeshView().getTransforms().clear();
        stlViewer.getMainGroup().getChildren().clear();
        objectsToTransform.getChildren().clear();
    }

    public TriangleMesh createMesh(Polyhedron polyhedron) {
        // Create a new TriangleMesh
        TriangleMesh mesh = new TriangleMesh();
        ObservableFloatArray points = mesh.getPoints();
        ObservableIntegerArray faces = mesh.getFaces();
        ObservableFloatArray texCoords = mesh.getTexCoords();

        // Add the vertices to the mesh
        for (Triangle triangle : polyhedron.getTriangles()) {
            for (Vertex vertex : triangle.getVertices()) {
                points.addAll(
                        (float) (vertex.getPosX() - polyhedron.getCenter().getPosX()),
                        (float) (vertex.getPosY() - polyhedron.getCenter().getPosY()),
                        (float) (vertex.getPosZ()- polyhedron.getCenter().getPosZ())
                );
                // Add texture coordinates
                texCoords.addAll(0, 0);
            }
        }

        // Add the faces to the mesh
        for (int i = 0; i < polyhedron.getTriangleCount() * 3; i += 3) {
            faces.addAll(
                    i + FACE_FIRST, FACE_FIRST,
                    i + FACE_SECOND, FACE_SECOND,
                    i + FACE_THIRD, FACE_THIRD
            );
        }

        return mesh;
    }

    public void applyInitialTransformations() {
        // Get the scene center from subscene dimensions
        double centerX = (WINDOW_WIDTH - INFOBAR_WIDTH) / 2;
        double centerY = WINDOW_HEIGHT / 2;

        // Translate the mesh to the center of the scene
        objectsToTransform.getTransforms().addAll(
                new Translate(centerX, centerY, 0),
                // Rotate the mesh to display it correctly
                rotationX,
                rotationY,
                translation
        );
        objectsToTransform.getChildren().add(stlViewer.getMeshView());
        stlViewer.getMainGroup().getChildren().add(objectsToTransform);

        // Set the camera to look at the mesh
        placeCamera();
    }

    public void placeCamera() {
        // Reset the camera position
        stlViewer.getUserCamera().getTransforms().clear();

        // Calculate distance to the mesh based on the mesh size
        longestSide = findMaxDouble(applicationController.getPolyhedronController().getPolyhedron().getBoundingBox());
        System.out.println("Longest side: " + longestSide);

        // Set the camera position
        stlViewer.getUserCamera().getTransforms().addAll(
                new Translate(0, 0, -longestSide * 2)
        );

        // Set camera properties
        stlViewer.getUserCamera().setNearClip(0.1);
        stlViewer.getUserCamera().setFarClip(10000);
    }

    public void pollMouseInput() {
        stlViewer.getThreeDView().setOnMousePressed(this::onMousePressed);
        stlViewer.getThreeDView().setOnMouseDragged(event -> onMouseDragged(event));
        stlViewer.getThreeDView().setOnScroll(event -> zoom(event));
    }

    public void onMousePressed(MouseEvent event) {
        // Store the initial mouse position
        anchorX = event.getSceneX();
        anchorY = event.getSceneY();
        // If the left mouse button is pressed, rotate the mesh
        if (event.isPrimaryButtonDown()) {
            anchorAngleX = rotationX.getAngle();
            anchorAngleY = rotationY.getAngle();
        } else if (event.isSecondaryButtonDown()) {
            // If the right mouse button is pressed, translate the mesh
            anchorTranslateX = translation.getX();
            anchorTranslateY = translation.getY();
        }
    }

    public void onMouseDragged(MouseEvent event) {
        // Calculate the mouse movement
        double deltaX = event.getSceneX() - anchorX;
        double deltaY = event.getSceneY() - anchorY;
        // If the left mouse button is pressed, rotate the mesh
        if (event.isPrimaryButtonDown()) {
            rotationX.setAngle(anchorAngleX - deltaY);
            rotationY.setAngle(anchorAngleY + deltaX);
        } else if (event.isSecondaryButtonDown()) {
            // If the right mouse button is pressed, translate the mesh
            translation.setX(anchorTranslateX + deltaX);
            translation.setY(anchorTranslateY + deltaY);
        }
    }

    public void zoom(ScrollEvent event) {
        // Zoom the mesh based on the scroll direction
        double delta = event.getDeltaY();
        // Scale the zoom speed based on the mesh size and the distance to the mesh
        double zoomSpeed = longestSide * ZOOM_MULTIPLIER * stlViewer.getUserCamera().getTranslateZ();

        if (delta < 0) {
            stlViewer.getUserCamera().setTranslateZ(stlViewer.getUserCamera().getTranslateZ() + zoomSpeed);
        } else {
            stlViewer.getUserCamera().setTranslateZ(stlViewer.getUserCamera().getTranslateZ() - zoomSpeed);
        }
    }

    public void translateModel(String axis, double offset) {
        switch (axis.toLowerCase()) {
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
    }

    public void rotateModel(String axis, double degrees) {
        switch (axis.toLowerCase()) {
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
    }

    public String getFilePath() {
        return filePath;
    }
}
