import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;

public class WindowController implements Initializable {

    //public static boolean isSaved = true;
    public static boolean isSaved = false;
    // Static fields to hold references to instance variables
    private static Slider staticBrushWidth;
    private static Canvas staticCanvas;
    // Instance variables

    private boolean eraserSelected = false;

    //return true indicates that the program termination is way to go
    //return false refrains from termination
    public static boolean closeConfirmation() {
        if (!isSaved) {
            //create a confirmation box
            Alert close_alert = new Alert(Alert.AlertType.CONFIRMATION);
            close_alert.setHeaderText("Save Confirmation");
            close_alert.setContentText("Do you want save your progress?");

            //create buttons for confirmation box
            ButtonType save_alert_button = new ButtonType("Save");
            ButtonType dont_save_alert_button = new ButtonType("Don't Save");
            ButtonType cancel_alert_button = new ButtonType("Cancel");

            //add buttons to the confirmation box
            close_alert.getButtonTypes().setAll(save_alert_button, dont_save_alert_button, cancel_alert_button);

            //show the confirmation box
            Optional<ButtonType> result = close_alert.showAndWait();

            if (result.get() == save_alert_button) {
                //save the canvas
                SaveImage();
                return true;
            } else if (result.get() == dont_save_alert_button) {
                //exits the program
                System.exit(0);
                return true;
            } else if (result.get() == cancel_alert_button) {
                return true;
            }
        }
        return false;
    }

    private static void SaveImage() {
        FileChooser ImageSaver = new FileChooser();
        ImageSaver.setTitle("Save Image File");

        //image data types
        ImageSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG"));

        //show the file save location dialogue box
        File savefile = ImageSaver.showSaveDialog(null);
        if (savefile != null) {
            try {
                //writeable image format created as the canvas size
                WritableImage writableImage = new WritableImage((int) staticCanvas.getWidth(), (int) staticCanvas.getHeight());
                //snapshot of the canvas stored into writable image
                staticCanvas.snapshot(null, writableImage);
                //writeable image converted to regular image format
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

                ImageIO.write(renderedImage, "png", savefile);
                isSaved = true;
            } catch (IOException ex) {
                System.err.println("Unable to Save");
            }
        }
    }

    @FXML
    public void brushSelected(ActionEvent e) {
        eraserSelected = false;
    }

    @FXML
    public void eraserSelected(ActionEvent e) {
        eraserSelected = true;
        colorPalette.setValue(Color.WHITE);
    }

    @FXML
    public void saveSelected(ActionEvent e) {
        // save the canvas
        SaveImage();
    }
}
