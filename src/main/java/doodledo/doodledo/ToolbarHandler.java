package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class ToolbarHandler {
    private final WindowController windowController;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private Color selectedColor = Color.BLUE;
    private MasterController masterController;
    public Color canvasColor;
    public Color eraserColor;

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, MasterController masterController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController; // Initialize MasterController here
        setupCanvasHandlers();
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(masterController.getBrushWidth());
            brush.setStroke(masterController.getColorPaletteValue());
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
        selectedColor = canvasColor;
    }

    public void clearCanvas() {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        setCanvasColor(canvasColor);
        WindowController.setIsSaved(false);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}