package com.example.stlviewer.view;

import com.example.stlviewer.control.STLViewerController;
import com.example.stlviewer.model.Polyhedron;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class STLViewer extends Application
{
    private STLViewerController stlViewerController;

    private final String stlFilePath;
    private final float zoomFactor;

    private final MeshView meshView;
    private final Label numberOfTrianglesLabel;
    private final Label surfaceAreaLabel;
    private final Label volumeLabel;
    private final Group mainGroup;
    private final PerspectiveCamera userCamera;
    private SubScene ThreeDView;

    public STLViewer(STLViewerController stlViewerController) {
        this.stlViewerController = stlViewerController;
        this.stlFilePath = stlViewerController.getFilePath();
        this.zoomFactor = 1.0f;
        this.meshView = new MeshView();
        this.numberOfTrianglesLabel = new Label();
        this.surfaceAreaLabel = new Label();
        this.volumeLabel = new Label();
        this.mainGroup = new Group();
        this.userCamera = new PerspectiveCamera(false);
    }

    @Override
    public void start (Stage stage)
    {
        configureStage(stage);
        stage.show();
    }

    private void configureStage(Stage stage)
    {
        stage.setTitle("STL Viewer | " + stlFilePath);

        BorderPane borderPane = new BorderPane();
        // Setup the window size and color
        Scene scene = new Scene(borderPane, 1600, 900, true);
        scene.setFill(javafx.scene.paint.Color.GREY);
        stage.setResizable(true);

        // Add the menu bar, info labels, and the 3D view
        borderPane.setTop(configureMenuBar(stage));
        borderPane.setRight(configureInfoLabels());
        borderPane.setCenter(configure3DView());

        // Set the scene
        stage.setScene(scene);
    }

    private MenuBar configureMenuBar(Stage stage)
    {
        MenuBar topMenuBar = new MenuBar();
        // Add menu items
        topMenuBar.getMenus().addAll(configureMenuFile(stage), configureMenuEdit(), configureMenuView());
        return topMenuBar;
    }

    private Menu configureMenuFile(Stage stage)
    {
        Menu menuFile = new Menu("File");
        // Add menu items
        MenuItem menuItemOpen = new MenuItem("Open File...");
        menuItemOpen.setOnAction(e -> stlViewerController.openFile(stage));
        MenuItem menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction(e -> System.exit(0));
        menuFile.getItems().addAll(menuItemOpen, menuItemExit);
        return menuFile;
    }

    public File openFile (Stage stage)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open STL File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        return fileChooser.showOpenDialog(stage);
    }

    private Menu configureMenuEdit()
    {
        Menu menuEdit = new Menu("Edit");
        // Add menu items
        MenuItem menuItemSetColor = new MenuItem("Set Color...");
        menuItemSetColor.setOnAction(e -> openColorDialog());
        menuEdit.getItems().add(menuItemSetColor);
        return menuEdit;
    }

    private void openColorDialog()
    {
        Dialog<ButtonType> colorDialog = new Dialog<>();
        colorDialog.setTitle("Set Colors");

        // Create the VBox for the dialog
        VBox dialogVBox = configureColorDialogVBox();
        colorDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        colorDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        colorDialog.showAndWait();
    }

    private VBox configureColorDialogVBox()
    {
        VBox dialogVBox = new VBox();
        // Add the color picker for the 3D model
        ColorPicker modelColorPicker = configureModelColorPicker();
        // Add the color picker for the background
        ColorPicker backgroundColorPicker = configureBackgroundColorPicker();

        // Add the color pickers with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label("3D Model Color:"), modelColorPicker,
                new Label("Background Color:"), backgroundColorPicker
        );

        return dialogVBox;
    }

    private ColorPicker configureModelColorPicker()
    {
        ColorPicker modelColorPicker = new ColorPicker(((PhongMaterial) meshView.getMaterial()).getDiffuseColor());
        modelColorPicker.setOnAction(e -> {
            Material material = new PhongMaterial(modelColorPicker.getValue());
            meshView.setMaterial(material);
        });
        return modelColorPicker;
    }

    private ColorPicker configureBackgroundColorPicker()
    {
        // Create a color picker with the current background color
        ColorPicker backgroundColorPicker = new ColorPicker((javafx.scene.paint.Color) ThreeDView.getFill());
        // Set the new background color when the user selects a new color
        backgroundColorPicker.setOnAction(e -> ThreeDView.setFill(backgroundColorPicker.getValue()));
        return backgroundColorPicker;
    }

    private Menu configureMenuView()
    {
        Menu menuView = new Menu("View");
        // Add menu items
        MenuItem menuItemTranslate = new MenuItem("Translate...");
        menuItemTranslate.setOnAction(e -> openTranslateDialog());
        MenuItem menuItemRotate = new MenuItem("Rotate...");
        menuItemRotate.setOnAction(e -> openRotateDialog());
        MenuItem menuItemSetZoom = new MenuItem("Set Zoom...");
        menuItemSetZoom.setOnAction(e -> openZoomDialog());
        MenuItem menuItemResetZoom = new MenuItem("Reset View");
        menuItemResetZoom.setOnAction(e -> userCamera.getTransforms().clear());
        menuView.getItems().addAll(menuItemTranslate, menuItemRotate, menuItemSetZoom, menuItemResetZoom);
        return menuView;
    }

    private void openTranslateDialog()
    {
        Dialog<ButtonType> translateDialog = new Dialog<>();
        translateDialog.setTitle("Translate Model");

        // Create the VBox for the dialog
        VBox dialogVBox = configureTranslateDialogVBox();
        translateDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        translateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        translateDialog.showAndWait();
    }

    private VBox configureTranslateDialogVBox()
    {
        VBox dialogVBox = new VBox();
        // Add the text fields for the translation values
        TextField translateX = new TextField();
        translateX.setPromptText("X");
        TextField translateY = new TextField();
        translateY.setPromptText("Y");
        TextField translateZ = new TextField();
        translateZ.setPromptText("Z");

        // Add the text fields with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label("X:"), translateX,
                new Label("Y:"), translateY,
                new Label("Z:"), translateZ
        );

        return dialogVBox;
    }

    private void openRotateDialog()
    {
        Dialog<ButtonType> rotateDialog = new Dialog<>();
        rotateDialog.setTitle("Rotate Model");

        // Create the VBox for the dialog
        VBox dialogVBox = configureRotateDialogVBox();
        rotateDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        rotateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        rotateDialog.showAndWait();
    }

    private VBox configureRotateDialogVBox()
    {
        VBox dialogVBox = new VBox();
        // Add the text fields for the rotation values
        TextField rotateX = new TextField();
        rotateX.setPromptText("X");
        TextField rotateY = new TextField();
        rotateY.setPromptText("Y");
        TextField rotateZ = new TextField();
        rotateZ.setPromptText("Z");

        // Add the text fields with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label("X:"), rotateX,
                new Label("Y:"), rotateY,
                new Label("Z:"), rotateZ
        );

        return dialogVBox;
    }

    private void openZoomDialog()
    {
        Dialog<ButtonType> zoomDialog = new Dialog<>();
        zoomDialog.setTitle("Set Zoom");

        // Create the VBox for the dialog
        VBox dialogVBox = configureZoomDialogVBox();
        zoomDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        zoomDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        zoomDialog.showAndWait();
    }

    private VBox configureZoomDialogVBox()
    {
        VBox dialogVBox = new VBox();
        // Add the text field for the zoom value
        TextField zoomValue = new TextField();
        zoomValue.setPromptText("Zoom Factor");

        // Add the text field with label to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label("Zoom Factor:"), zoomValue
        );

        return dialogVBox;
    }

    private VBox configureInfoLabels()
    {
        VBox infoLabels = new VBox();
        // Set dimensions and padding for the VBox
        infoLabels.setPrefWidth(200);
        infoLabels.setPadding(new javafx.geometry.Insets(10));
        infoLabels.setSpacing(10);

        // Add the labels for the number of triangles, surface area, and volume
        infoLabels.getChildren().addAll(
                makeLabelArial("Model Information", FontWeight.BOLD, 16),
                makeLabelArial("Number of Triangles: ", FontWeight.NORMAL, 14), numberOfTrianglesLabel,
                makeLabelArial("Surface Area: ", FontWeight.NORMAL, 14), surfaceAreaLabel,
                makeLabelArial("Volume: ", FontWeight.NORMAL, 14), volumeLabel
        );
        return infoLabels;
    }

    private Label makeLabelArial(String labelText, FontWeight fontWeight, int fontSize)
    {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", fontWeight, fontSize));
        return label;
    }

    private SubScene configure3DView()
    {
        ThreeDView = new SubScene(mainGroup, 1400, 900, true, javafx.scene.SceneAntialiasing.BALANCED);
        ThreeDView.setFill(javafx.scene.paint.Color.LIGHTGREY);
        ThreeDView.setCamera(userCamera);

        return ThreeDView;
    }

    public void displayModel(Polyhedron polyhedron)
    {
        System.out.println("Displaying model...");
        // Set the number of triangles, surface area, and volume labels
        numberOfTrianglesLabel.setText(String.valueOf(polyhedron.getTriangleCount()));
        surfaceAreaLabel.setText(String.format("%.2f", polyhedron.getSurfaceArea()));
        volumeLabel.setText(String.format("%.2f", polyhedron.getVolume()));

        // Render the 3D model
        stlViewerController.renderModel(polyhedron);
    }

    public MeshView getMeshView() {
        return meshView;
    }

    public Camera getUserCamera() {
        return userCamera;
    }

    public SubScene getThreeDView() {
        return ThreeDView;
    }

    public Group getMainGroup() {
        return mainGroup;
    }
}
