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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;

public class WindowController implements Initializable {

    //public static boolean isSaved = true;
    public static boolean isSaved = false;
    // Static fields to hold references to instance variables
    private static Slider staticBrushWidth;
    private static Canvas staticCanvas;
    // Instance variables
    @FXML
    private Slider brushWidth;
    @FXML
    private Canvas canvas;
    @FXML
    private Button saveButton;
    @FXML
    private ColorPicker colorPalette;
    @FXML
    private GraphicsContext brush;
    private boolean eraserSelected = false;
    private double lastX, lastY;

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

            if (result.get() == save_alert_button) {
                //save the canvas
                SaveImage();
                return true;
            } else if (result.get() == dont_save_alert_button) {
                System.exit(0);
                return true;
            } else if (result.get() == cancel_alert_button) {
                return true;
            }
        }
        return false;
    }

    private static void SaveImage() {
        FileChooser ImageSaver = new FileChooser();
        ImageSaver.setTitle("Save Image File");

        //image data types
        ImageSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG"));

        File savefile = ImageSaver.showSaveDialog(null);
        if (savefile != null) {
            try {
                WritableImage writableImage = new WritableImage((int) staticCanvas.getWidth(), (int) staticCanvas.getHeight());
                staticCanvas.snapshot(null, writableImage); //make a snapshot and store it in writableImage
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null); //convert
                ImageIO.write(renderedImage, "png", savefile); //saves the file
                isSaved = true;
            } catch (IOException ex) {
                System.err.println("Unable to Save");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code...
        staticBrushWidth = brushWidth;
        staticCanvas = canvas;

        brush = canvas.getGraphicsContext2D();
        colorPalette.setValue(Color.BLUE); // for testing
        brushWidth.setValue(5); // for testing

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(staticBrushWidth.getValue());
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
        eraserSelected = false;
        colorPalette.setValue(Color.BLACK);
    }

    @FXML
    public void eraserSelected(ActionEvent e) {
        eraserSelected = true;
        colorPalette.setValue(Color.WHITE);
    }

    @FXML
    public void saveSelected(ActionEvent e) {
        // save the canvas
        SaveImage();
    }

    @FXML
    public void clearCanvas(ActionEvent e) {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
