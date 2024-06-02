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
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;

import java.util.Optional;

import static doodledo.doodledo.CanvasInitController.globalCanvasColor;

public class MasterController implements Initializable {

    @FXML
    private ComboBox<String> shape_dropdown;

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

    @FXML
    private TextArea inputText;

    @FXML
    private Text hoveringText;

    public double getBrushWidth() {
        return brushWidth.getValue();
    }

    public void saveCurrentState() {
        double x = canvas.getWidth();
        double y = canvas.getHeight();
        stateHandler.saveCurrentState();
    }

    public void addImage() {
        toolbarHandler.addImage();
    }

    @FXML
    public void addText() {
        // prompt the user for text
        TextInputDialog dialog = new TextInputDialog("Enter text");
        dialog.setTitle("Add Text");
        dialog.setHeaderText("Enter the text you want to add to the canvas:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            toolbarHandler.addText(text);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<String> shape_dropdown_list = FXCollections.observableArrayList("Circle", "Square", "Rectangle", "Ellipse", "Triangle");
        shape_dropdown.setItems(shape_dropdown_list);

        shape_dropdown.getSelectionModel().selectedItemProperty().addListener((Observable observable) -> {
            String selectedShape = shape_dropdown.getSelectionModel().getSelectedItem();
            if (selectedShape != null) {
                toolbarHandler.selectShape(selectedShape, colorPalette.getValue(), getBrushWidth());
            }
//            shape_dropdown.getSelectionModel().clearSelection();
        });

        ObservableList<String> export_dropdown_list = FXCollections.observableArrayList("Image", "PDF");
        export_context_menu.setItems(export_dropdown_list);
        windowController = new WindowController(); // Create a new WindowController instance
        this.toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this,
                hoveringText);
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

        // listener for color palette
        colorPalette.valueProperty().addListener((observable, oldValue, newValue) -> {
            toolbarHandler.updateSelectedColor(newValue);
            if (toolbarHandler.softBrushSelected) {
                toolbarHandler.selectSoftBrush(newValue);
            }
            if (toolbarHandler.highlighterSelected) {
                toolbarHandler.selectHighLighter(newValue);
            }

            
        });

        // Add a change listener to the brushWidth
        brushWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toolbarHandler.softBrushSelected) {
                toolbarHandler.selectSoftBrush(colorPalette.getValue());
            }

            if (toolbarHandler.highlighterSelected) {
                toolbarHandler.selectHighLighter(colorPalette.getValue());
            }

            toolbarHandler.updateSelectedColor(colorPalette.getValue());
        });

        hoveringText = new Text();
        hoveringText.setVisible(false);

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


        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
            if (toolbarHandler.currentTool instanceof ShapeTool) {
                ((ShapeTool) toolbarHandler.currentTool).setStrokeColor(selectedColor);
            }
        });

        brushWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toolbarHandler.currentTool instanceof ShapeTool) {
                ((ShapeTool) toolbarHandler.currentTool).setStrokeWidth(newValue.doubleValue());
            }
        });
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

    public double getToolbarHeight() {
        return export_context_menu.getHeight();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @FXML
    public void HighLighterSelected() {
        toolbarHandler.selectHighLighter(colorPalette.getValue());
    }

    @FXML
    public void selectShape() {
        String selectedShape = shape_dropdown.getSelectionModel().getSelectedItem();
        Color selectedColor = colorPalette.getValue();
        double selectedWidth = getBrushWidth();
        toolbarHandler.selectShape(selectedShape, selectedColor, selectedWidth);
    }

    @FXML
    public void fillShape() {
        toolbarHandler.fillShape();
    }

}