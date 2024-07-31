package com.example.stlviewer.view;

import com.example.stlviewer.control.GUIController;
import com.example.stlviewer.model.Polyhedron;
import com.example.stlviewer.res.Constants;
import com.example.stlviewer.res.Strings;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.ArrayList;

import static com.example.stlviewer.util.MathUtil.roundToTwoNonZeroDigits;

/**
 * The STLViewer class provides a JavaFX-based interface for viewing and interacting with 3D models in STL format.
 * It allows the user to open an STL file, view the model in 3D, and set various properties such as color, rotation,
 * and translation. The class also displays information about the model, such as the number of triangles, surface area,
 * and volume.
 */
public class STLViewer extends Application
{
    /**
     * The main stage for the application.
     */
    private Stage stage = new Stage();
    /**
     * The MeshView object for displaying the 3D model.
     */
    private final MeshView meshView = new MeshView();
    /**
     * The Label displaying the number of triangles in the model.
     */
    private final Label numberOfTrianglesLabel = new Label();
    /**
     * The Label displaying the surface area of the model.
     */
    private final Label surfaceAreaLabel = new Label();
    /**
     * The Label displaying the volume of the model.
     */
    private final Label volumeLabel = new Label();
    /**
     * The Label displaying the weight of the model.
     */
    private final Label weightLabel = new Label();
    /**
     * The Label displaying the material of the model.
     */
    private final Label materialLabel = new Label();
    /**
     * The Label displaying the density of the model.
     */
    private final Label densityLabel = new Label();
    /**
     * The Label displaying the material description of the model.
     */
    private final Label materialDescriptionLabel = new Label();
    /**
     * The Label displaying the rotation properties of the model.
     */
    private final Label rotationLabel = new Label();
    /**
     * The Label displaying the translation properties of the model.
     */
    private final Label translationLabel = new Label();
    /**
     * The Group object for the main 3D scene.
     */
    private final Group threeDGroup = new Group();
    /**
     * The PerspectiveCamera object for the 3D view.
     */
    private final PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
    /**
     * The controller for the STL viewer application.
     */
    private GUIController GUIController;
    /**
     * The SubScene object for the 3D view.
     */
    private SubScene threeDView;
    /**
     * The list of materials available for the 3D model.
     */
    private ArrayList<com.example.stlviewer.model.Material> materials = new ArrayList<>();

    /**
     * Constructs an STLViewer with the specified controller.
     * Precondition: The controller should be initialized.
     * Postcondition: An STLViewer instance is created with the given controller.
     *
     * @param GUIController - The controller for the STL viewer application.
     */
    public STLViewer (GUIController GUIController)
    {
        this.GUIController = GUIController;
    }

    /**
     * Starts the JavaFX application and configures the main stage.
     * Precondition: Stage must be initialized.
     * Postcondition: The main stage is configured and displayed.
     *
     * @param stage - The primary stage for the application.
     */
    @Override
    public void start (Stage stage)
    {
        this.stage = stage;
        configureStage();
        stage.show();
    }

