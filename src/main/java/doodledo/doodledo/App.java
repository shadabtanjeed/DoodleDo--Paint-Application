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
    private static WindowController windowController;

    public static void main(String[] args) {
        launch();
    }

    public static MasterController getUIController() {
        return masterController;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        masterController = fxmlLoader.getController(); // Initialize MasterController here
        stage.setTitle("DoodleDo");
        stage.setMaximized(true);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest((WindowEvent event) -> {
            if (WindowController.closeConfirmation()) {
                event.consume();
            }
        });
    }
}