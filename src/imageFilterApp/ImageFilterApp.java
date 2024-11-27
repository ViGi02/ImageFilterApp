/**
 * ImageFilterApp handles the graphical user interface and image processing logic
 * for applying filters to images.
 * 
 * @author Unathi Vayeke
 */
package imageFilterApp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageFilterApp {
    private ImageView imgView;
    private Image img;
    private ComboBox<String> filterDropdown;
    private String fileName;

    /**
     * Default constructor for ImageFilterApp.
     */
    public ImageFilterApp() {
        imgView = new ImageView();
    }

    /**
     * Creating and returning the root layout for the application.
     * 
     * @return The root layout (BorderPane) for the application.
     */
    public BorderPane createRoot(Stage primaryStage) {
        BorderPane root = new BorderPane();

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setStyle("-fx-padding: 20;");

        Button uploadButton = new Button("Upload Image");
        Button saveButton = new Button("Save Image");
        Button exitButton = new Button("Exit");
        filterDropdown = new ComboBox<>();
        filterDropdown.getItems().addAll("None", "Grayscale", "Invert Colors", "Sepia");
        filterDropdown.setValue("None");

        uploadButton.getStyleClass().add("button");
        saveButton.getStyleClass().add("button-save");
        exitButton.getStyleClass().add("button-exit");
        filterDropdown.getStyleClass().add("combo-box");

        imgView.setPreserveRatio(true);
        imgView.setFitWidth(700);
        imgView.setFitHeight(500);
        imgView.getStyleClass().add("image-view");

        uploadButton.setOnAction(e -> chooseImage(primaryStage));
        saveButton.setOnAction(e -> saveImage(primaryStage));
        exitButton.setOnAction(e -> System.exit(0));
        filterDropdown.setOnAction(e -> applyFilter(filterDropdown.getValue()));

        btnBox.getChildren().addAll(uploadButton, filterDropdown, saveButton, exitButton);
        root.setTop(btnBox);
        root.setCenter(imgView);

        return root;
    }

    /**
     * Opening a file chooser to select an image and store the filename.
     * 
     * @param stage The primary stage of the application.
     */
    private void chooseImage(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                img = new Image(new FileInputStream(file));
                imgView.setImage(img);
                //updateBackgroundBasedOnImage(root);
                fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            } catch (IOException e) {
                showAlert("Error", "Could not load image!");
            }
        }
    }

    /**
     * Applying the selected filter to the image.
     * 
     * @param filter The name of the filter to apply.
     */
    private void applyFilter(String filter) {
        if (img == null) {
            showAlert("Warning", "Please upload an image first!");
            return;
        }

        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
            BufferedImage filteredImage;

            switch (filter) {
                case "Grayscale":
                    filteredImage = applyGrayscale(bufferedImage);
                    break;
                case "Invert Colors":
                    filteredImage = invertColors(bufferedImage);
                    break;
                case "Sepia":
                    filteredImage = applySepia(bufferedImage);
                    break;
                default:
                    filteredImage = bufferedImage;
            }

            imgView.setImage(SwingFXUtils.toFXImage(filteredImage, null));
        } catch (Exception e) {
            showAlert("Error", "Could not apply filter!");
        }
    }

    /**
     * Saving the filtered image to a file.
     * 
     * @param stage The primary stage of the application.
     */
    private void saveImage(Stage stage) {
        if (imgView.getImage() == null) {
            showAlert("Warning", "No image to save!");
            return;
        }

        if (fileName == null || fileName.isEmpty()) {
            showAlert("Warning", "No original image to save!");
            return;
        }

        String filterName = filterDropdown.getValue();

        String newFileName = fileName + " " + filterName + ".png";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(newFileName);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imgView.getImage(), null);
                ImageIO.write(bufferedImage, "png", file);
                showAlert("Success", "Image saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Could not save image!");
            }
        }
    }
    /*
    /**
     * Extracting the dominant color from the given image by calculating the average 
 * red, green, and blue values of all pixels and ensuring that the values are within 
 * the valid RGB range (0 to 255).
 * 
 * @param image The image from which the dominant color is to be extracted.
 * @return The dominant color of the image as a {@link java.awt.Color}.
 *
private Color getDominantColor(BufferedImage image) {
    long r = 0, g = 0, b = 0;
    int width = image.getWidth();
    int height = image.getHeight();
    int totalPixels = width * height;

    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            Color pixelColor = new Color(image.getRGB(x, y));
            r += pixelColor.getRed();
            g += pixelColor.getGreen();
            b += pixelColor.getBlue();
        }
    }

    r = Math.min(255, Math.max(0, (int) (r / totalPixels)));
    g = Math.min(255, Math.max(0, (int) (g / totalPixels)));
    b = Math.min(255, Math.max(0, (int) (b / totalPixels)));

    return new Color(r, g, b); 
}

    /**
     * Updating the background color of the application based on the dominant color
     * of the currently loaded image.
     * 
     * @param root The root layout of the application (BorderPane) that will have its background color updated.
     *
    private void updateBackgroundBasedOnImage(BorderPane root) {
        if (img == null) return;

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
        Color dominantColor = getDominantColor(bufferedImage);

        javafx.scene.paint.Color javafxColor = javafx.scene.paint.Color.rgb(dominantColor.getRed(), dominantColor.getGreen(), dominantColor.getBlue());

        root.setStyle("-fx-background-color: #" + colorToHex(javafxColor) + ";");
    }

    /**
     * Converting a {@link javafx.scene.paint.Color} to a hexadecimal string.
     * This is used to format the JavaFX color into a CSS-friendly hex color code.
     * 
     * @param color The JavaFX color to convert.
     * @return The hexadecimal representation of the color as a string (e.g., "FF5733").
     *
    private String colorToHex(javafx.scene.paint.Color color) {
        return String.format("%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }*/

    /**
     * Applying a grayscale filter to the image.
     * 
     * @param img The original image.
     * @return The filtered image.
     */
    private BufferedImage applyGrayscale(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;
                int newPixel = (a << 24) | (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    /**
     * Applying an invert colors filter to the image.
     * 
     * @param img The original image.
     * @return The filtered image.
     */
    private BufferedImage invertColors(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int newPixel = (a << 24) | ((255 - r) << 16) | ((255 - g) << 8) | (255 - b);
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    /**
     * Applying a sepia filter to the image.
     * 
     * @param img The original image.
     * @return The filtered image.
     */
    private BufferedImage applySepia(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                r = Math.min(255, tr);
                g = Math.min(255, tg);
                b = Math.min(255, tb);
                int newPixel = (a << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newPixel);
            }
        }
        return result;
    }

    /**
     * Displaying an alert dialog with the given title and message.
     * 
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}