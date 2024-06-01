package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class ShapeTool extends Tool {

    private String shape;

    public ShapeTool(Canvas canvas, GraphicsContext graphicsContext, String shape) {
        super(canvas, graphicsContext);
        this.shape = shape;
    }

    @Override
    public void draw(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        switch (shape) {
            case "Circle":
                graphicsContext.strokeOval(x - 50, y - 50, 100, 100);
                break;
            case "Square":
                graphicsContext.strokeRect(x - 50, y - 50, 100, 100);
                break;
            case "Rectangle":
                graphicsContext.strokeRect(x - 50, y - 25, 100, 50);
                break;
            case "Ellipse":
                graphicsContext.strokeOval(x - 50, y - 25, 100, 50);
                break;
            case "Triangle":
                graphicsContext.strokePolygon(new double[]{x, x - 50, x + 50}, new double[]{y - 50, y + 50, y + 50}, 3);
                break;
        }
    }
}
