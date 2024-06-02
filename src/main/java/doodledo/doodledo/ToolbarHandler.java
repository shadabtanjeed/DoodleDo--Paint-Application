package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ToolbarHandler {
    private final WindowController windowController;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private Color selectedColor;
    private MasterController masterController;
    private String textToDraw = null;


    public Color canvasColor;
    public Color eraserColor;

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, MasterController masterController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController;
        setupCanvasHandlers();
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(masterController.getBrushWidth());
            brush.setStroke(selectedColor);
            brush.setLineCap(StrokeLineCap.BUTT);
            lastX = e.getX();
            lastY = e.getY();
            masterController.saveCurrentState();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            brush.lineTo(e.getX(), e.getY());
            brush.stroke();
            lastX = e.getX();
            lastY = e.getY();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            brush.closePath();
            WindowController.setIsSaved(false);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
            // Clear the previous temporary text
            if (textToDraw != null) {
                masterController.undoAction();
                // Save the state before drawing the new temporary text
                masterController.saveCurrentState();
                brush.setFont(new Font("Verdana", 10 + 3 * ( masterController.getBrushWidth() ) ));
                brush.setFill(selectedColor);
                brush.fillText(textToDraw, e.getX(), e.getY());
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (textToDraw != null) {
                masterController.undoAction();

                brush.setFont(new Font("Verdana", 10 + 3 * ( masterController.getBrushWidth() ) ));
                brush.setFill(selectedColor);
                brush.fillText(textToDraw, e.getX(), e.getY());

                textToDraw = null;
                masterController.saveCurrentState();
            }
        });
    }

    public void setCanvasColor(Color color) {
        eraserColor = color;
        brush.setFill(color);
        brush.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void selectBrush(Color color) {
        eraserSelected = false;
        selectedColor = color;
    }

    public void selectEraser() {
        eraserSelected = true;
        selectedColor = eraserColor;
    }

    public void clearCanvas() {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        setCanvasColor(canvasColor);
        WindowController.setIsSaved(false);
    }

    public void updateSelectedColor(Color color) {
        if (!eraserSelected) {
            selectedColor = color;
        }
    }

    public void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Images", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG Images", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Images", "*.jpeg"),
                new FileChooser.ExtensionFilter("SVG Images", "*.svg")
                );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));

                double scalingFactor = masterController.getBrushWidth()/15;
                double width = image.getWidth()*scalingFactor;
                double height = image.getHeight()*scalingFactor;

                brush.drawImage(image, 50, 50, width, height);
                masterController.saveCurrentState();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addText(String text) {
        textToDraw = text;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}