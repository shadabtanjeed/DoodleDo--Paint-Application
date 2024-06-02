package doodledo.doodledo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
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
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_window.fxml")));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1920, 980);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        stage.setOnCloseRequest((WindowEvent event2) -> {
            if (WindowController.closeConfirmation()) {
                event2.consume();
            }
        });
    }


}