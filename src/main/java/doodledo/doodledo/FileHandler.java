package doodledo.doodledo;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class FileHandler {
    public static void saveImage(Canvas canvas) {
        FileChooser imageSaver = new FileChooser();
        imageSaver.setTitle("Save Image File");
        imageSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG"));

        File saveFile = imageSaver.showSaveDialog(null);
        if (saveFile != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", saveFile);
                WindowController.setIsSaved(true);
            } catch (IOException ex) {
                System.err.println("Unable to Save");
            }
        }
    }
}
