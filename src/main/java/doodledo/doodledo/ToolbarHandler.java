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
    Tool currentTool;
    boolean softBrushSelected = false;
    boolean highlighterSelected = false;
    boolean eraserSelected = false;
    private Canvas canvas;
    private GraphicsContext brush;
    private double lastX, lastY;
    private boolean brushSelected = true;
    private boolean shapeSelected = false;
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
            if (currentTool != null) {
                currentTool.onMousePressed(e);
            }

            if (brushSelected) {
                brush.beginPath();
                brush.moveTo(e.getX(), e.getY());
                brush.setLineWidth(masterController.getBrushWidth());
                brush.setStroke(selectedColor);
                brush.setLineCap(StrokeLineCap.ROUND);
                lastX = e.getX();
                lastY = e.getY();
            }

            masterController.saveCurrentState();
            FileHandler.setIsSaved(false);

            if (softBrushSelected) {
                brush.setFill(brushGradient);
                brush.fillOval(e.getX() - softBrush.getRadius(), e.getY() - softBrush.getRadius(),
                        softBrush.getRadius() * 2, softBrush.getRadius() * 2);
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
            if (currentTool != null) {
                currentTool.onMouseDragged(e);
            }

            if (brushSelected) {

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
            }

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
            if (currentTool != null) {
                currentTool.onMouseReleased(e);
            }

            if (brushSelected) {
                if (textToDraw != null) {
                    double fontSize = Math.min(10 + 3 * (masterController.getBrushWidth()), canvas.getHeight() - 1);
                    brush.setFont(new Font("Verdana", fontSize));
                    brush.setFill(selectedColor);
                    brush.fillText(textToDraw, lastX, lastY);

                    textToDraw = null;
                    hoveringText.setVisible(false);
                    masterController.saveCurrentState();
                } else {
                    brush.closePath();
                }

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
        brushSelected = true;
        eraserSelected = false;
        softBrushSelected = false;
        highlighterSelected = false;
        selectedColor = color;
        shapeSelected = false;
        currentTool = null;
    }

    public void selectBrush(Color color, String eraser) {
        brushSelected = true;
        eraserSelected = true;
        softBrushSelected = false;
        highlighterSelected = false;
        selectedColor = color;
        shapeSelected = false;
        currentTool = null;
    }

    public void selectEraser() {
        eraserSelected = true;
        softBrushSelected = false;
        highlighterSelected = false;
        selectedColor = eraserColor;
        brushSelected = false;
        shapeSelected = false;
        currentTool = null;
        selectBrush(eraserColor, "");
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

    public void selectShape(String shape, Color strokeColor, double strokeWidth) {
        currentTool = new ShapeTool(canvas, brush, shape, strokeColor, strokeWidth);
        shapeSelected = true;
        brushSelected = false;
        eraserSelected = false;
    }

    public void fillShape() {
        if (currentTool instanceof ShapeTool) {
            ((ShapeTool) currentTool).setFillShape(true, selectedColor);
        }
    }

    public Color getSelectedColor() {
        return selectedColor;
    }
}
