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
import java.util.Stack;

import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;

public class WindowController implements Initializable {

    // public static boolean isSaved = true;
    public static boolean isSaved = true;
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

    private Stack<WritableImage> undoStack = new Stack<>();
    private Stack<WritableImage> redoStack = new Stack<>();

    // return true indicates that the program termination is way to go
    // return false refrains from termination
    public static boolean closeConfirmation() {
        if (!isSaved) {
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
                SaveImage();
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

    private static void SaveImage() {
        FileChooser ImageSaver = new FileChooser();
        ImageSaver.setTitle("Save Image File");

        // image data types
        ImageSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG"));

        // show the file save location dialogue box
        File savefile = ImageSaver.showSaveDialog(null);
        if (savefile != null) {
            try {
                // writeable image format created as the canvas size
                WritableImage writableImage = new WritableImage((int) staticCanvas.getWidth(),
                        (int) staticCanvas.getHeight());
                // snapshot of the canvas stored into writable image
                staticCanvas.snapshot(null, writableImage);
                // writeable image converted to regular image format
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

                ImageIO.write(renderedImage, "png", savefile);
                isSaved = true;
            } catch (IOException ex) {
                System.err.println("Unable to Save");
            }
        }
    }

    public void saveCurrentState() {
        redoStack.clear();
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);
        undoStack.push(snapshot);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code...
        staticBrushWidth = brushWidth;
        staticCanvas = canvas;

        brush = canvas.getGraphicsContext2D();
        colorPalette.setValue(Color.BLUE); // for testing
        brushWidth.setValue(5); // for testing
        saveCurrentState();

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            saveCurrentState();
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
            isSaved = false;
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
        isSaved = false;
    }

    @FXML
    public void undoAction(ActionEvent e) {
        // undo the last action
        if (!undoStack.isEmpty()) {
            WritableImage currentSnapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, currentSnapshot);
            redoStack.push(currentSnapshot);

            // restore previous state from undo stack
            WritableImage previousSnapshot = undoStack.pop();
            brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            brush.drawImage(previousSnapshot, 0, 0);

            isSaved = false;
        }
    }

    @FXML
    public void redoAction(ActionEvent e) {
        if (!redoStack.isEmpty()) {
            WritableImage curentSnapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, curentSnapshot);
            undoStack.push(curentSnapshot);

            // restore previous state from redo stack
            WritableImage nextSnapshot = redoStack.pop();
            brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            brush.drawImage(nextSnapshot, 0, 0);

            isSaved = false;
        }
    }

}
