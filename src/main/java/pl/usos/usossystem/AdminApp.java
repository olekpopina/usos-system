package pl.usos.usossystem;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Przedmiot;
import pl.usos.usossystem.model.Semestr;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentPrzedmiotView;
import pl.usos.usossystem.repository.OcenaRepository;
import pl.usos.usossystem.repository.PrzedmiotRepository;
import pl.usos.usossystem.repository.SemestrRepository;
import pl.usos.usossystem.repository.StudentRepository;
import pl.usos.usossystem.repository.StudentSemestrRepository;

public class AdminApp extends Application {

    private final StudentRepository studentRepository = new StudentRepository();
    private final PrzedmiotRepository przedmiotRepository = new PrzedmiotRepository();
    private final SemestrRepository semestrRepository = new SemestrRepository();
    private final StudentSemestrRepository studentSemestrRepository = new StudentSemestrRepository();
    private final OcenaRepository ocenaRepository = new OcenaRepository();

    private final TableView<Student> studentTable = new TableView<>();
    private final TableView<Przedmiot> przedmiotTable = new TableView<>();
    private final TableView<StudentPrzedmiotView> studentPrzedmiotTable = new TableView<>();

    private final TextField studentIdField = new TextField();
    private final TextField imieField = new TextField();
    private final TextField nazwiskoField = new TextField();
    private final TextField indeksField = new TextField();

    private final TextField przedmiotIdField = new TextField();
    private final TextField przedmiotNazwaField = new TextField();

    private final ComboBox<Student> studentCombo = new ComboBox<>();
    private final ComboBox<Semestr> semestrCombo = new ComboBox<>();
    private final ComboBox<Przedmiot> przedmiotCombo = new ComboBox<>();
    private final TextField ocenaField = new TextField();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        Tab studenciTab = new Tab("Studenci", createStudenciTab());
        Tab przedmiotyTab = new Tab("Przedmioty", createPrzedmiotyTab());
        Tab etapTab = new Tab("Etap studiów", createEtapTab());

        studenciTab.setClosable(false);
        przedmiotyTab.setClosable(false);
        etapTab.setClosable(false);

        tabPane.getTabs().addAll(studenciTab, przedmiotyTab, etapTab);

        refreshAll();

        Scene scene = new Scene(tabPane, 1100, 760);
        stage.setTitle("Mini-USOS - Panel dziekanatu");
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

        TableColumn<Student, String> semestrCol = new TableColumn<>("Aktualny semestr");
        semestrCol.setCellValueFactory(new PropertyValueFactory<>("aktualnySemestrNazwa"));

        TableColumn<Student, String> statusCol = new TableColumn<>("Status semestru");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusSemestru"));

        studentTable.getColumns().clear();
        studentTable.getColumns().addAll(idCol, imieCol, nazwiskoCol, indeksCol, semestrCol, statusCol);
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

        VBox root = new VBox(15,
                new Label("Zarządzanie studentami"),
                studentTable,
                form
        );
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

        VBox root = new VBox(15,
                new Label("Zarządzanie przedmiotami"),
                przedmiotTable,
                form
        );
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createEtapTab() {
        Label opis = new Label("Obsługa semestrów, ocen i rejestracji studenta");

        Button ustawSemestrButton = new Button("Ustaw studentowi aktualny semestr");
        Button przypiszPrzedmiotDoSemestruButton = new Button("Przypisz przedmiot do semestru");
        Button przypiszPrzedmiotyStudentowiButton = new Button("Przypisz studentowi przedmioty z semestru");
        Button dodajOceneButton = new Button("Dodaj ocenę");
        Button zaliczRecznieButton = new Button("Zalicz semestr ręcznie");
        Button kolejnySemestrButton = new Button("Rejestruj na kolejny semestr");
        Button pokazPrzedmiotyButton = new Button("Pokaż przedmioty studenta");

        ustawSemestrButton.setOnAction(e -> ustawAktualnySemestrStudenta());
        przypiszPrzedmiotDoSemestruButton.setOnAction(e -> przypiszPrzedmiotDoSemestru());
        przypiszPrzedmiotyStudentowiButton.setOnAction(e -> przypiszStudentowiPrzedmiotyZSemestru());
        dodajOceneButton.setOnAction(e -> dodajOcene());
        zaliczRecznieButton.setOnAction(e -> zaliczSemestrRecznie());
        kolejnySemestrButton.setOnAction(e -> rejestrujNaKolejnySemestr());
        pokazPrzedmiotyButton.setOnAction(e -> pokazPrzedmiotyStudenta());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Student:"), 0, 0);
        form.add(studentCombo, 1, 0);

        form.add(new Label("Semestr:"), 0, 1);
        form.add(semestrCombo, 1, 1);

        form.add(new Label("Przedmiot:"), 0, 2);
        form.add(przedmiotCombo, 1, 2);

        form.add(new Label("Ocena:"), 0, 3);
        form.add(ocenaField, 1, 3);

        form.add(ustawSemestrButton, 0, 4);
        form.add(przypiszPrzedmiotDoSemestruButton, 1, 4);
        form.add(przypiszPrzedmiotyStudentowiButton, 0, 5);
        form.add(dodajOceneButton, 1, 5);
        form.add(zaliczRecznieButton, 0, 6);
        form.add(kolejnySemestrButton, 1, 6);
        form.add(pokazPrzedmiotyButton, 0, 7);

        TableColumn<StudentPrzedmiotView, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentPrzedmiotView, String> semestrCol = new TableColumn<>("Semestr");
        semestrCol.setCellValueFactory(new PropertyValueFactory<>("semestr"));

        TableColumn<StudentPrzedmiotView, String> ocenaCol = new TableColumn<>("Ocena");
        ocenaCol.setCellValueFactory(cell -> {
            Double value = cell.getValue().getOcena();
            return new ReadOnlyStringWrapper(value == null ? "-" : String.valueOf(value));
        });

        TableColumn<StudentPrzedmiotView, String> statusCol = new TableColumn<>("Przedmiot zaliczony");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentPrzedmiotTable.getColumns().clear();
        studentPrzedmiotTable.getColumns().addAll(przedmiotCol, semestrCol, ocenaCol, statusCol);
        studentPrzedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(15, opis, form, new Label("Przedmioty i oceny wybranego studenta"), studentPrzedmiotTable);
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

        String nazwa = przedmiotNazwaField.getText().trim();
        if (nazwa.isEmpty()) {
            showAlert("Błąd", "Wpisz nazwę przedmiotu.");
            return;
        }

        int id = Integer.parseInt(przedmiotIdField.getText());
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

    private void ustawAktualnySemestrStudenta() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Błąd", "Wybierz studenta i semestr.");
            return;
        }

