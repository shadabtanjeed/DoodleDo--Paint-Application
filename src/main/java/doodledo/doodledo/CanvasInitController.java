package doodledo.doodledo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;


public class CanvasInitController {
    public static Color globalCanvasColor;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ColorPicker canvasColorSelector;
    private MasterController masterController;


    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    public void continueToMainWindow(ActionEvent event) throws IOException {
        Color initCanvasColor = canvasColorSelector.getValue();
        globalCanvasColor = initCanvasColor;

        switchToMainWindow(event);
    }


    public void switchToMainWindow(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_window.fxml"));
        root = loader.load();
        MasterController masterController = loader.getController();
        masterController.initialize(null, null);
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1280, 720);


        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        // Add key combinations
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            KeyCombination exportPdfCombination = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
            KeyCombination undoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
            KeyCombination redoCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
            KeyCombination clearCombination = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            KeyCombination eraserCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
            KeyCombination brushCombination = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);


            if (saveCombination.match(keyEvent)) {
                masterController.saveSelected();
            } else if (exportPdfCombination.match(keyEvent)) {
                masterController.exportPDFAction();
            } else if (undoCombination.match(keyEvent)) {
                masterController.undoAction();
            } else if (redoCombination.match(keyEvent)) {
                masterController.redoAction();
            } else if (clearCombination.match(keyEvent)) {
                masterController.clearCanvas();
            } else if (eraserCombination.match(keyEvent)) {
                masterController.eraserSelected();
            } else if (brushCombination.match(keyEvent)) {
                masterController.brushSelected();
            }
        });

        stage.setOnCloseRequest((WindowEvent event2) -> {
            if (WindowController.closeConfirmation()) {
                event2.consume();
            }
        });
    }


}