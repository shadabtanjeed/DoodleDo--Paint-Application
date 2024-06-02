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
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ToolbarHandler {
    private final WindowController windowController;
    private final Circle softBrush = new Circle();
    private final SnapshotParameters snapshotParams = new SnapshotParameters();
    public Color canvasColor;
    public Color eraserColor;
    boolean softBrushSelected = false;
    boolean highlighterSelected = false;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean eraserSelected = false;
    private Color selectedColor;
    private MasterController masterController;
    private RadialGradient brushGradient;
    private String textToDraw = null;
    private Text hoveringText;
    private double toolbarHeight;

    public ToolbarHandler(Canvas canvas, GraphicsContext brush, WindowController windowController,
                          MasterController masterController, Text hoveringText) {
        this.canvas = canvas;
        this.brush = brush;
        this.windowController = windowController;
        this.masterController = masterController;
        this.hoveringText = hoveringText;
        this.toolbarHeight = masterController.getToolbarHeight();
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
                new Stop(1, Color.TRANSPARENT));


    }

    private void setupCanvasHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            brush.beginPath();
            brush.moveTo(e.getX(), e.getY());
            brush.setLineWidth(masterController.getBrushWidth());
            brush.setStroke(selectedColor);
            brush.setLineCap(StrokeLineCap.ROUND); // Change StrokeLineCap to ROUND
            lastX = e.getX();
            lastY = e.getY();
            masterController.saveCurrentState();
            FileHandler.setIsSaved(false);

            if (softBrushSelected) {
                brush.setFill(brushGradient);
                brush.fillOval(e.getX() - softBrush.getRadius(), e.getY() - softBrush.getRadius(),
                        softBrush.getRadius() * 2, softBrush.getRadius() * 2);
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
                            y - softBrush.getRadius());
                }
            }

            if (highlighterSelected) {
                brush.setLineWidth(masterController.getBrushWidth());
                brush.setStroke(selectedColor);
                brush.setLineCap(StrokeLineCap.BUTT);
                brush.strokeLine(lastX, lastY, currentX, currentY);
            }

            if (!softBrushSelected && !highlighterSelected) {
                brush.lineTo(currentX, currentY);
                brush.stroke();
            }

            lastX = currentX;
            lastY = currentY;
            FileHandler.setIsSaved(false);
        });


        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (textToDraw != null) {
                brush.setFont(new Font("Verdana", 10 + 3 * (masterController.getBrushWidth())));
                brush.setFill(selectedColor);
                brush.fillText(textToDraw, e.getX(), e.getY());

                textToDraw = null;
                hoveringText.setVisible(false);
                masterController.saveCurrentState();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            if (textToDraw != null) {
                brush.setFont(new Font("Verdana", 10 + 3 * (masterController.getBrushWidth())));
                brush.setFill(selectedColor);
                brush.fillText(textToDraw, e.getX(), e.getY());

                // textToDraw = null; // Remove this line
                hoveringText.setVisible(false);
                masterController.saveCurrentState();
            } else {
                brush.closePath();
            }

            FileHandler.setIsSaved(false);
        });


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
            if (textToDraw != null && e.getY() > toolbarHeight) {
                double fontSize = 10 + 3 * (masterController.getBrushWidth());
                hoveringText.setFont(new Font("Verdana", fontSize));
                hoveringText.setFill(selectedColor);
                hoveringText.setX(e.getX());
                hoveringText.setY(e.getY() + 2 * fontSize);
                hoveringText.setText(textToDraw);
                hoveringText.setVisible(true);
            } else {
                hoveringText.setVisible(false);
            }
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
        highlighterSelected = false;
        selectedColor = color;
    }

    public void selectEraser() {
        eraserSelected = true;
        softBrushSelected = false;
        highlighterSelected = false;
        selectedColor = eraserColor;
    }

    public void clearCanvas() {
        brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        setCanvasColor(canvasColor);

    }

    public void updateSelectedColor(Color color) {
        if (!eraserSelected) {
            if (highlighterSelected) {
                selectedColor = Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.4);
            } else {
                selectedColor = color;
            }
            brushGradient = createRadialGradient(selectedColor);
        }
    }

    public void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Images", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG Images", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Images", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));

                double scalingFactor = masterController.getBrushWidth() / 15;
                double width = image.getWidth() * scalingFactor;
                double height = image.getHeight() * scalingFactor;

                brush.drawImage(image, 50, 50, width, height);
                masterController.saveCurrentState();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addText(String text) {
        textToDraw = text;
        if (highlighterSelected) {
            selectedColor = Color.color(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), 1.0);
        }
        highlighterSelected = false;
        softBrushSelected = false;
        eraserSelected = false;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void selectSoftBrush(Color value) {
        softBrushSelected = true;
        highlighterSelected = false;
        eraserSelected = false;

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

    public void selectHighLighter(Color value) {
        highlighterSelected = true;
        selectedColor = Color.color(value.getRed(), value.getGreen(), value.getBlue(), 0.4);
        softBrushSelected = false;
        eraserSelected = false;
    }
}