    /**
     * Configures the main stage with the specified title and layout. A menu bar is added to the top, info labels are
     * added to the right, and the 3D view is added to the center of the border pane. The scene is then set on the stage.
     * Precondition: The stage must be initialized.
     * Postcondition: The stage is configured with the specified title and layout.
     */
    private void configureStage ()
    {
        stage.setTitle(Strings.WINDOW_TITLE);

        BorderPane borderPane = new BorderPane();
        // Set up the window size and color
        Scene scene = new Scene(borderPane, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene.setFill(javafx.scene.paint.Color.GREY);

        // Add the menu bar, info labels, and the 3D view
        borderPane.setTop(configureMenuBar());
        borderPane.setRight(configureInfoLabels());
        borderPane.setCenter(configure3DView());

        // Set the scene
        stage.setScene(scene);
    }

    /**
     * Configures the menu bar with the File, Edit, and View menus. Each menu contains menu items for various actions.
     * Precondition: The stage must be initialized.
     * Postcondition: The menu bar is configured with the specified menus and menu items.
     *
     * @return The configured menu bar.
     */
    private MenuBar configureMenuBar ()
    {
        MenuBar topMenuBar = new MenuBar();
        // Add menu items
        topMenuBar.getMenus().addAll(configureMenuFile(), configureMenuEdit(), configureMenuView());
        return topMenuBar;
    }

    /**
     * Configures the File menu with menu items for opening a file and exiting the application.
     * Precondition: The stage must be initialized.
     * Postcondition: The File menu is configured with the specified menu items.
     *
     * @return The configured File menu.
     */
    private Menu configureMenuFile ()
    {
        Menu menuFile = new Menu(Strings.STLV_MENU);
        // Add menu items
        MenuItem menuItemOpen = new MenuItem(Strings.STLV_OPEN_FILE);
        menuItemOpen.setOnAction(e -> GUIController.openFile(stage));
        MenuItem menuItemExit = new MenuItem(Strings.STLV_EXIT);
        menuItemExit.setOnAction(e -> System.exit(0));
        menuFile.getItems().addAll(menuItemOpen, menuItemExit);
        return menuFile;
    }

    /**
     * Opens a file chooser dialog to select an STL file for opening. A file filter is applied to only show STL files.
     * Precondition: The stage must be initialized.
     * Postcondition: The file chooser dialog is displayed and the selected file is returned.
     *
     * @param stage - The primary stage for the application.
     * @return The selected file.
     */
    public File openFile (Stage stage)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Strings.STLV_OPEN_STL_FILE);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Strings.STLV_STL_FILES, Strings.STL_FILE_SUFFIX_ALL));
        return fileChooser.showOpenDialog(stage);
    }

    public void displayFileErrorDialog ()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Strings.STLV_ERROR);
        alert.setHeaderText(Strings.STLV_FILE_ERROR);
        alert.setContentText(Strings.STLV_FILE_ERROR_MESSAGE);
        alert.showAndWait();
    }

    /**
     * Configures the Edit menu with menu items for setting the color of the 3D model.
     * Precondition: None.
     * Postcondition: The Edit menu is configured with the specified menu items.
     *
     * @return The configured Edit menu.
     */
    private Menu configureMenuEdit ()
    {
        Menu menuEdit = new Menu(Strings.STLV_EDIT);
        // Add menu items
        // Color menu
        MenuItem menuItemSetColor = new MenuItem(Strings.STLV_SET_COLOR);
        menuItemSetColor.disableProperty().bind(meshView.materialProperty().isNull());
        menuItemSetColor.setOnAction(_ -> openColorDialog());
        // Material menu
        MenuItem menuItemSetMaterial = new MenuItem(Strings.STLV_SET_MATERIAL);
        menuItemSetMaterial.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemSetMaterial.setOnAction(_ -> openMaterialDialog());
        // Unit system menu
        MenuItem menuItemSetUnitSystem = new MenuItem(Strings.STLV_SET_UNITS);
        menuItemSetUnitSystem.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemSetUnitSystem.setOnAction(_ -> openUnitsDialog());

        menuEdit.getItems().addAll(menuItemSetColor, menuItemSetMaterial, menuItemSetUnitSystem);
        return menuEdit;
    }

    /**
     * Opens a color dialog to allow the user to set the color of the 3D model and the background.
     * Precondition: None.
     * Postcondition: The color dialog is displayed and the selected colors are applied to the model and background.
     */
    private void openColorDialog ()
    {
        Dialog<ButtonType> colorDialog = new Dialog<>();
        colorDialog.setTitle(Strings.STLV_SET_COLORS);

        // Create the VBox for the dialog
        VBox dialogVBox = configureColorDialogVBox();
        colorDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        colorDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        colorDialog.showAndWait();
    }

    /**
     * Configures the VBox for the color dialog with color pickers for the 3D model and background.
     * Precondition: The supermenu item was selected.
     * Postcondition: The VBox is configured with the color pickers for the 3D model and background.
     *
     * @return The configured VBox for the color dialog.
     */
    private VBox configureColorDialogVBox ()
    {
        VBox dialogVBox = new VBox();
        // Add the color picker for the 3D model
        ColorPicker modelColorPicker = configureModelColorPicker();
        // Add the color picker for the background
        ColorPicker backgroundColorPicker = configureBackgroundColorPicker();

        // Add the color pickers with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_3D_MODEL_COLOR), modelColorPicker,
                new Label(Strings.STLV_BACKGROUNG_COLOR), backgroundColorPicker
        );

        return dialogVBox;
    }

    /**
     * Opens a dialog to allow the user to set the unit system and scaling factor for the 3D model.
     * Precondition: The supermenu item was selected and the materials list is filled.
     * Postcondition: The unit system dialog is displayed and the new unit system and scaling factor are applied to the model.
     */
    public void openMaterialDialog() {
        Dialog<ButtonType> materialDialog = new Dialog<>();
        materialDialog.setTitle(Strings.STLV_SET_MATERIAL);

        // Create the VBox for the dialog
        VBox dialogVBox = new VBox();
        // Add the dropdown for the material
        ComboBox<String> materialComboBox = new ComboBox<>();
        for (com.example.stlviewer.model.Material material : materials) {
            materialComboBox.getItems().add(material.getName());
        }
        materialComboBox.setValue(GUIController.getCurrentMaterial().getName()); // Default material is the first one
        // The description of the selected material is displayed beneath the dropdown menu
        Label materialDescription = new Label(GUIController.getCurrentMaterial().getDescription());

        // Add the dropdown with label and description to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_MATERIAL), materialComboBox, materialDescription
        );

        // Add the OK and Cancel buttons
        materialDialog.getDialogPane().setContent(dialogVBox);
        materialDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Update the description label when a new material is selected
        materialComboBox.setOnAction(e -> {
            for (com.example.stlviewer.model.Material material : materials) {
                if (material.getName().equals(materialComboBox.getValue())) {
                    materialDescription.setText(material.getDescription());
                    break;
                }
            }
        });

        // Handle the OK button click
        materialDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String selectedMaterial = materialComboBox.getValue();
                for (com.example.stlviewer.model.Material material : materials) {
                    // Get material with the selected name
                    if (material.getName().equals(selectedMaterial)) {
                        // Apply the new material to the model
                        GUIController.setCurrentMaterial(material);
                        // Update the labels and recalculate the weight
                        updateMaterialData();
                        break;
                    }
                }
            }
            return null;
        });

        // Show the dialog and wait for the user response
        materialDialog.showAndWait();
    }

    /**
     * Configures the color picker for the 3D model with the current color.
     * Precondition: The mesh view must be initialized and have a material with a diffuse color.
     * Postcondition: The color picker is configured with the current color and an action listener to set the new color.
     *
     * @return The configured color picker for the 3D model.
     */
    private ColorPicker configureModelColorPicker ()
    {
        ColorPicker modelColorPicker = new ColorPicker(((PhongMaterial) meshView.getMaterial()).getDiffuseColor());
        modelColorPicker.setOnAction(e -> {
            Material material = new PhongMaterial(modelColorPicker.getValue());
            meshView.setMaterial(material);
        });
        return modelColorPicker;
    }

    /**
     * Configures the color picker for the background with the current color.
     * Precondition: The 3D view must be initialized.
     * Postcondition: The color picker is configured with the current color and an action listener to set the new color.
     *
     * @return The configured color picker for the background.
     */
    private ColorPicker configureBackgroundColorPicker ()
    {
        // Create a color picker with the current background color
        ColorPicker backgroundColorPicker = new ColorPicker((javafx.scene.paint.Color) threeDView.getFill());
        // Set the new background color when the user selects a new color
        backgroundColorPicker.setOnAction(e -> threeDView.setFill(backgroundColorPicker.getValue()));
        return backgroundColorPicker;
    }

    public void openUnitsDialog ()
    {
        Dialog<ButtonType> unitSystemDialog = new Dialog<>();
        unitSystemDialog.setTitle(Strings.STLV_SET_UNITS);

        // Create the VBox for the dialog
        VBox dialogVBox = new VBox();
        // Add the dropdown for the unit of length
        ComboBox<String> unitLengthComboBox = new ComboBox<>();
        unitLengthComboBox.getItems().addAll(Strings.STLV_UNIT_M, Strings.STLV_UNIT_CM, Strings.STLV_UNIT_MM, Strings.STLV_UNIT_INCH);
        unitLengthComboBox.setValue(GUIController.getUnitLength());
        // Add the dropdown for the unit of mass
        ComboBox<String> unitMassComboBox = new ComboBox<>();
        unitMassComboBox.getItems().addAll(Strings.STLV_UNIT_KG, Strings.STLV_UNIT_G, Strings.STLV_UNIT_LB);
        unitMassComboBox.setValue(GUIController.getUnitMass());

        // Add the dropdown with label and scaling factor to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_UNIT_LENGTH), unitLengthComboBox,
                new Label(Strings.STLV_UNIT_MASS), unitMassComboBox
        );

        // Add the OK and Cancel buttons
        unitSystemDialog.getDialogPane().setContent(dialogVBox);
        unitSystemDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the OK button click
        unitSystemDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String newLengthUnit = unitLengthComboBox.getValue();
                String newMassUnit = unitMassComboBox.getValue();
                GUIController.setUnitSystem(newLengthUnit, newMassUnit);
            }
            return null;
        });

        // Show the dialog and wait for the user response
        unitSystemDialog.showAndWait();
    }

    /**
     * Configures the View menu with menu items for setting the view properties such as translation, rotation, and zoom.
     * Precondition: The controller must be initialized.
     * Postcondition: The View menu is configured with the specified menu items.
     *
     * @return The configured View menu.
     */
    private Menu configureMenuView ()
    {
        Menu menuView = new Menu(Strings.STLV_VIEW);
        // Add menu items
        MenuItem menuItemTranslate = new MenuItem(Strings.STLV_TRANSLATE);
        menuItemTranslate.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemTranslate.setOnAction(e -> openTranslateDialog());
        MenuItem menuItemRotate = new MenuItem(Strings.STLV_ROTATE);
        menuItemRotate.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemRotate.setOnAction(e -> openRotateDialog());
        MenuItem menuItemSetZoom = new MenuItem(Strings.STLV_SET_ZOOM);
        menuItemSetZoom.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemSetZoom.setOnAction(e -> openZoomDialog());
        MenuItem menuItemResetZoom = new MenuItem(Strings.STLV_RESET_VIEW);
        menuItemResetZoom.disableProperty().bind(GUIController.isMeshLoadedProperty().not());
        menuItemResetZoom.setOnAction(e -> GUIController.resetView());
        menuView.getItems().addAll(menuItemTranslate, menuItemRotate, menuItemSetZoom, menuItemResetZoom);
        return menuView;
    }

    /**
     * Opens a dialog to allow the user to apply a translation motion to the 3D model. The dialog contains text fields
     * for the X and Y translation values.
     * Precondition: The supermenu item was selected.
     * Postcondition: The translation dialog is displayed and the new translation values are applied to the model.
     */
    private void openTranslateDialog ()
    {
        Dialog<ButtonType> translateDialog = new Dialog<>();
        translateDialog.setTitle(Strings.STLV_TRANSLATE_MODEL);

        // Create the VBox for the dialog
        VBox dialogVBox = new VBox();
        // Add the text fields for the translation values
        TextField translateX = new TextField();
        translateX.setText(String.valueOf(Constants.N_ZERO));
        TextField translateY = new TextField();
        translateY.setText(String.valueOf(Constants.N_ZERO));
        TextField translateZ = new TextField();
        translateZ.setText(String.valueOf(Constants.N_ZERO));

        // Add the text fields with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_X_COLON), translateX,
                new Label(Strings.STLV_Y_COLON), translateY,
                new Label(Strings.STLV_Z_COLON), translateZ
        );

        translateDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        translateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        translateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double newTranslateX = Double.parseDouble(translateX.getText());
                    double newTranslateY = Double.parseDouble(translateY.getText());
                    double newTranslateZ = Double.parseDouble(translateZ.getText());
                    GUIController.translateModel(newTranslateX, newTranslateY, newTranslateZ);
                } catch (NumberFormatException e) {
                    // TODO: Handle invalid input
                }
            }
            return null;
        });

        // Show the dialog and wait for the user response
        translateDialog.showAndWait();
    }

    /**
     * Opens a dialog to allow the user to apply a rotation motion to the 3D model. The dialog contains text fields
     * for the X and Y rotation angles. The rotation angles are specified in degrees.
     * Precondition: The supermenu item was selected.
     * Postcondition: The rotation dialog is displayed and the new rotation angles are applied to the model.
     */
    private void openRotateDialog ()
    {
        Dialog<ButtonType> rotateDialog = new Dialog<>();
        rotateDialog.setTitle(Strings.STLV_ROTATE_MODEL);

        // Create the VBox for the dialog
        VBox dialogVBox = new VBox();
        // Add the text fields for the rotation values
        TextField rotateX = new TextField();
        rotateX.setText(String.valueOf(String.valueOf(Constants.N_ZERO)));
        TextField rotateY = new TextField();
        rotateY.setText(String.valueOf(String.valueOf(Constants.N_ZERO)));

        // Add the text fields with labels to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_X_COLON), rotateX,
                new Label(Strings.STLV_Y_COLON), rotateY
        );

        rotateDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        rotateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the OK button click
        rotateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double newRotateX = Double.parseDouble(rotateX.getText());
                    GUIController.rotateModel(Strings.AXIS_X, newRotateX);
                    double newRotateY = Double.parseDouble(rotateY.getText());
                    GUIController.rotateModel(Strings.AXIS_Y, newRotateY);
                } catch (NumberFormatException e) {
                    // TODO: Handle invalid input
                }
            }
            return null;
        });

        // Show the dialog and wait for the user response
        rotateDialog.showAndWait();
    }

    /**
     * Opens a dialog to allow the user to set the zoom parameters for the camera motion. The dialog contains text fields
     * for the zoom limit and zoom coefficient. The zoom limit is the maximum speed at which the camera can zoom in and
     * out, while the zoom coefficient governs the rate at which the zoom speed increases.
     * Precondition: The supermenu item was selected.
     * Postcondition: The zoom dialog is displayed and the new zoom factor is applied to the 3D view.
     */
    private void openZoomDialog ()
    {
        Dialog<ButtonType> zoomDialog = new Dialog<>();
        zoomDialog.setTitle(Strings.STLV_SET_ZOOM_2);

        // Create the VBox for the dialog
        VBox dialogVBox = new VBox();
        // Add the text field for the zoom values
        TextField zoomLimit = new TextField();
        zoomLimit.setPromptText(Strings.STLV_ZOOM_LIMIT);
        zoomLimit.setText(String.valueOf(GUIController.getZoomLimit()));
        TextField zoomCoefficient = new TextField();
        zoomCoefficient.setPromptText(Strings.STLV_ZOOM_COEFF);
        zoomCoefficient.setText(String.valueOf(GUIController.getZoomCoefficient()));

        // Add the text field with label to the dialog VBox
        dialogVBox.getChildren().addAll(
                new Label(Strings.STLV_ZOOM_LIMIT + Strings.COLON), zoomLimit,
                new Label(Strings.STLV_ZOOM_COEFF + Strings.COLON), zoomCoefficient
        );

        zoomDialog.getDialogPane().setContent(dialogVBox);
        // Add the OK and Cancel buttons
        zoomDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the OK button click
        zoomDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double newZoomLimit = Double.parseDouble(zoomLimit.getText());
                    GUIController.setZoomLimit(newZoomLimit);
                    double newZoomCoefficient = Double.parseDouble(zoomCoefficient.getText());
                    GUIController.setZoomCoefficient(newZoomCoefficient);
                } catch (NumberFormatException e) {
                    // TODO: Handle invalid input
                }
            }
            return null;
        });

        // Show the dialog and wait for the user response
        zoomDialog.showAndWait();
    }

    /**
     * Configures the VBox properties for the information labels on the right side of the window. The labels display
     * information about the model, such as the number of triangles, surface area, and volume, as well as the view
     * properties, such as rotation and translation. Rotation and translation values are updated in real-time when
     * the user interacts with the 3D view. The other information is updated when a new model is loaded.
     * Precondition: The labels must be initialized.
     * Postcondition: The VBox is configured with the specified labels.
     *
     * @return The configured VBox for the information labels.
     */
    private VBox configureInfoLabels ()
    {
        VBox infoLabels = new VBox();
        // Set dimensions and padding for the VBox
        infoLabels.setPrefWidth(200);
        infoLabels.setPadding(new javafx.geometry.Insets(10));
        infoLabels.setSpacing(10);

        // Make the description label wrap text
        materialDescriptionLabel.setWrapText(true);
        materialLabel.setFont(Font.font(Strings.ARIAL, FontWeight.NORMAL, 14));

        // Add the labels for the number of triangles, surface area, and volume
        infoLabels.getChildren().addAll(
                makeLabelArial(Strings.STLV_MODEL_INFORMATION, FontWeight.BOLD, 16),
                makeLabelArial(Strings.STLV_NUMBER_OF_TRIANGLES, FontWeight.NORMAL, 14), numberOfTrianglesLabel,
                makeLabelArial(Strings.STLV_SURFACE_AREA, FontWeight.NORMAL, 14), surfaceAreaLabel,
                makeLabelArial(Strings.STLV_VOLUME, FontWeight.NORMAL, 14), volumeLabel,
                makeLabelArial(Strings.STLV_VIEW_PROPERTIES, FontWeight.BOLD, 16),
                makeLabelArial(Strings.STLC_ROTATION_LABEL, FontWeight.NORMAL, 14), rotationLabel,
                makeLabelArial(Strings.STLV_TRANSLATION_LABEL, FontWeight.NORMAL, 14), translationLabel,
                makeLabelArial(Strings.STLV_MATERIAL_INFORMATION, FontWeight.BOLD, 16),
                materialLabel, materialDescriptionLabel,
                makeLabelArial(Strings.STLV_WEIGHT, FontWeight.NORMAL, 14), weightLabel
        );
        return infoLabels;
    }

    /**
     * Updates the view properties labels with the current rotation and translation values.
     * Precondition: The controller must be initialized.
     * Postcondition: The labels are updated with the current rotation and translation values.
     */
    public void updateViewProperties ()
    {
        Platform.runLater(() -> {
            rotationLabel.setText(String.format(
                    Strings.STLV_VIEWPROP_ROTATE,
                    GUIController.getRotationX().getAngle(),
                    GUIController.getRotationY().getAngle()
            ));
            translationLabel.setText(String.format(
                    Strings.STLV_VIEWPROP_TRANSLATE,
                    GUIController.getTranslation().getX(),
                    GUIController.getTranslation().getY(),
                    GUIController.getTranslation().getZ()
            ));
        });
    }

    public void updateMaterialData ()
    {
        meshView.setMaterial(GUIController.getCurrentMaterial());
        Platform.runLater(() -> {
            materialLabel.setText(GUIController.getCurrentMaterial().getName());
            densityLabel.setText(String.format(Strings.FORMAT_STRING_2F, GUIController.getCurrentMaterial().getDensity()));
            weightLabel.setText(String.format(Strings.FORMAT_STRING_2F, GUIController.calculateMass(GUIController.getCurrentMaterial())) + " kg");
            materialDescriptionLabel.setText(GUIController.getCurrentMaterial().getDescription());
        });
    }

    public void updateWithNewUnits(double volume, double surfaceArea, double weight, String unitWeight) {
        Platform.runLater(() -> {
            // Recalculate the volume and weight with the new units
            volumeLabel.setText(roundToTwoNonZeroDigits(volume) + Strings.SPACE + Strings.STLV_UNIT_M3);
            surfaceAreaLabel.setText(roundToTwoNonZeroDigits(surfaceArea) + Strings.SPACE + Strings.STLV_UNIT_M2);
            weightLabel.setText(roundToTwoNonZeroDigits(weight) + Strings.SPACE + unitWeight);
        });
    }

    /**
     * Creates a new Label with the specified text, font weight, and font size. The font family is set to Arial.
     * Precondition: None.
     * Postcondition: A new Label is created with the specified text, font weight, and font size.
     *
     * @param labelText  - The text to display on the label.
     * @param fontWeight - The font weight for the label text.
     * @param fontSize   - The font size for the label text.
     * @return The new Label with the specified text, font weight, and font size.
     */
    private Label makeLabelArial (String labelText, FontWeight fontWeight, int fontSize)
    {
        Label label = new Label(labelText);
        label.setFont(Font.font(Strings.ARIAL, fontWeight, fontSize));
        return label;
    }

    /**
     * Configures the 3D view with the specified group and camera. The view is set to be anti-aliased for better
     * rendering quality. Sets up the 3D view dimensions and background color.
     * Precondition: The group and camera must be initialized.
     * Postcondition: The 3D view is configured with the specified group and camera.
     *
     * @return The configured 3D view.
     */
    private SubScene configure3DView ()
    {
        threeDView = new SubScene(threeDGroup, Constants.WINDOW_WIDTH - Constants.INFOBAR_WIDTH, Constants.WINDOW_HEIGHT, true, javafx.scene.SceneAntialiasing.BALANCED);
        threeDView.setFill(javafx.scene.paint.Color.LIGHTGREY);
        threeDView.setCamera(perspectiveCamera);

        return threeDView;
    }

    /**
     * Displays the 3D model in the STL viewer. The model is rendered in the 3D view, and the information labels are
     * updated with the model properties. The values are displayed with two decimal places.
     * Precondition: The model must be initialized.
     * Postcondition: The 3D model is displayed in the viewer, and the information labels are updated.
     *
     * @param polyhedron - The 3D model to display.
     */
    public void displayModel (Polyhedron polyhedron)
    {
        // Set the number of triangles, surface area, and volume labels
        numberOfTrianglesLabel.setText(String.valueOf(polyhedron.getTriangleCount()));
        surfaceAreaLabel.setText(String.valueOf(roundToTwoNonZeroDigits(polyhedron.getSurfaceArea())));
        volumeLabel.setText(String.valueOf(roundToTwoNonZeroDigits(polyhedron.getVolume())));
        // Apply the current material to the model
        updateMaterialData();

        // Render the 3D model
        GUIController.renderModel(polyhedron);
    }

    /**
     * Returns the MeshView object for the 3D model.
     *
     * @return The MeshView object for the 3D model.
     */
    public MeshView getMeshView ()
    {
        return meshView;
    }

    /**
     * Returns the PerspectiveCamera object for the 3D view.
     *
     * @return The PerspectiveCamera object for the 3D view.
     */
    public Camera getPerspectiveCamera ()
    {
        return perspectiveCamera;
    }

    /**
     * Returns the current field of view for the camera.
     *
     * @return The current field of view for the camera.
     */
    public double getFieldOfView ()
    {
        return perspectiveCamera.getFieldOfView();
    }

    /**
     * Returns the SubScene object for the 3D view.
     *
     * @return The SubScene object for the 3D view.
     */
    public SubScene getThreeDView ()
    {
        return threeDView;
    }

    /**
     * Returns the Group object for the 3D scene.
     *
     * @return The Group object for the 3D scene.
     */
    public Group getThreeDGroup ()
    {
        return threeDGroup;
    }

    /**
     * Returns the main stage for the application.
     *
     * @return The main stage for the application.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the list of materials available for the 3D model.
     *
     * @return  The list of materials available for the 3D model.
     */
    public ArrayList<com.example.stlviewer.model.Material> getMaterials() {
        return materials;
    }

    /**
     * Sets the list of materials available for the 3D model.
     *
     * @param materials - The list of materials available for the 3D model.
     */
    public void setMaterials(ArrayList<com.example.stlviewer.model.Material> materials) {
        this.materials = materials;
    }
}
