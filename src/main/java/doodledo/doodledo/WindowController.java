package doodledo.doodledo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    private StateHandler stateHandler;
    @FXML
    private Canvas canvas;
    private ToolbarHandler toolbarHandler;

    public static boolean closeConfirmation() {
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit? Any unsaved changes will be lost.",
                "Exit DoodleDo",
                JOptionPane.YES_NO_OPTION);
        return confirmed != JOptionPane.YES_OPTION;
    }

    public static void setIsSaved(boolean b) {
        if (b) {
            System.out.println("Saved");
        } else {
            System.out.println("Not Saved");
        }
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), this, new MasterController());
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

    public Canvas getCanvas() {
        return canvas;
    }

    public ToolbarHandler getCanvasHandler() {
        return toolbarHandler;
    }
}