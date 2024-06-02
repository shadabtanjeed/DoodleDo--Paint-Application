package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class ToolbarHandler {
    private final WindowController windowController;
    public Color canvasColor;
    public Color eraserColor;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;

    private boolean brushSelected = true;
    private boolean eraserSelected = false;

    private boolean shapeSelected = false;
    private Color selectedColor;
    private MasterController masterController;

    private Tool currentTool;

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, MasterController masterController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController; // Initialize MasterController here
        setupCanvasHandlers();
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (currentTool != null) {
                currentTool.onMousePressed(e);
            }

            if (brushSelected) {
                brush.beginPath();
                brush.moveTo(e.getX(), e.getY());
                brush.setLineWidth(masterController.getBrushWidth());
                brush.setStroke(selectedColor);
                brush.setLineCap(StrokeLineCap.BUTT);
                lastX = e.getX();
                lastY = e.getY();
            }

            masterController.saveCurrentState();
            FileHandler.setIsSaved(false);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (currentTool != null) {
                currentTool.onMouseDragged(e);
            }

            if (brushSelected) {
                brush.lineTo(e.getX(), e.getY());
                brush.stroke();
                lastX = e.getX();
                lastY = e.getY();
            }

            FileHandler.setIsSaved(false);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (currentTool != null) {
                currentTool.onMouseReleased(e);
            }

            if (brushSelected) {
                brush.closePath();
            }

            WindowController.setIsSaved(false);
            FileHandler.setIsSaved(false);
        });
    }

    public void setCanvasColor(Color color) {
        eraserColor = color;
        canvasColor = color;
        brush.setFill(color);
        brush.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void selectBrush(Color color) {
        brushSelected = true;
        eraserSelected = false;
        selectedColor = color;
        shapeSelected = false;
        currentTool = null;
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

    public Canvas getCanvas() {
        return canvas;
    }

    public void selectShape(String shape, Color strokeColor, double strokeWidth) {
        // Set the current tool to a new ShapeTool
        currentTool = new ShapeTool(canvas, brush, shape, strokeColor, strokeWidth);
        shapeSelected = true;
        brushSelected = false;
    }
}

