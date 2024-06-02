package doodledo.doodledo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.TextInputDialog;


import java.util.Optional;


import static doodledo.doodledo.CanvasInitController.globalCanvasColor;

public class MasterController implements Initializable {

    @FXML
    private ComboBox<String> shape_dropdown;

    @FXML
    private ColorPicker colorPalette;
    @FXML
    private Slider brushWidth;
    private StateHandler stateHandler;
    @FXML
    private Canvas canvas;
    private ToolbarHandler toolbarHandler;
    private WindowController windowController;
    @FXML
    private ComboBox<String> export_context_menu;

    private Color initBrushColor = Color.BLUE;
    private Color initCanvasColor = globalCanvasColor;

    @FXML
    private TextArea inputText;

    @FXML
    private Text hoveringText;

    @FXML
    private Button brushTool;
    @FXML
    private Button soft_brush;
    @FXML
    private Button highlighter_brush;
    @FXML
    private Button addTxt;
    @FXML
    private Button square_tool;
    @FXML
    private Button rectangle_tool;
    @FXML
    private Button circle_tool;
    @FXML
    private Button ellipse_tool;
    @FXML
    private Button triangle_tool;
    @FXML
    private Button line_tool;
    @FXML
    private Button eraserTool;
    @FXML
    private Button fill_tool;
    @FXML
    private Button save_button;
    @FXML
    private Button export_pdf_button;

    @FXML
    private Button addImg;

