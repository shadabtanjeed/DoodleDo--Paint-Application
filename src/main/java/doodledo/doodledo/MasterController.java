package doodledo.doodledo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

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

    public double getBrushWidth() {
        return brushWidth.getValue();
    }

    public Color getColorPaletteValue() {
        return colorPalette.getValue();
    }

    public void saveCurrentState() {
        stateHandler.saveCurrentState();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        windowController = new WindowController(); // Create a new WindowController instance
        toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this); // Pass the WindowController and MasterController instance to ToolbarHandler
        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
        toolbarHandler.setCanvasColor(Color.BLACK);
        stateHandler.saveCurrentState();
    }

    @FXML
    public void brushSelected() {
        toolbarHandler.selectBrush(colorPalette.getValue());
    }

    @FXML
    public void eraserSelected() {
        toolbarHandler.selectEraser(Color.WHITE);
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
}