        studentRepository.setStudentSemestr(student.getId(), semestr.getId());
        refreshAll();
        showInfo("OK", "Ustawiono aktualny semestr studentowi.");
    }

    private void przypiszPrzedmiotDoSemestru() {
        Semestr semestr = semestrCombo.getValue();
        Przedmiot przedmiot = przedmiotCombo.getValue();

        if (semestr == null || przedmiot == null) {
            showAlert("Błąd", "Wybierz semestr i przedmiot.");
            return;
        }

        studentSemestrRepository.przypiszPrzedmiotDoSemestru(semestr.getId(), przedmiot.getId());
        showInfo("OK", "Przedmiot przypisany do semestru.");
    }

    private void przypiszStudentowiPrzedmiotyZSemestru() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Błąd", "Wybierz studenta i semestr.");
            return;
        }

        studentSemestrRepository.przypiszPrzedmiotySemestruStudentowi(student.getId(), semestr.getId());
        pokazPrzedmiotyStudenta();
        showInfo("OK", "Student otrzymał przedmioty z wybranego semestru.");
    }

    private void dodajOcene() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();
        Przedmiot przedmiot = przedmiotCombo.getValue();
        String ocenaText = ocenaField.getText().trim();

        if (student == null || semestr == null || przedmiot == null || ocenaText.isEmpty()) {
            showAlert("Błąd", "Wybierz studenta, semestr, przedmiot i wpisz ocenę.");
            return;
        }

        try {
            double ocena = Double.parseDouble(ocenaText);

            ocenaRepository.addOcena(student.getId(), przedmiot.getId(), semestr.getId(), ocena);

            studentSemestrRepository.oznaczPrzedmiotyJakoZaliczoneJesliMajaOceny(student.getId(), semestr.getId());

            boolean komplet = studentSemestrRepository.czyStudentMaWszystkieOcenyWSemestrze(student.getId(), semestr.getId());
            if (komplet) {
                studentRepository.setStatusSemestru(student.getId(), "Zaliczony automatycznie");
            }

            refreshAll();
            pokazPrzedmiotyStudenta();
            ocenaField.clear();
            showInfo("OK", "Dodano ocenę.");
        } catch (NumberFormatException ex) {
            showAlert("Błąd", "Ocena musi być liczbą.");
        }
    }

    private void zaliczSemestrRecznie() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Błąd", "Wybierz studenta i semestr.");
            return;
        }

        studentRepository.setStatusSemestru(student.getId(), "Zaliczony ręcznie");
        refreshAll();
        showInfo("OK", "Semestr oznaczono jako zaliczony ręcznie.");
    }

    private void rejestrujNaKolejnySemestr() {
        Student student = studentCombo.getValue();
        Semestr current = semestrCombo.getValue();

        if (student == null || current == null) {
            showAlert("Błąd", "Wybierz studenta i aktualny semestr.");
            return;
        }

        boolean komplet = studentSemestrRepository.czyStudentMaWszystkieOcenyWSemestrze(student.getId(), current.getId());

        if (komplet) {
            studentRepository.setStatusSemestru(student.getId(), "Zaliczony automatycznie");
        } else {
            Student aktualnyStudent = studentRepository.getAllStudents().stream()
                    .filter(s -> s.getId() == student.getId())
                    .findFirst()
                    .orElse(null);

            if (aktualnyStudent == null || aktualnyStudent.getStatusSemestru() == null ||
                    !aktualnyStudent.getStatusSemestru().equals("Zaliczony ręcznie")) {
                showAlert("Błąd", "Student nie ma kompletu ocen ani ręcznie zaliczonego semestru.");
                return;
            }
        }

        Semestr next = semestrRepository.getNextSemestr(current.getNumer());
        if (next == null) {
            showAlert("Informacja", "Brak kolejnego semestru w bazie.");
            return;
        }

        studentRepository.setStudentSemestr(student.getId(), next.getId());
        studentSemestrRepository.przypiszPrzedmiotySemestruStudentowi(student.getId(), next.getId());

        refreshAll();
        pokazPrzedmiotyStudenta();
        showInfo("OK", "Student został zarejestrowany na kolejny semestr: " + next.getNazwa());
    }

    private void pokazPrzedmiotyStudenta() {
        Student student = studentCombo.getValue();

        if (student == null) {
            showAlert("Błąd", "Wybierz studenta.");
            return;
        }

        studentPrzedmiotTable.setItems(
                FXCollections.observableArrayList(
                        studentSemestrRepository.getStudentDashboardData(student.getId())
                )
        );
    }

    private void refreshAll() {
        studentTable.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        przedmiotTable.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));

        studentCombo.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        semestrCombo.setItems(FXCollections.observableArrayList(semestrRepository.getAllSemestry()));
        przedmiotCombo.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}