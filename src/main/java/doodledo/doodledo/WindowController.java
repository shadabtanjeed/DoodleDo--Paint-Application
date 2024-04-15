package doodledo.doodledo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.fxml.Initializable;
import java.util.ResourceBundle;
import java.net.URL;


public class WindowController implements Initializable{

    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker colorPalette;
    @FXML
    private GraphicsContext brush; // Renamed from brush to avoid conflict
    private double brushSize;
    private boolean brushSelected = false;
    @FXML
    private Slider brushWidth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        brush = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {
            brushSize = brushWidth.getValue();

            // get co-ordinates of the clicked position
            double x = e.getX() - (brushSize / 2);
            double y = e.getY() - (brushSize / 2);

            if (brushSelected) {
                brush.setFill(colorPalette.getValue());
                brush.fillRoundRect(x, y, brushSize, brushSize, brushSize / 2, brushSize / 2); // Adjust the width and height
            }
        });
    }

    @FXML
    public void brushSelected(ActionEvent e) {
        brushSelected = true;
    }
}