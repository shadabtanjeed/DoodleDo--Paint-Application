package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ShapeTool extends Tool {

    private boolean fillShape = false;
    private Color fillColor;
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
        tempGraphicsContext.setStroke(strokeColor);
        tempGraphicsContext.setLineWidth(strokeWidth);
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        tempGraphicsContext.setStroke(strokeColor);
        tempGraphicsContext.setLineWidth(strokeWidth);
        drawShape(graphicsContext, startX, startY, event.getX(), event.getY());
    }

    private void drawShape(GraphicsContext gc, double startX, double startY, double endX, double endY) {
        switch (shape) {
            case "Line":
                gc.strokeLine(startX, startY, endX, endY);
                break;
            case "Circle":
                double radius = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                gc.strokeOval(startX, startY, radius, radius);
                if (fillShape) {
                    gc.setFill(fillColor);
                    gc.fillOval(startX, startY, radius, radius);
                }
                break;
            case "Square":
                double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                gc.strokeRect(startX, startY, size, size);
                if (fillShape) {
                    gc.setFill(fillColor);
                    gc.fillRect(startX, startY, size, size);
                }
                break;
            case "Rectangle":
                gc.strokeRect(startX, startY, endX - startX, endY - startY);
                if (fillShape) {
                    gc.setFill(fillColor);
                    gc.fillRect(startX, startY, endX - startX, endY - startY);
                }
                break;
            case "Ellipse":
                gc.strokeOval(startX, startY, endX - startX, endY - startY);
                if (fillShape) {
                    gc.setFill(fillColor);
                    gc.fillOval(startX, startY, endX - startX, endY - startY);
                }
                break;
            case "Triangle":
                gc.strokePolygon(new double[]{startX, startX - (endX - startX) / 2, startX + (endX - startX) / 2},
                        new double[]{startY, startY + (endY - startY), startY + (endY - startY)}, 3);
                if (fillShape) {
                    gc.setFill(fillColor);
                    gc.fillPolygon(new double[]{startX, startX - (endX - startX) / 2, startX + (endX - startX) / 2},
                            new double[]{startY, startY + (endY - startY), startY + (endY - startY)}, 3);
                }
                break;
        }
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setFillShape(boolean fillShape, Color fillColor) {
        this.fillShape = fillShape;
        this.fillColor = fillColor;
    }
}