package doodledo.doodledo;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;


public class MasterController implements Initializable {

    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    private StateHandler stateHandler;
    @FXML
    private Canvas canvas;
    private ToolbarHandler toolbarHandler;
    private WindowController windowController; // New instance variable
    @FXML
    private ComboBox<String> export_context_menu;

    private Color initPenColor = Color.BLUE;
    private Color initCanvasColor = Color.BLACK;

    @FXML
    private TextArea inputText;

    public double getBrushWidth() {
        return brushWidth.getValue();
    }

    public Color getColorPaletteValue() {
        return colorPalette.getValue();
    }

    public void saveCurrentState() {
        double x = canvas.getWidth();
        double y = canvas.getHeight();
        stateHandler.saveCurrentState();
    }

    public void addImage() {
        toolbarHandler.addImage();
    }

    @FXML
    public void addText() {
        // prompt the user for text
        TextInputDialog dialog = new TextInputDialog("Enter text");
        dialog.setTitle("Add Text");
        dialog.setHeaderText("Enter the text you want to add to the canvas:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            toolbarHandler.addText(text);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> export_dropdown_list = FXCollections.observableArrayList("Image", "PDF");
        export_context_menu.setItems(export_dropdown_list);
        windowController = new WindowController(); // Create a new WindowController instance
        toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this); // Pass the WindowController and MasterController instance to ToolbarHandler
        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
        toolbarHandler.setCanvasColor(initCanvasColor);
        stateHandler.saveCurrentState();

        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
        });

        colorPalette.setValue(initPenColor);
        toolbarHandler.updateSelectedColor(initPenColor);

        // Add action listener to export_context_menu
        export_context_menu.getSelectionModel().selectedItemProperty().addListener((Observable observable) -> {
            String selected = export_context_menu.getSelectionModel().getSelectedItem();
            if ("Image".equals(selected)) {
                saveSelected();
            } else if ("PDF".equals(selected)) {
                exportPDFAction();
            }
            // Clear the selection
            export_context_menu.getSelectionModel().clearSelection();
        });
    }

    @FXML
    public void brushSelected() {
        toolbarHandler.selectBrush(colorPalette.getValue());
    }

    @FXML
    public void eraserSelected() {
        toolbarHandler.selectEraser();
    }

    @FXML
    public void saveSelected() {
        FileHandler.saveImage(canvas);
    }

    @FXML
    public void clearCanvas() {
        toolbarHandler.clearCanvas();
    }

    @FXML
    public void undoAction() {
        stateHandler.undoAction();
    }

    @FXML
    public void redoAction() {
        stateHandler.redoAction();
    }

    public void exportPDFAction() {
        FileHandler.exportCanvasToPdf(canvas);
    }

    Canvas getCanvas() {
        return canvas;
    }
}