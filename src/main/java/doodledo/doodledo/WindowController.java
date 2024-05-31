package doodledo.doodledo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML
    private static Canvas canvas;
    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    private StateHandler stateHandler;
    private ToolbarHandler toolbarHandler;

    public static boolean closeConfirmation() {
        if (!FileHandler.getIsSaved()) {
            // create a confirmation box
            Alert close_alert = new Alert(Alert.AlertType.CONFIRMATION);
            close_alert.setHeaderText("Save Confirmation");
            close_alert.setContentText("Do you want save your progress?");

            // create buttons for confirmation box
            ButtonType save_alert_button = new ButtonType("Save");
            ButtonType dont_save_alert_button = new ButtonType("Don't Save");
            ButtonType cancel_alert_button = new ButtonType("Cancel");

            // add buttons to the confirmation box
            close_alert.getButtonTypes().setAll(save_alert_button, dont_save_alert_button, cancel_alert_button);

            // show the confirmation box
            Optional<ButtonType> result = close_alert.showAndWait();

            if (result.get() == save_alert_button) {
                // save the canvas
                Canvas canvas1 = WindowController.getCanvas();
                FileHandler.saveImage(canvas1);
                return true;
            } else if (result.get() == dont_save_alert_button) {
                // exits the program
                System.exit(0);
                return true;
            } else if (result.get() == cancel_alert_button) {
                return true;
            }
        }
        return false;
    }

    public static void setIsSaved(boolean b) {
        if (b) {
            System.out.println("Saved");
        } else {
            System.out.println("Not Saved");
        }
    }

    public static Canvas getCanvas() {
        return canvas;
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

    public ToolbarHandler getCanvasHandler() {
        return toolbarHandler;
    }
}