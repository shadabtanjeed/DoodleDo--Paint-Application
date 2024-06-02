package doodledo.doodledo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.Stack;

public class StateHandler {
    private Stack<WritableImage> undoStack = new Stack<>();
    private Stack<WritableImage> redoStack = new Stack<>();
    private Canvas canvas;
    private GraphicsContext brush;
    private boolean canvasIsSetUp = false;

    public StateHandler(Canvas canvas, GraphicsContext brush) {
        this.canvas = canvas;
        this.brush = brush;
    }

    public void saveCurrentState() {
        if (!canvasIsSetUp) {
            canvasIsSetUp = true;
            return;
        }
        redoStack.clear();
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);
        undoStack.push(snapshot);
    }

    public void undoAction() {
        if (!undoStack.isEmpty()) {
            WritableImage currentSnapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, currentSnapshot);
            redoStack.push(currentSnapshot);
            WritableImage previousSnapshot = undoStack.pop();
            brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            brush.drawImage(previousSnapshot, 0, 0);

        }
    }

    public void redoAction() {
        if (!redoStack.isEmpty()) {
            WritableImage currentSnapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, currentSnapshot);
            undoStack.push(currentSnapshot);
            WritableImage nextSnapshot = redoStack.pop();
            brush.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            brush.drawImage(nextSnapshot, 0, 0);

        }
    }
}
