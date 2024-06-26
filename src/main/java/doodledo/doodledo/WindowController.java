package doodledo.doodledo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML
    private static Canvas canvas;
    private StateHandler stateHandler;
    private ToolbarHandler toolbarHandler;

    public static boolean closeConfirmation() {
        if (!FileHandler.getIsSaved()) {
            Alert close_alert = new Alert(Alert.AlertType.CONFIRMATION);
            close_alert.setHeaderText("Save Confirmation");
            close_alert.setContentText("Do you want save your progress?");

            ButtonType save_alert_button = new ButtonType("Save");
            ButtonType dont_save_alert_button = new ButtonType("Don't Save");
            ButtonType cancel_alert_button = new ButtonType("Cancel");

            close_alert.getButtonTypes().setAll(save_alert_button, dont_save_alert_button, cancel_alert_button);

            Optional<ButtonType> result = close_alert.showAndWait();

            if (result.get() == save_alert_button) {
                Canvas canvas1 = WindowController.getCanvas();
                FileHandler.saveImage(canvas1);
                return true;
            } else if (result.get() == dont_save_alert_button) {
                System.exit(0);
                return true;
            } else
                return result.get() == cancel_alert_button;
        }
        return false;
    }

    public static void setIsSaved(boolean b) {
        if (b) {
            System.out.println("Saved");
        } else {

        }
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(Canvas canvas) {
        WindowController.canvas = canvas;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Text hoveringText = new Text();
        hoveringText.setVisible(false);
        toolbarHandler = new ToolbarHandler(canvas, canvas.getGraphicsContext2D(), this, new MasterController(),
                hoveringText);

        stateHandler = new StateHandler(canvas, canvas.getGraphicsContext2D());
        stateHandler.saveCurrentState();
    }
}