package doodledo.doodledo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static MasterController masterController;
    private static CanvasInitController canvasInitController;

    public static void main(String[] args) {
        launch();
    }


    public static CanvasInitController getCanvasInitController() {
        return canvasInitController;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("CanvasInit.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 150);
        canvasInitController = fxmlLoader.getController();
        masterController = new MasterController();
        canvasInitController.setMasterController(masterController);
        stage.setTitle("DoodleDo");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();

    }
}