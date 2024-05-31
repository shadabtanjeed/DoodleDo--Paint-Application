package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import java.util.Stack;

public class ToolbarHandler {
    private final WindowController windowController;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private boolean fillSelected = false;
    private Color selectedColor;
    private MasterController masterController;
    public Color canvasColor;
    public Color eraserColor;

    private class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, MasterController masterController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController; // Initialize MasterController here
        setupCanvasHandlers();
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (fillSelected) {
                //point coordinates, target color, replacement color
                fillShape((int) e.getX(), (int) e.getY(), selectedColor, masterController.getColorPaletteValue());
                System.out.println("FillShape called when mouse pressed");
            } else {
                brush.beginPath();
                brush.moveTo(e.getX(), e.getY());
                brush.setLineWidth(masterController.getBrushWidth());
                brush.setStroke(selectedColor);
                brush.setLineCap(StrokeLineCap.BUTT);
                lastX = e.getX();
                lastY = e.getY();
                masterController.saveCurrentState();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (!fillSelected) {
                brush.lineTo(e.getX(), e.getY());
                brush.stroke();
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (!fillSelected) {
                brush.closePath();
                WindowController.setIsSaved(false);
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
        fillSelected = false;
        selectedColor = color;
    }

    public void selectEraser() {
        eraserSelected = true;
        fillSelected = false;
        selectedColor = eraserColor;
    }

    public void selectFill() {
        System.out.println("selectFill() called, ");
        fillSelected = true;
        eraserSelected = false;
        selectedColor = getPixelColor((int) lastX, (int) lastY);
    }

    public Color getPixelColor(int x, int y) {
        System.out.println("getPixelColor() called");
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        return writableImage.getPixelReader().getColor(x, y);
    }

    public void clearCanvas() {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        setCanvasColor(canvasColor);
        WindowController.setIsSaved(false);
    }

    public void updateSelectedColor(Color color) {
        if (!eraserSelected && !fillSelected) {
            selectedColor = color;
        }
    }

    public void fillShape(int x, int y, Color targetColor, Color replacementColor) {
        System.out.println("executing fillShape");
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        Color backgroundColor = eraserColor; // Assuming eraserColor is the background color

        if (!writableImage.getPixelReader().getColor(x, y).equals(backgroundColor) || writableImage.getPixelReader().getColor(x, y).equals(replacementColor)) {
            return;
        }

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            if ((p.x < 0) || (p.y < 0) || (p.x >= writableImage.getWidth()) || (p.y >= writableImage.getHeight())) {
                continue;
            }

            if (!writableImage.getPixelReader().getColor(p.x, p.y).equals(backgroundColor)) {
                continue;
            }

            writableImage.getPixelWriter().setColor(p.x, p.y, replacementColor);

            stack.push(new Point(p.x+1, p.y));
            stack.push(new Point(p.x-1, p.y));
            stack.push(new Point(p.x, p.y+1));
            stack.push(new Point(p.x, p.y-1));
        }

        brush.drawImage(writableImage, 0, 0);
        WindowController.setIsSaved(false);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}