package doodledo.doodledo;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MasterController implements Initializable {

    private final Color initBrushColor = Color.BLUE;
    private Color initCanvasColor = Color.CRIMSON;
    private boolean isCanvasColorSet = false;

    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    @FXML
    private Canvas canvas;
    @FXML
    private ComboBox<String> export_context_menu;
    @FXML
    private ColorPicker canvasColorSelector;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private StateHandler stateHandler;
    private ToolbarHandler toolbarHandler;
    private WindowController windowController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(this::initializeControllers);
        initializeColorPalette();
        initializeExportContextMenu();
    }

    private void initializeControllers() {
        windowController = new WindowController();
        toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this);
        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
    }

    private void initializeColorPalette() {
        colorPalette.setValue(initBrushColor);
        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
        });
    }

    private void initializeExportContextMenu() {
        ObservableList<String> export_dropdown_list = FXCollections.observableArrayList("Image", "PDF");
        export_context_menu.setItems(export_dropdown_list);
        export_context_menu.getSelectionModel().selectedItemProperty().addListener((Observable observable) -> {
            String selected = export_context_menu.getSelectionModel().getSelectedItem();
            if ("Image".equals(selected)) {
                saveSelected();
            } else if ("PDF".equals(selected)) {
                exportPDFAction();
            }
            export_context_menu.getSelectionModel().clearSelection();
        });
    }

    public void continueToMainWindow(ActionEvent event) throws IOException {
        setInitCanvasColor(canvasColorSelector.getValue());
        switchToMainCanvas(event);
        updateCanvasColor();
    }

    public void switchToMainCanvas(ActionEvent event) throws IOException {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_window.fxml")));
        scene = new Scene(root, 1080, 720);
        stage.setTitle("DoodleDo");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo.png")));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        stage.setOnCloseRequest((WindowEvent we) -> {
            if (WindowController.closeConfirmation()) {
                we.consume();
            }
        });
    }

    public void updateCanvasColor() {
        if (toolbarHandler != null) {
            toolbarHandler.setCanvasColor(initCanvasColor);
            isCanvasColorSet = true;
            canvas.getGraphicsContext2D().setFill(initCanvasColor);
            canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    @FXML
    public void brushSelected() {
        toolbarHandler.selectBrush(colorPalette.getValue());
    }

    @FXML
    public void eraserSelected() {
        toolbarHandler.selectEraser();
    }

    @FXML
    public void saveSelected() {
        FileHandler.saveImage(canvas);
    }

    @FXML
    public void clearCanvas() {
        toolbarHandler.clearCanvas();
    }

    @FXML
    public void undoAction() {
        stateHandler.undoAction();
    }

    @FXML
    public void redoAction() {
        stateHandler.redoAction();
    }

    public void exportPDFAction() {
        FileHandler.exportCanvasToPdf(canvas);
    }

    public double getBrushWidth() {
        return brushWidth.getValue();
    }

    public Color getColorPaletteValue() {
        return colorPalette.getValue();
    }

    public void saveCurrentState() {
        stateHandler.saveCurrentState();
    }

    public void updateCanvas() {
        if (isCanvasColorSet) {
            toolbarHandler.setCanvasColor(initCanvasColor);
        }
    }

    public Color getInitCanvasColor() {
        return this.initCanvasColor;
    }

    public void setInitCanvasColor(Color color) {
        this.initCanvasColor = color;
        isCanvasColorSet = true;
    }

    public Canvas getCanvas() {
        return canvas;
    }


}