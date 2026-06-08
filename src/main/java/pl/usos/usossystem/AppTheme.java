package pl.usos.usossystem;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public final class AppTheme {

    private static final String STYLESHEET = Objects.requireNonNull(
            AppTheme.class.getResource("/pl/usos/usossystem/styles/app.css"),
            "Missing app stylesheet"
    ).toExternalForm();

    private static final Image APP_ICON = new Image(Objects.requireNonNull(
            AppTheme.class.getResourceAsStream("/pl/usos/usossystem/assets/usos-icon.png"),
            "Missing app icon"
    ));

    private AppTheme() {
    }

    public static void apply(Stage stage, Scene scene) {
        if (!scene.getStylesheets().contains(STYLESHEET)) {
            scene.getStylesheets().add(STYLESHEET);
        }
        if (stage.getIcons().isEmpty()) {
            stage.getIcons().add(APP_ICON);
        }
        if (scene.getWidth() >= 1200) {
            stage.setMinWidth(1180);
            stage.setMinHeight(820);
        }
    }
}
