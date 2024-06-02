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
    private GraphicsContext tempGraphicsContext;

    public ShapeTool(Canvas canvas, GraphicsContext graphicsContext, String shape, Color strokeColor, double strokeWidth) {
        super(canvas, graphicsContext);
        this.shape = shape;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.tempGraphicsContext = canvas.getGraphicsContext2D();
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
        tempGraphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Set the stroke color and width
        tempGraphicsContext.setStroke(strokeColor);
        tempGraphicsContext.setLineWidth(strokeWidth);

        // Draw the temporary shape on the temporary graphics context
        drawShape(tempGraphicsContext, startX, startY, endX, endY);
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        // Draw the final shape on the main graphics context
        drawShape(graphicsContext, startX, startY, event.getX(), event.getY());
    }

    private void drawShape(GraphicsContext gc, double startX, double startY, double endX, double endY) {
        switch (shape) {
            case "Circle":
                double radius = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                gc.strokeOval(startX, startY, radius, radius);
                break;
            case "Square":
                double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                gc.strokeRect(startX, startY, size, size);
                break;
            case "Rectangle":
                gc.strokeRect(startX, startY, endX - startX, endY - startY);
                break;
            case "Ellipse":
                gc.strokeOval(startX, startY, endX - startX, endY - startY);
                break;
            case "Triangle":
                gc.strokePolygon(new double[]{startX, startX - (endX - startX) / 2, startX + (endX - startX) / 2},
                        new double[]{startY, startY + (endY - startY), startY + (endY - startY)}, 3);
                break;
        }
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}