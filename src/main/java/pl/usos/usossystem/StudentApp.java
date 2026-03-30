package pl.usos.usossystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.OcenaView;
import pl.usos.usossystem.model.Przedmiot;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.repository.OcenaRepository;
import pl.usos.usossystem.repository.PrzedmiotRepository;
import pl.usos.usossystem.repository.StudentRepository;

public class StudentApp extends Application {

    private final StudentRepository studentRepository = new StudentRepository();
    private final PrzedmiotRepository przedmiotRepository = new PrzedmiotRepository();
    private final OcenaRepository ocenaRepository = new OcenaRepository();

    private final TableView<Student> studentTable = new TableView<>();
    private final TableView<Przedmiot> przedmiotTable = new TableView<>();
    private final TableView<OcenaView> ocenaTable = new TableView<>();

    private final TextField studentIdField = new TextField();
    private final TextField imieField = new TextField();
    private final TextField nazwiskoField = new TextField();
    private final TextField indeksField = new TextField();

    private final TextField przedmiotIdField = new TextField();
    private final TextField przedmiotNazwaField = new TextField();

    private final TextField ocenaIdField = new TextField();
    private final ComboBox<Student> studentComboBox = new ComboBox<>();
    private final ComboBox<Przedmiot> przedmiotComboBox = new ComboBox<>();
    private final TextField ocenaField = new TextField();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        Tab studenciTab = new Tab("Studenci", createStudenciTab());
        Tab przedmiotyTab = new Tab("Przedmioty", createPrzedmiotyTab());
        Tab ocenyTab = new Tab("Oceny", createOcenyTab());

        studenciTab.setClosable(false);
        przedmiotyTab.setClosable(false);
        ocenyTab.setClosable(false);

        tabPane.getTabs().addAll(studenciTab, przedmiotyTab, ocenyTab);

        refreshAll();