    @FXML
    private Button clearButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    public double getBrushWidth() {
        return brushWidth.getValue();
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

        windowController = new WindowController();
        this.toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), windowController, this,
                hoveringText);
        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
        stateHandler.saveCurrentState();

        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
        });

        if (initCanvasColor != null) {
            Platform.runLater(() -> toolbarHandler.setCanvasColor(initCanvasColor));
        }

        colorPalette.setValue(initBrushColor);
        toolbarHandler.updateSelectedColor(initBrushColor);

        // listener for color palette
        colorPalette.valueProperty().addListener((observable, oldValue, newValue) -> {
            toolbarHandler.updateSelectedColor(newValue);
            if (toolbarHandler.softBrushSelected) {
                toolbarHandler.selectSoftBrush(newValue);
            }
            if (toolbarHandler.highlighterSelected) {
                toolbarHandler.selectHighLighter(newValue);
            }


        });

        // Add a change listener to the brushWidth
        brushWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toolbarHandler.softBrushSelected) {
                toolbarHandler.selectSoftBrush(colorPalette.getValue());
            }

            if (toolbarHandler.highlighterSelected) {
                toolbarHandler.selectHighLighter(colorPalette.getValue());
            }

            if (toolbarHandler.eraserSelected) {
                toolbarHandler.selectEraser();
            }

            if (!toolbarHandler.eraserSelected) {
                toolbarHandler.updateSelectedColor(colorPalette.getValue());
            }

        });

        hoveringText = new Text();
        hoveringText.setVisible(false);


        WindowController.setCanvas(this.getCanvas());


        colorPalette.setOnAction(event -> {
            Color selectedColor = colorPalette.getValue();
            toolbarHandler.updateSelectedColor(selectedColor);
            if (toolbarHandler.currentTool instanceof ShapeTool) {
                ((ShapeTool) toolbarHandler.currentTool).setStrokeColor(selectedColor);
            }
        });

        brushWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toolbarHandler.currentTool instanceof ShapeTool) {
                ((ShapeTool) toolbarHandler.currentTool).setStrokeWidth(newValue.doubleValue());
            }
        });

        Tooltip brushToolTooltip = new Tooltip("Pen Tool");
        Tooltip.install(brushTool, brushToolTooltip);

        Tooltip softBrushTooltip = new Tooltip("Soft Brush Tool");
        Tooltip.install(soft_brush, softBrushTooltip);

        Tooltip highlighterTooltip = new Tooltip("Highlighter Tool");
        Tooltip.install(highlighter_brush, highlighterTooltip);

        Tooltip addTxtTooltip = new Tooltip("Add Text Tool");
        Tooltip.install(addTxt, addTxtTooltip);

        Tooltip squareTooltip = new Tooltip("Square Tool");
        Tooltip.install(square_tool, squareTooltip);

        Tooltip rectangleTooltip = new Tooltip("Rectangle Tool");
        Tooltip.install(rectangle_tool, rectangleTooltip);

        Tooltip circleTooltip = new Tooltip("Circle Tool");
        Tooltip.install(circle_tool, circleTooltip);

        Tooltip ellipseTooltip = new Tooltip("Ellipse Tool");
        Tooltip.install(ellipse_tool, ellipseTooltip);

        Tooltip triangleTooltip = new Tooltip("Triangle Tool");
        Tooltip.install(triangle_tool, triangleTooltip);

        Tooltip lineTooltip = new Tooltip("Line Tool");
        Tooltip.install(line_tool, lineTooltip);

        Tooltip eraserTooltip = new Tooltip("Eraser Tool");
        Tooltip.install(eraserTool, eraserTooltip);

        Tooltip fillTooltip = new Tooltip("Fill Tool");
        Tooltip.install(fill_tool, fillTooltip);

        Tooltip colorPaletteTooltip = new Tooltip("Color Palette");
        Tooltip.install(colorPalette, colorPaletteTooltip);

        Tooltip brushWidthTooltip = new Tooltip("Brush Width");
        Tooltip.install(brushWidth, brushWidthTooltip);

        Tooltip inputTextTooltip = new Tooltip("Input Text");
        Tooltip.install(inputText, inputTextTooltip);

        Tooltip saveimageTooltip = new Tooltip("Save Image");
        Tooltip.install(save_button, saveimageTooltip);

        Tooltip clearCanvasTooltip = new Tooltip("Clear Canvas");
        Tooltip.install(clearButton, clearCanvasTooltip);

        Tooltip undoTooltip = new Tooltip("Undo");
        Tooltip.install(undoButton, undoTooltip);

        Tooltip redoTooltip = new Tooltip("Redo");
        Tooltip.install(redoButton, redoTooltip);

        Tooltip exportTooltip = new Tooltip("Export to PDF");
        Tooltip.install(export_pdf_button, exportTooltip);

        Tooltip exportInsertImage = new Tooltip("Insert image ");
        Tooltip.install(addImg, exportInsertImage);


    }

    @FXML
    public void brushSelected() {
        resetButtonStyles();
        brushTool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectBrush(colorPalette.getValue());
    }

    @FXML
    public void eraserSelected() {
        resetButtonStyles();
        eraserTool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectEraser();
    }

    @FXML
    public void softBrushSelected() {
        resetButtonStyles();
        soft_brush.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectSoftBrush(colorPalette.getValue());
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

    @FXML
    public void exportPDFAction() {
        FileHandler.exportCanvasToPdf(canvas);
    }


    public Canvas getCanvas() {
        return canvas;
    }

    @FXML
    public void HighLighterSelected() {
        resetButtonStyles();
        highlighter_brush.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectHighLighter(colorPalette.getValue());
    }

    @FXML
    public void selectShape() {
        String selectedShape = shape_dropdown.getSelectionModel().getSelectedItem();
        Color selectedColor = colorPalette.getValue();
        double selectedWidth = getBrushWidth();
        toolbarHandler.selectShape(selectedShape, selectedColor, selectedWidth);
    }

    @FXML
    public void fillShape() {
        resetButtonStyles();
        fill_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.fillShape();
    }

    @FXML
    public void rectangleSelected() {
        resetButtonStyles();
        rectangle_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Rectangle", colorPalette.getValue(), getBrushWidth());
    }

    @FXML
    public void circleSelected() {
        resetButtonStyles();
        circle_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Circle", colorPalette.getValue(), getBrushWidth());
    }

    @FXML
    public void squareSelected() {
        resetButtonStyles();
        square_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Square", colorPalette.getValue(), getBrushWidth());
    }

    @FXML
    public void ellipseSelected() {
        resetButtonStyles();
        ellipse_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Ellipse", colorPalette.getValue(), getBrushWidth());
    }

    @FXML
    public void triangleSelected() {
        resetButtonStyles();
        triangle_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Triangle", colorPalette.getValue(), getBrushWidth());
    }

    @FXML
    public void lineSelected() {
        resetButtonStyles();
        line_tool.setStyle("-fx-background-color: #DEE6C4; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;");
        toolbarHandler.selectShape("Line", colorPalette.getValue(), getBrushWidth());
    }

    private void resetButtonStyles() {
        String resetStyle = "-fx-background-color: white; -fx-border-color: #4682B4; -fx-border-radius: 5; -fx-border-width: 1.5;";
        brushTool.setStyle(resetStyle);
        soft_brush.setStyle(resetStyle);
        highlighter_brush.setStyle(resetStyle);
        addTxt.setStyle(resetStyle);
        square_tool.setStyle(resetStyle);
        rectangle_tool.setStyle(resetStyle);
        circle_tool.setStyle(resetStyle);
        ellipse_tool.setStyle(resetStyle);
        triangle_tool.setStyle(resetStyle);
        line_tool.setStyle(resetStyle);
        eraserTool.setStyle(resetStyle);
        fill_tool.setStyle(resetStyle);
    }

}