package pl.usos.usossystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.repository.StudentRepository;

public class LoginApp extends Application {

    private final StudentRepository studentRepository = new StudentRepository();

    @Override
    public void start(Stage stage) {
        Label title = new Label("Logowanie do Mini-USOS");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");

        Button loginButton = new Button("Zaloguj");

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
                    showAlert("Błąd", "Niepoprawny login lub hasło.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Błąd", "Login studenta powinien być numerem indeksu albo użyj admin/admin.");
            }
        });

        VBox root = new VBox(12, title, loginField, passwordField, loginButton);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 320, 220));
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