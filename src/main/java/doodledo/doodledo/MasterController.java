package doodledo.doodledo;


import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static doodledo.doodledo.CanvasInitController.globalCanvasColor;


public class MasterController implements Initializable {

    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    private StateHandler stateHandler;
    @FXML
    private Canvas canvas;
    private ToolbarHandler toolbarHandler;
    private WindowController windowController; // New instance variable
    @FXML
    private ComboBox<String> export_context_menu;

    private Color initBrushColor = Color.BLUE;
    private Color initCanvasColor = globalCanvasColor;


    public double getBrushWidth() {
        return brushWidth.getValue();
    }


    public void saveCurrentState() {
        stateHandler.saveCurrentState();
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> export_dropdown_list = FXCollections.observableArrayList("Image", "PDF");
        export_context_menu.setItems(export_dropdown_list);
        windowController = new WindowController(); // Create a new WindowController instance
        this.toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this);
        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
        stateHandler.saveCurrentState();

        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
        });

        if (initCanvasColor != null) {
            Platform.runLater(() -> toolbarHandler.setCanvasColor(initCanvasColor));
        }

        colorPalette.setValue(initBrushColor);
        toolbarHandler.updateSelectedColor(initBrushColor);

        // Add action listener to export_context_menu
        export_context_menu.getSelectionModel().selectedItemProperty().addListener((Observable observable) -> {
            String selected = export_context_menu.getSelectionModel().getSelectedItem();
            if ("Image".equals(selected)) {
                saveSelected();
            } else if ("PDF".equals(selected)) {
                exportPDFAction();
            }
            // Clear the selection
            export_context_menu.getSelectionModel().clearSelection();
        });

        WindowController.setCanvas(this.getCanvas());
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
    public void softBrushSelected() {
        toolbarHandler.selectSoftBrush(colorPalette.getValue());
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

    @FXML
    public void exportPDFAction() {
        FileHandler.exportCanvasToPdf(canvas);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @FXML
    public void HighLighterSelected() {
        toolbarHandler.selectHighLighter(colorPalette.getValue());
    }


}