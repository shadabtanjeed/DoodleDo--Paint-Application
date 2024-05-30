package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class CanvasHandler {
    private final WindowController windowController;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private Color eraserColor;
    private Color selectedColor = Color.BLUE;
    private UIController uiController;

    public CanvasHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, UIController uiController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.uiController = uiController; // Initialize uiController here
        setupCanvasHandlers();
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(uiController.getBrushWidth());
            brush.setStroke(uiController.getColorPaletteValue());
            brush.setLineCap(StrokeLineCap.BUTT);
            lastX = e.getX();
            lastY = e.getY();
            uiController.saveCurrentState();
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

    public void selectEraser(Color color) {
        eraserSelected = true;
        selectedColor = eraserColor;
    }

    public void clearCanvas() {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        WindowController.setIsSaved(false);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}