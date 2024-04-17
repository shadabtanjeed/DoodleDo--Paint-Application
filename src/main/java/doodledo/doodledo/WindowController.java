package doodledo.doodledo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.Color;

import java.util.ResourceBundle;
import java.net.URL;


public class WindowController implements Initializable {

    boolean eraserSelected = false;
    boolean isSaved = false;

    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker colorPalette;
    @FXML
    private GraphicsContext brush; // Renamed from brush to avoid conflict
    private double brushSize;
    private boolean brushSelected = true; // for testing. should be false by default...
    @FXML
    private Slider brushWidth;
    private double lastX, lastY; // store last position where mouse were dragged/pressed

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        brush = canvas.getGraphicsContext2D();
        colorPalette.setValue(Color.BLUE); // for testing
        brushWidth.setValue(5); // for testing

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(brushWidth.getValue());
            brush.setStroke(colorPalette.getValue());
            brush.setLineCap(StrokeLineCap.BUTT);
            lastX = e.getX();
            lastY = e.getY();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            brush.lineTo(e.getX(), e.getY()); // draw line from last known co-ordinate to current co-ordinate
            brush.stroke();
            lastX = e.getX();
            lastY = e.getY();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            brush.closePath();
        });
    }

    @FXML
    public void brushSelected(ActionEvent e) {
        brushSelected = true;
    }

    @FXML
    public void eraserSelected(ActionEvent e) {
        brushSelected = false;
        eraserSelected = true;
        colorPalette.setValue(Color.WHITE);
    }

    boolean closeConfirmation() {
        if (!isSaved) {

            //create a confirmation box
            Alert close_alert = new Alert(Alert.AlertType.CONFIRMATION);
            close_alert.setHeaderText("Save Confirmation");
            close_alert.setContentText("Do you want save your progress?");

            //create buttons
            ButtonType save_alert_button = new ButtonType("Save");
            ButtonType dont_save_alert_button = new ButtonType("Don't Save");
            ButtonType cancel_alert_button = new ButtonType("Cancel");
        }

        return false;
    }
}