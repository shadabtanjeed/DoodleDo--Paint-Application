package doodledo.doodledo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static MasterController masterController;

    public static void main(String[] args) {
        launch();
    }

    public static MasterController getUIController() {
        return masterController;
    }

    @Override
    public void start(Stage stage) throws IOException {
        masterController = new MasterController(); // Initialize MasterController here
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("CanvasInit.fxml"));

//        fxmlLoader.setController(masterController);

        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);

        stage.setTitle("DoodleDo");

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        stage.setOnCloseRequest((WindowEvent we) -> {
            if (WindowController.closeConfirmation()) {
                we.consume();
            }
        });
    }
}