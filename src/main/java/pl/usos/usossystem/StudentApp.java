package pl.usos.usossystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.repository.StudentRepository;

public class StudentApp extends Application {

    private final StudentRepository repository = new StudentRepository();
    private final TableView<Student> table = new TableView<>();

    @Override
    public void start(Stage stage) {
        Label title = new Label("System USOS - Studenci");

        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> imieCol = new TableColumn<>("Imię");
        imieCol.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<Student, String> nazwiskoCol = new TableColumn<>("Nazwisko");
        nazwiskoCol.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        TableColumn<Student, Integer> indeksCol = new TableColumn<>("Indeks");
        indeksCol.setCellValueFactory(new PropertyValueFactory<>("indeks"));

        table.getColumns().add(idCol);
        table.getColumns().add(imieCol);
        table.getColumns().add(nazwiskoCol);
        table.getColumns().add(indeksCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField imieField = new TextField();
        imieField.setPromptText("Imię");

        TextField nazwiskoField = new TextField();
        nazwiskoField.setPromptText("Nazwisko");

        TextField indeksField = new TextField();
        indeksField.setPromptText("Indeks");

        Button addButton = new Button("Dodaj studenta");

        addButton.setOnAction(e -> {
            String imie = imieField.getText().trim();
            String nazwisko = nazwiskoField.getText().trim();
            String indeksText = indeksField.getText().trim();

            if (imie.isEmpty() || nazwisko.isEmpty() || indeksText.isEmpty()) {
                showAlert("Błąd", "Wypełnij wszystkie pola.");
                return;
            }

            try {
                int indeks = Integer.parseInt(indeksText);
                repository.addStudent(imie, nazwisko, indeks);
                refreshTable();

                imieField.clear();
                nazwiskoField.clear();
                indeksField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Błąd", "Indeks musi być liczbą.");
            }
        });

        refreshTable();

        VBox root = new VBox(10, title, table, imieField, nazwiskoField, indeksField, addButton);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 700, 500);
        stage.setTitle("USOS - Studenci");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(repository.getAllStudents()));
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