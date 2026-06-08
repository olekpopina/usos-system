package pl.usos.usossystem;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
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
    private static final double SCROLL_SPEED_MULTIPLIER = 2.8;

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

    public static void configureScrollPane(ScrollPane scrollPane) {
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() == 0 || scrollPane.getContent() == null) {
                return;
            }

            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double scrollableHeight = contentHeight - viewportHeight;

            if (scrollableHeight <= 0) {
                return;
            }

            double delta = (event.getDeltaY() * SCROLL_SPEED_MULTIPLIER) / scrollableHeight;
            double nextValue = clamp(scrollPane.getVvalue() - delta, 0.0, 1.0);
            scrollPane.setVvalue(nextValue);
            event.consume();
        });
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
