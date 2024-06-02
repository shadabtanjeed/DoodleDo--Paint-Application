package doodledo.doodledo;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.kernel.geom.PageSize;

import java.io.FileOutputStream;

public class FileHandler {

    static boolean isSaved = false;

    public static void saveImage(Canvas canvas) {
        FileChooser imageSaver = new FileChooser();
        imageSaver.setTitle("Save Image File");
        imageSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG"),
                new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.JPEG")
        );


        File saveFile = imageSaver.showSaveDialog(null);
        if (saveFile != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", saveFile);


                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Image Export");
                alert2.setHeaderText(null);
                alert2.setContentText("Canvas exported to image successfully.");
                alert2.showAndWait();
            } catch (IOException ex) {
                System.err.println("Unable to Save");
            }
        }

        isSaved = true;
    }

    public static void exportCanvasToPdf(Canvas canvas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            try {
                saveCanvasToPdf(canvas, saveFile.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Export");
                alert.setHeaderText(null);
                alert.setContentText("Canvas exported to PDF successfully.");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        isSaved = true;
    }

    public static void saveCanvasToPdf(Canvas canvas, String pdfPath) throws IOException {
        // Convert canvas to image bytes
        byte[] imageBytes = canvasToImageBytes(canvas);

        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
        // Convert canvas size from pixels to points
        float widthInPoints = (float) canvas.getWidth() * 72 / 96; // 96 is the DPI for most screens
        float heightInPoints = (float) canvas.getHeight() * 72 / 96;
        PageSize pageSize = new PageSize(widthInPoints, heightInPoints);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(pageSize);
        Document document = new Document(pdfDocument);

        // Add image to the document
        ImageData imageData = ImageDataFactory.create(imageBytes);
        Image image = new Image(imageData);
        document.add(image);

        // Close the document
        document.close();
    }

    public static byte[] canvasToImageBytes(Canvas canvas) throws IOException {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean getIsSaved() {
        return isSaved;
    }

    public static void setIsSaved(boolean b) {
        isSaved = b;
    }
}
