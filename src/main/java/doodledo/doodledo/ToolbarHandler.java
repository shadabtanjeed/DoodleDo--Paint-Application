package doodledo.doodledo;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;

public class ToolbarHandler {
    private final WindowController windowController;
    private final Circle softBrush = new Circle();
    private final SnapshotParameters snapshotParams = new SnapshotParameters();
    public Color canvasColor;
    public Color eraserColor;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private boolean softBrushSelected = false;
    private Color selectedColor;
    private MasterController masterController;
    private RadialGradient brushGradient;

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController, MasterController masterController) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController;
        setupCanvasHandlers();

        snapshotParams.setFill(Color.TRANSPARENT);
        softBrush.setRadius(masterController.getBrushWidth() / 2);
        softBrush.setFill(createRadialGradient(selectedColor));
    }

    private RadialGradient createRadialGradient(Color color) {
        return new RadialGradient(
                0, 0, 0.5, 0.5, 0.5,
                true, CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, Color.TRANSPARENT)
        );
    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(masterController.getBrushWidth());
            brush.setStroke(selectedColor);
            brush.setLineCap(StrokeLineCap.ROUND);  // Change StrokeLineCap to ROUND
            lastX = e.getX();
            lastY = e.getY();
            masterController.saveCurrentState();
            FileHandler.setIsSaved(false);

            if (softBrushSelected) {
                brush.setFill(brushGradient);
                brush.fillOval(e.getX() - softBrush.getRadius(), e.getY() - softBrush.getRadius(), softBrush.getRadius() * 2, softBrush.getRadius() * 2);
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            double currentX = e.getX();
            double currentY = e.getY();

            if (softBrushSelected) {
                Image brushImage = snapshotBrushImage();
                double distance = Math.sqrt(Math.pow(currentX - lastX, 2) + Math.pow(currentY - lastY, 2));
                int steps = (int) Math.max(distance, 1);

                for (int i = 0; i < steps; i++) {
                    double t = (double) i / (steps - 1);
                    double x = lerp(lastX, currentX, t);
                    double y = lerp(lastY, currentY, t);
                    brush.drawImage(
                            brushImage,
                            x - softBrush.getRadius(),
                            y - softBrush.getRadius()
                    );
                }
            }

            if (!softBrushSelected) {
                brush.lineTo(currentX, currentY);
                brush.stroke();
            }

            lastX = currentX;
            lastY = currentY;
            FileHandler.setIsSaved(false);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            brush.closePath();
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
        eraserSelected = false;
        softBrushSelected = false;
        selectedColor = color;
    }

    public void selectEraser() {
        eraserSelected = true;
        softBrushSelected = false;
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
            brushGradient = createRadialGradient(selectedColor);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void selectSoftBrush(Color value) {
        softBrush.setFill(createRadialGradient(value));
        softBrushSelected = true;
        softBrush.setRadius(masterController.getBrushWidth() / 2);
        brushGradient = createRadialGradient(value);
    }

    private Image snapshotBrushImage() {
        return softBrush.snapshot(snapshotParams, null);
    }

    private double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }
}