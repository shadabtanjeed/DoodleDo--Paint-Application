package doodledo.doodledo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.util.Optional;
import java.util.ResourceBundle;
import java.net.URL;

import javafx.embed.swing.SwingFXUtils;


import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.image.WritableImage;


public class WindowController implements Initializable {

    static boolean eraserSelected = false;
    static boolean isSaved = true;

    @FXML
    private Button saveButton;

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
    private WritableImage writableImage;
    private int widthOfCanvas = 1920; // replace with actual width
    private int heightOfCanvas = 1080; // replace with actual height
    private File file = new File("output.png"); // replace with actual file path

    public static boolean closeConfirmation() {
        if (!isSaved) {

            //create a confirmation box
            Alert close_alert = new Alert(Alert.AlertType.CONFIRMATION);
            close_alert.setHeaderText("Save Confirmation");
            close_alert.setContentText("Do you want save your progress?");

            //create buttons
            ButtonType save_alert_button = new ButtonType("Save");
            ButtonType dont_save_alert_button = new ButtonType("Don't Save");
            ButtonType cancel_alert_button = new ButtonType("Cancel");

            close_alert.getButtonTypes().setAll(save_alert_button, dont_save_alert_button, cancel_alert_button);

            Optional<ButtonType> result = close_alert.showAndWait();
        }

        return false;
//      return true;
    }

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

    @FXML
    public void saveSelected(ActionEvent e) {
        // save the canvas
        SaveImage();
        System.exit(0);


    }

    private void saveImage() {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        File outputFile = new File("output.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outputFile);
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}