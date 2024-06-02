package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public abstract class Tool {
    protected Canvas canvas;
    protected GraphicsContext graphicsContext;

    public Tool(Canvas canvas, GraphicsContext graphicsContext) {
        this.canvas = canvas;
        this.graphicsContext = graphicsContext;
    }

    public abstract void onMousePressed(MouseEvent event);
    public abstract void onMouseDragged(MouseEvent event);
    public abstract void onMouseReleased(MouseEvent event);
}