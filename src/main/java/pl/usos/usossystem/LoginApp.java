package pl.usos.usossystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.repository.StudentRepository;

public class LoginApp extends Application {

    private final StudentRepository studentRepository = new StudentRepository();

    @Override
    public void start(Stage stage) {
        Label title = new Label("Logowanie do Mini-USOS");
        title.getStyleClass().add("page-title");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Haslo");

        Button loginButton = new Button("Zaloguj");
        loginButton.setDefaultButton(true);

        loginButton.setOnAction(e -> {
            String login = loginField.getText().trim();
            String haslo = passwordField.getText().trim();

            if (login.equals("admin") && haslo.equals("admin")) {
                try {
                    new AdminApp().start(new Stage());
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            try {
                int indeks = Integer.parseInt(login);
                Student student = studentRepository.loginStudent(indeks, haslo);

                if (student != null) {
                    try {
                        new StudentPanelApp(student).start(new Stage());
                        stage.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    showAlert("Blad", "Niepoprawny login lub haslo.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Blad", "Login studenta powinien byc numerem indeksu albo uzyj admin/admin.");
            }
        });

        VBox root = new VBox(12, title, loginField, passwordField, loginButton);
        root.getStyleClass().addAll("page-root", "card-panel");
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 360, 240);
        AppTheme.apply(stage, scene);
        stage.setScene(scene);
        stage.setMinWidth(360);
        stage.setMinHeight(240);
        stage.setTitle("Logowanie");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
