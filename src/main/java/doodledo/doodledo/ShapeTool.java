package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ShapeTool extends Tool {

    private String shape;
    private double startX, startY;
    private Color strokeColor;
    private double strokeWidth;

    public ShapeTool(Canvas canvas, GraphicsContext graphicsContext, String shape, Color strokeColor, double strokeWidth) {
        super(canvas, graphicsContext);
        this.shape = shape;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        double endX = event.getX();
        double endY = event.getY();

        // Clear the previous temporary shape
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Set the stroke color and width
        graphicsContext.setStroke(strokeColor);
        graphicsContext.setLineWidth(strokeWidth);

        switch (shape) {
            case "Circle":
                double radius = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                graphicsContext.strokeOval(startX, startY, radius, radius);
                break;
            case "Square":
                double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                graphicsContext.strokeRect(startX, startY, size, size);
                break;
            case "Rectangle":
                graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);
                break;
            case "Ellipse":
                graphicsContext.strokeOval(startX, startY, endX - startX, endY - startY);
                break;
            case "Triangle":
                graphicsContext.strokePolygon(new double[]{startX, startX - (endX - startX) / 2, startX + (endX - startX) / 2},
                        new double[]{startY, startY + (endY - startY), startY + (endY - startY)}, 3);
                break;
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        // Draw the final shape
        onMouseDragged(event);
    }
}