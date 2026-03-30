package pl.usos.usossystem;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.repository.StudentRepository;

public class StudentApp extends Application {

    private final StudentRepository repository = new StudentRepository();
    private final TableView<Student> table = new TableView<>();

    private final TextField idField = new TextField();
    private final TextField imieField = new TextField();
    private final TextField nazwiskoField = new TextField();
    private final TextField indeksField = new TextField();

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

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedStudent) -> {
            if (selectedStudent != null) {
                idField.setText(String.valueOf(selectedStudent.getId()));
                imieField.setText(selectedStudent.getImie());
                nazwiskoField.setText(selectedStudent.getNazwisko());
                indeksField.setText(String.valueOf(selectedStudent.getIndeks()));
            }
        });

        idField.setPromptText("ID");
        idField.setEditable(false);

        imieField.setPromptText("Imię");
        nazwiskoField.setPromptText("Nazwisko");
        indeksField.setPromptText("Indeks");

        Button addButton = new Button("Dodaj");
        Button updateButton = new Button("Edytuj");
        Button deleteButton = new Button("Usuń");
        Button clearButton = new Button("Wyczyść");

        addButton.setOnAction(e -> addStudent());
        updateButton.setOnAction(e -> updateStudent());
        deleteButton.setOnAction(e -> deleteStudent());
        clearButton.setOnAction(e -> clearFields());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("ID:"), 0, 0);
        form.add(idField, 1, 0);

        form.add(new Label("Imię:"), 0, 1);
        form.add(imieField, 1, 1);

        form.add(new Label("Nazwisko:"), 0, 2);
        form.add(nazwiskoField, 1, 2);

        form.add(new Label("Indeks:"), 0, 3);
        form.add(indeksField, 1, 3);

        form.add(addButton, 0, 4);
        form.add(updateButton, 1, 4);
        form.add(deleteButton, 0, 5);
        form.add(clearButton, 1, 5);

        refreshTable();

        VBox root = new VBox(15, title, table, form);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("USOS - Studenci");
        stage.setScene(scene);
        stage.show();
    }

    private void addStudent() {
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
            clearFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Indeks musi być liczbą.");
        }
    }

    private void updateStudent() {
        String idText = idField.getText().trim();
        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        String indeksText = indeksField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Błąd", "Wybierz studenta z tabeli.");
            return;
        }

        if (imie.isEmpty() || nazwisko.isEmpty() || indeksText.isEmpty()) {
            showAlert("Błąd", "Wypełnij wszystkie pola.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            int indeks = Integer.parseInt(indeksText);

            repository.updateStudent(id, imie, nazwisko, indeks);
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "ID i indeks muszą być liczbami.");
        }
    }

    private void deleteStudent() {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            showAlert("Błąd", "Wybierz studenta z tabeli.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie");
        confirm.setHeaderText(null);
        confirm.setContentText("Czy na pewno chcesz usunąć studenta?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            int id = Integer.parseInt(idText);
            repository.deleteStudent(id);
            refreshTable();
            clearFields();
        }
    }

    private void clearFields() {
        idField.clear();
        imieField.clear();
        nazwiskoField.clear();
        indeksField.clear();
        table.getSelectionModel().clearSelection();
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