        Scene scene = new Scene(tabPane, 950, 650);
        stage.setTitle("Mini-USOS");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStudenciTab() {
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> imieCol = new TableColumn<>("Imię");
        imieCol.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<Student, String> nazwiskoCol = new TableColumn<>("Nazwisko");
        nazwiskoCol.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        TableColumn<Student, Integer> indeksCol = new TableColumn<>("Indeks");
        indeksCol.setCellValueFactory(new PropertyValueFactory<>("indeks"));

        studentTable.getColumns().clear();
        studentTable.getColumns().addAll(idCol, imieCol, nazwiskoCol, indeksCol);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                studentIdField.setText(String.valueOf(selected.getId()));
                imieField.setText(selected.getImie());
                nazwiskoField.setText(selected.getNazwisko());
                indeksField.setText(String.valueOf(selected.getIndeks()));
            }
        });

        studentIdField.setEditable(false);

        Button addButton = new Button("Dodaj");
        Button updateButton = new Button("Edytuj");
        Button deleteButton = new Button("Usuń");
        Button clearButton = new Button("Wyczyść");

        addButton.setOnAction(e -> addStudent());
        updateButton.setOnAction(e -> updateStudent());
        deleteButton.setOnAction(e -> deleteStudent());
        clearButton.setOnAction(e -> clearStudentFields());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("ID:"), 0, 0);
        form.add(studentIdField, 1, 0);
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

        VBox root = new VBox(15, new Label("Zarządzanie studentami"), studentTable, form);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createPrzedmiotyTab() {
        TableColumn<Przedmiot, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Przedmiot, String> nazwaCol = new TableColumn<>("Nazwa przedmiotu");
        nazwaCol.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        przedmiotTable.getColumns().clear();
        przedmiotTable.getColumns().addAll(idCol, nazwaCol);
        przedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        przedmiotTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                przedmiotIdField.setText(String.valueOf(selected.getId()));
                przedmiotNazwaField.setText(selected.getNazwa());
            }
        });

        przedmiotIdField.setEditable(false);

        Button addButton = new Button("Dodaj");
        Button updateButton = new Button("Edytuj");
        Button deleteButton = new Button("Usuń");
        Button clearButton = new Button("Wyczyść");

        addButton.setOnAction(e -> addPrzedmiot());
        updateButton.setOnAction(e -> updatePrzedmiot());
        deleteButton.setOnAction(e -> deletePrzedmiot());
        clearButton.setOnAction(e -> clearPrzedmiotFields());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("ID:"), 0, 0);
        form.add(przedmiotIdField, 1, 0);
        form.add(new Label("Nazwa:"), 0, 1);
        form.add(przedmiotNazwaField, 1, 1);
        form.add(addButton, 0, 2);
        form.add(updateButton, 1, 2);
        form.add(deleteButton, 0, 3);
        form.add(clearButton, 1, 3);

        VBox root = new VBox(15, new Label("Zarządzanie przedmiotami"), przedmiotTable, form);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createOcenyTab() {
        TableColumn<OcenaView, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<OcenaView, String> imieCol = new TableColumn<>("Imię");
        imieCol.setCellValueFactory(new PropertyValueFactory<>("imie"));

        TableColumn<OcenaView, String> nazwiskoCol = new TableColumn<>("Nazwisko");
        nazwiskoCol.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));

        TableColumn<OcenaView, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<OcenaView, Double> ocenaCol = new TableColumn<>("Ocena");
        ocenaCol.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        ocenaTable.getColumns().clear();
        ocenaTable.getColumns().addAll(idCol, imieCol, nazwiskoCol, przedmiotCol, ocenaCol);
        ocenaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ocenaIdField.setEditable(false);

        Button addButton = new Button("Dodaj ocenę");
        Button updateButton = new Button("Edytuj ocenę");
        Button deleteButton = new Button("Usuń ocenę");
        Button clearButton = new Button("Wyczyść");

        addButton.setOnAction(e -> addOcena());
        updateButton.setOnAction(e -> updateOcena());
        deleteButton.setOnAction(e -> deleteOcena());
        clearButton.setOnAction(e -> clearOcenaFields());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("ID:"), 0, 0);
        form.add(ocenaIdField, 1, 0);

        form.add(new Label("Student:"), 0, 1);
        form.add(studentComboBox, 1, 1);

        form.add(new Label("Przedmiot:"), 0, 2);
        form.add(przedmiotComboBox, 1, 2);

        form.add(new Label("Ocena:"), 0, 3);
        form.add(ocenaField, 1, 3);

        form.add(addButton, 0, 4);
        form.add(updateButton, 1, 4);
        form.add(deleteButton, 0, 5);
        form.add(clearButton, 1, 5);

        ocenaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                ocenaIdField.setText(String.valueOf(selected.getId()));
                ocenaField.setText(String.valueOf(selected.getOcena()));

                Student foundStudent = studentRepository.getAllStudents().stream()
                        .filter(s -> s.getImie().equals(selected.getImie()) && s.getNazwisko().equals(selected.getNazwisko()))
                        .findFirst()
                        .orElse(null);

                Przedmiot foundPrzedmiot = przedmiotRepository.getAllPrzedmioty().stream()
                        .filter(p -> p.getNazwa().equals(selected.getPrzedmiot()))
                        .findFirst()
                        .orElse(null);

                studentComboBox.setValue(foundStudent);
                przedmiotComboBox.setValue(foundPrzedmiot);
            }
        });

        VBox root = new VBox(15, new Label("Oceny studentów"), ocenaTable, form);
        root.setPadding(new Insets(15));
        return root;
    }

    private void addStudent() {
        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        String indeksText = indeksField.getText().trim();

        if (imie.isEmpty() || nazwisko.isEmpty() || indeksText.isEmpty()) {
            showAlert("Błąd", "Wypełnij wszystkie pola studenta.");
            return;
        }

        try {
            int indeks = Integer.parseInt(indeksText);
            studentRepository.addStudent(imie, nazwisko, indeks);
            refreshAll();
            clearStudentFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Indeks musi być liczbą.");
        }
    }

    private void updateStudent() {
        if (studentIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz studenta z tabeli.");
            return;
        }

        try {
            int id = Integer.parseInt(studentIdField.getText());
            int indeks = Integer.parseInt(indeksField.getText());

            studentRepository.updateStudent(id, imieField.getText().trim(), nazwiskoField.getText().trim(), indeks);
            refreshAll();
            clearStudentFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Indeks musi być liczbą.");
        }
    }

    private void deleteStudent() {
        if (studentIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz studenta z tabeli.");
            return;
        }

        int id = Integer.parseInt(studentIdField.getText());
        studentRepository.deleteStudent(id);
        refreshAll();
        clearStudentFields();
    }

    private void addPrzedmiot() {
        String nazwa = przedmiotNazwaField.getText().trim();

        if (nazwa.isEmpty()) {
            showAlert("Błąd", "Wpisz nazwę przedmiotu.");
            return;
        }

        przedmiotRepository.addPrzedmiot(nazwa);
        refreshAll();
        clearPrzedmiotFields();
    }

    private void updatePrzedmiot() {
        if (przedmiotIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz przedmiot z tabeli.");
            return;
        }

        int id = Integer.parseInt(przedmiotIdField.getText());
        String nazwa = przedmiotNazwaField.getText().trim();

        if (nazwa.isEmpty()) {
            showAlert("Błąd", "Wpisz nazwę przedmiotu.");
            return;
        }

        przedmiotRepository.updatePrzedmiot(id, nazwa);
        refreshAll();
        clearPrzedmiotFields();
    }

    private void deletePrzedmiot() {
        if (przedmiotIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz przedmiot z tabeli.");
            return;
        }

        int id = Integer.parseInt(przedmiotIdField.getText());
        przedmiotRepository.deletePrzedmiot(id);
        refreshAll();
        clearPrzedmiotFields();
    }

    private void addOcena() {
        Student student = studentComboBox.getValue();
        Przedmiot przedmiot = przedmiotComboBox.getValue();
        String ocenaText = ocenaField.getText().trim();

        if (student == null || przedmiot == null || ocenaText.isEmpty()) {
            showAlert("Błąd", "Wybierz studenta, przedmiot i wpisz ocenę.");
            return;
        }

        try {
            double ocena = Double.parseDouble(ocenaText);
            ocenaRepository.addOcena(student.getId(), przedmiot.getId(), ocena);
            refreshAll();
            clearOcenaFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Ocena musi być liczbą.");
        }
    }

    private void updateOcena() {
        if (ocenaIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz ocenę z tabeli.");
            return;
        }

        Student student = studentComboBox.getValue();
        Przedmiot przedmiot = przedmiotComboBox.getValue();
        String ocenaText = ocenaField.getText().trim();

        if (student == null || przedmiot == null || ocenaText.isEmpty()) {
            showAlert("Błąd", "Wybierz studenta, przedmiot i wpisz ocenę.");
            return;
        }

        try {
            int id = Integer.parseInt(ocenaIdField.getText());
            double ocena = Double.parseDouble(ocenaText);

            ocenaRepository.updateOcena(id, student.getId(), przedmiot.getId(), ocena);
            refreshAll();
            clearOcenaFields();
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Ocena musi być liczbą.");
        }
    }

    private void deleteOcena() {
        if (ocenaIdField.getText().isEmpty()) {
            showAlert("Błąd", "Wybierz ocenę z tabeli.");
            return;
        }

        int id = Integer.parseInt(ocenaIdField.getText());
        ocenaRepository.deleteOcena(id);
        refreshAll();
        clearOcenaFields();
    }

    private void refreshAll() {
        studentTable.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        przedmiotTable.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));
        ocenaTable.setItems(FXCollections.observableArrayList(ocenaRepository.getAllOcenyView()));

        studentComboBox.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        przedmiotComboBox.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));
    }

    private void clearStudentFields() {
        studentIdField.clear();
        imieField.clear();
        nazwiskoField.clear();
        indeksField.clear();
        studentTable.getSelectionModel().clearSelection();
    }

    private void clearPrzedmiotFields() {
        przedmiotIdField.clear();
        przedmiotNazwaField.clear();
        przedmiotTable.getSelectionModel().clearSelection();
    }

    private void clearOcenaFields() {
        ocenaIdField.clear();
        ocenaField.clear();
        studentComboBox.setValue(null);
        przedmiotComboBox.setValue(null);
        ocenaTable.getSelectionModel().clearSelection();
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