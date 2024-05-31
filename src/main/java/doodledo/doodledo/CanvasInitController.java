//package doodledo.doodledo;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.ColorPicker;
//import javafx.scene.image.Image;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//
//import java.io.IOException;
//import java.util.Objects;
//
//public class CanvasInitController {
//
//    private Stage stage;
//    private Scene scene;
//    private Parent root;
//
//    @FXML
//    private ColorPicker canvasColorSelector;
//    private MasterController masterController;
//
//    public CanvasInitController() {
//        // No new MasterController instance creation here
//    }
//
//    public void setMasterController(MasterController masterController) {
//        this.masterController = masterController;
//    }
//
//    public void continueToMainWindow(ActionEvent event) throws IOException {
//        Color initCanvasColor = canvasColorSelector.getValue();
//        System.out.println("Canvas color from picker: ");
//        System.out.println(initCanvasColor);
//
//        // Set the initial canvas color in the master controller
//        masterController.setInitCanvasColor(initCanvasColor);
//
//
//        masterController.updateCanvasColor();
//
//        // Print the initCanvasColor
//        initCanvasColor = masterController.getInitCanvasColor();
//        System.out.println("Canvas color from getter: ");
//        System.out.println(initCanvasColor);
//
//        switchToMainCanvas(event);
//    }
//
//
//    public void switchToMainCanvas(ActionEvent event) throws IOException {
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_window.fxml")));
//
//        scene = new Scene(root, 1080, 720);
//        stage.setTitle("DoodleDo");
////        stage.setMaximized(true);
//        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo.png")));
//        stage.getIcons().add(icon);
//
//        stage.setScene(scene);
//        stage.setMaximized(true);
//        stage.show();
//
//        stage.setOnCloseRequest((WindowEvent we) -> {
//            if (WindowController.closeConfirmation()) {
//                we.consume();
//            }
//        });
//    }
//
//    public void initialize() {
//        canvasColorSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
//            // Set the initial canvas color in the master controller
//            masterController.setInitCanvasColor(newValue);
//            // Update the canvas color
//            masterController.updateCanvasColor();
//        });
//    }
//}