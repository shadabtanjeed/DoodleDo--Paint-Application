package doodledo.doodledo;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class CanvasInitController {

    @FXML
    private ColorPicker canvasColorSelector;
    private MasterController masterController;

    public void continueToMainWindow() {

        Color initCanvasColor = canvasColorSelector.getValue();
        masterController.setInitCanvasColor(initCanvasColor);

    }
}
