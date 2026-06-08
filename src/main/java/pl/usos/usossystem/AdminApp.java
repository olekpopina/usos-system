package pl.usos.usossystem;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import pl.usos.usossystem.service.SemesterCompletionService;
import pl.usos.usossystem.service.SemesterDecision;

import java.util.List;

public class AdminApp extends Application {

    private final StudentRepository studentRepository = new StudentRepository();
    private final PrzedmiotRepository przedmiotRepository = new PrzedmiotRepository();
    private final SemestrRepository semestrRepository = new SemestrRepository();
    private final StudentSemestrRepository studentSemestrRepository = new StudentSemestrRepository();
    private final OcenaRepository ocenaRepository = new OcenaRepository();
    private final SemesterCompletionService semesterCompletionService = new SemesterCompletionService();

    private final TableView<Student> studentTable = new TableView<>();
    private final TableView<Przedmiot> przedmiotTable = new TableView<>();
    private final TableView<Przedmiot> semestrPrzedmiotTable = new TableView<>();
    private final TableView<StudentPrzedmiotView> studentPrzedmiotTable = new TableView<>();

    private final TextField studentIdField = new TextField();
    private final TextField imieField = new TextField();
    private final TextField nazwiskoField = new TextField();
    private final TextField indeksField = new TextField();

    private final TextField przedmiotIdField = new TextField();
    private final TextField przedmiotNazwaField = new TextField();
    private final TextField przedmiotEctsField = new TextField();

    private final ComboBox<Student> studentCombo = new ComboBox<>();
    private final ComboBox<Semestr> semestrCombo = new ComboBox<>();
    private final ComboBox<Przedmiot> przedmiotCombo = new ComboBox<>();
    private final TextField ocenaField = new TextField();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        Tab studenciTab = new Tab("Studenci", createStudenciTab());
        Tab przedmiotyTab = new Tab("Przedmioty", createPrzedmiotyTab());
        Tab etapTab = new Tab("Etap studiow", createEtapTab());

        studenciTab.setClosable(false);
        przedmiotyTab.setClosable(false);
        etapTab.setClosable(false);

        tabPane.getTabs().addAll(studenciTab, przedmiotyTab, etapTab);

        refreshAll();

        Scene scene = new Scene(tabPane, 1180, 820);
        stage.setTitle("Mini-USOS - Panel dziekanatu");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStudenciTab() {
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> imieCol = new TableColumn<>("Imie");
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
        Button deleteButton = new Button("Usun");
        Button clearButton = new Button("Wyczysc");

        addButton.setOnAction(e -> addStudent());
        updateButton.setOnAction(e -> updateStudent());
        deleteButton.setOnAction(e -> deleteStudent());
        clearButton.setOnAction(e -> clearStudentFields());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("ID:"), 0, 0);
        form.add(studentIdField, 1, 0);
        form.add(new Label("Imie:"), 0, 1);
        form.add(imieField, 1, 1);
        form.add(new Label("Nazwisko:"), 0, 2);
        form.add(nazwiskoField, 1, 2);
        form.add(new Label("Indeks:"), 0, 3);
        form.add(indeksField, 1, 3);
        form.add(addButton, 0, 4);
        form.add(updateButton, 1, 4);
        form.add(deleteButton, 0, 5);
        form.add(clearButton, 1, 5);

        VBox root = new VBox(15, new Label("Zarzadzanie studentami"), studentTable, form);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createPrzedmiotyTab() {
        TableColumn<Przedmiot, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Przedmiot, String> nazwaCol = new TableColumn<>("Nazwa przedmiotu");
        nazwaCol.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        TableColumn<Przedmiot, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        przedmiotTable.getColumns().clear();
        przedmiotTable.getColumns().addAll(idCol, nazwaCol, ectsCol);
        przedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        przedmiotTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                przedmiotIdField.setText(String.valueOf(selected.getId()));
                przedmiotNazwaField.setText(selected.getNazwa());
                przedmiotEctsField.setText(String.valueOf(selected.getEcts()));
            }
        });

        przedmiotIdField.setEditable(false);

        Button addButton = new Button("Dodaj");
        Button updateButton = new Button("Edytuj");
        Button deleteButton = new Button("Usun");
        Button clearButton = new Button("Wyczysc");

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
        form.add(new Label("ECTS:"), 0, 2);
        form.add(przedmiotEctsField, 1, 2);
        form.add(addButton, 0, 3);
        form.add(updateButton, 1, 3);
        form.add(deleteButton, 0, 4);
        form.add(clearButton, 1, 4);

        VBox root = new VBox(15, new Label("Zarzadzanie przedmiotami"), przedmiotTable, form);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createEtapTab() {
        Label opis = new Label("Obsluga semestrow, ocen i rejestracji studenta");

        Button ustawSemestrButton = new Button("Ustaw studentowi aktualny semestr");
        Button przypiszPrzedmiotDoSemestruButton = new Button("Przypisz przedmiot do semestru");
        Button przypiszPrzedmiotyStudentowiButton = new Button("Przypisz studentowi przedmioty z semestru");
        Button dodajOceneButton = new Button("Dodaj lub popraw ocene");
        Button zaliczRecznieButton = new Button("Zalicz semestr recznie");
        Button kolejnySemestrButton = new Button("Rejestruj na kolejny semestr");
        Button pokazPrzedmiotyButton = new Button("Pokaz przedmioty studenta");

        ustawSemestrButton.setOnAction(e -> ustawAktualnySemestrStudenta());
        przypiszPrzedmiotDoSemestruButton.setOnAction(e -> przypiszPrzedmiotDoSemestru());
        przypiszPrzedmiotyStudentowiButton.setOnAction(e -> przypiszStudentowiPrzedmiotyZSemestru());
        dodajOceneButton.setOnAction(e -> dodajOcene());
        zaliczRecznieButton.setOnAction(e -> zaliczSemestrRecznie());
        kolejnySemestrButton.setOnAction(e -> rejestrujNaKolejnySemestr());
        pokazPrzedmiotyButton.setOnAction(e -> pokazPrzedmiotyStudenta());

        semestrCombo.valueProperty().addListener((obs, oldVal, selected) -> odswiezPrzedmiotySemestru());

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

        TableColumn<Przedmiot, String> semestrPrzedmiotCol = new TableColumn<>("Przedmiot semestru");
        semestrPrzedmiotCol.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        TableColumn<Przedmiot, Integer> semestrPrzedmiotEctsCol = new TableColumn<>("ECTS");
        semestrPrzedmiotEctsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        semestrPrzedmiotTable.getColumns().clear();
        semestrPrzedmiotTable.getColumns().addAll(semestrPrzedmiotCol, semestrPrzedmiotEctsCol);
        semestrPrzedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StudentPrzedmiotView, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentPrzedmiotView, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

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
        studentPrzedmiotTable.getColumns().addAll(przedmiotCol, ectsCol, semestrCol, ocenaCol, statusCol);
        studentPrzedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(
                15,
                opis,
                form,
                new Label("Przedmioty przypisane do wybranego semestru"),
                semestrPrzedmiotTable,
                new Label("Przedmioty i oceny wybranego studenta"),
                studentPrzedmiotTable
        );
        root.setPadding(new Insets(15));
        return root;
    }

    private void addStudent() {
        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        String indeksText = indeksField.getText().trim();

        if (imie.isEmpty() || nazwisko.isEmpty() || indeksText.isEmpty()) {
            showAlert("Blad", "Wypelnij wszystkie pola studenta.");
            return;
        }

        try {
            int indeks = Integer.parseInt(indeksText);
            studentRepository.addStudent(imie, nazwisko, indeks);
            refreshAll();
            clearStudentFields();
        } catch (NumberFormatException ex) {
            showAlert("Blad", "Indeks musi byc liczba.");
        }
    }

    private void updateStudent() {
        if (studentIdField.getText().isEmpty()) {
            showAlert("Blad", "Wybierz studenta z tabeli.");
            return;
        }

        try {
            int id = Integer.parseInt(studentIdField.getText());
            int indeks = Integer.parseInt(indeksField.getText());

            studentRepository.updateStudent(id, imieField.getText().trim(), nazwiskoField.getText().trim(), indeks);
            refreshAll();
            clearStudentFields();
        } catch (NumberFormatException ex) {
            showAlert("Blad", "Indeks musi byc liczba.");
        }
    }

    private void deleteStudent() {
        if (studentIdField.getText().isEmpty()) {
            showAlert("Blad", "Wybierz studenta z tabeli.");
            return;
        }

        int id = Integer.parseInt(studentIdField.getText());
        studentRepository.deleteStudent(id);
        refreshAll();
        clearStudentFields();
    }

    private void addPrzedmiot() {
        String nazwa = przedmiotNazwaField.getText().trim();
        String ectsText = przedmiotEctsField.getText().trim();

        if (nazwa.isEmpty() || ectsText.isEmpty()) {
            showAlert("Blad", "Wpisz nazwe przedmiotu i liczbe ECTS.");
            return;
        }

        try {
            int ects = Integer.parseInt(ectsText);
            przedmiotRepository.addPrzedmiot(nazwa, ects);
            refreshAll();
            clearPrzedmiotFields();
        } catch (NumberFormatException ex) {
            showAlert("Blad", "ECTS musi byc liczba.");
        }
    }

    private void updatePrzedmiot() {
        if (przedmiotIdField.getText().isEmpty()) {
            showAlert("Blad", "Wybierz przedmiot z tabeli.");
            return;
        }

        String nazwa = przedmiotNazwaField.getText().trim();
        String ectsText = przedmiotEctsField.getText().trim();

        if (nazwa.isEmpty() || ectsText.isEmpty()) {
            showAlert("Blad", "Wpisz nazwe przedmiotu i liczbe ECTS.");
            return;
        }

        try {
            int id = Integer.parseInt(przedmiotIdField.getText());
            int ects = Integer.parseInt(ectsText);
            przedmiotRepository.updatePrzedmiot(id, nazwa, ects);
            refreshAll();
            clearPrzedmiotFields();
            odswiezPrzedmiotySemestru();
        } catch (NumberFormatException ex) {
            showAlert("Blad", "ECTS musi byc liczba.");
        }
    }

    private void deletePrzedmiot() {
        if (przedmiotIdField.getText().isEmpty()) {
            showAlert("Blad", "Wybierz przedmiot z tabeli.");
            return;
        }

        int id = Integer.parseInt(przedmiotIdField.getText());
        przedmiotRepository.deletePrzedmiot(id);
        refreshAll();
        clearPrzedmiotFields();
        odswiezPrzedmiotySemestru();
    }

    private void ustawAktualnySemestrStudenta() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Blad", "Wybierz studenta i semestr.");
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
            showAlert("Blad", "Wybierz semestr i przedmiot.");
            return;
        }

        studentSemestrRepository.przypiszPrzedmiotDoSemestru(semestr.getId(), przedmiot.getId());
        odswiezPrzedmiotySemestru();
        showInfo("OK", "Przedmiot przypisany do semestru.");
    }

    private void przypiszStudentowiPrzedmiotyZSemestru() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Blad", "Wybierz studenta i semestr.");
            return;
        }

        studentSemestrRepository.przypiszPrzedmiotySemestruStudentowi(student.getId(), semestr.getId());
        aktualizujStatusSemestru(student.getId(), semestr.getId());
        refreshAll();
        pokazPrzedmiotyStudenta();
        showInfo("OK", "Student otrzymal przedmioty z wybranego semestru.");
    }

    private void dodajOcene() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();
        Przedmiot przedmiot = przedmiotCombo.getValue();
        String ocenaText = ocenaField.getText().trim();

        if (student == null || semestr == null || przedmiot == null || ocenaText.isEmpty()) {
            showAlert("Blad", "Wybierz studenta, semestr, przedmiot i wpisz ocene.");
            return;
        }

        try {
            double ocena = Double.parseDouble(ocenaText);

            ocenaRepository.addOcena(student.getId(), przedmiot.getId(), semestr.getId(), ocena);
            studentSemestrRepository.synchronizujZaliczeniePrzedmiotow(student.getId(), semestr.getId());
            aktualizujStatusSemestru(student.getId(), semestr.getId());

            refreshAll();
            pokazPrzedmiotyStudenta();
            ocenaField.clear();
            showInfo("OK", "Zapisano ocene.");
        } catch (NumberFormatException ex) {
            showAlert("Blad", "Ocena musi byc liczba.");
        }
    }

    private void zaliczSemestrRecznie() {
        Student student = studentCombo.getValue();
        Semestr semestr = semestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Blad", "Wybierz studenta i semestr.");
            return;
        }

        studentRepository.setStatusSemestru(student.getId(), "Zaliczony");
        refreshAll();
        showInfo("OK", "Semestr oznaczono jako zaliczony recznie.");
    }

    private void rejestrujNaKolejnySemestr() {
        Student student = studentCombo.getValue();
        Semestr current = semestrCombo.getValue();

        if (student == null || current == null) {
            showAlert("Blad", "Wybierz studenta i aktualny semestr.");
            return;
        }

        List<Double> grades = studentSemestrRepository.getOcenyStudentaWSemestrze(student.getId(), current.getId());
        SemesterDecision decision = semesterCompletionService.evaluateSemester(grades);
        Student aktualnyStudent = studentRepository.getStudentById(student.getId());
        boolean manualOverride = aktualnyStudent != null && "Zaliczony".equals(aktualnyStudent.getStatusSemestru());

        if (!semesterCompletionService.canRegisterNextSemester(decision) && !manualOverride) {
            showAlert("Blad", "Student nie spelnia warunkow rejestracji na kolejny semestr.");
            return;
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
        showInfo("OK", "Student zostal zarejestrowany na kolejny semestr: " + next.getNazwa());
    }

    private void pokazPrzedmiotyStudenta() {
        Student student = studentCombo.getValue();

        if (student == null) {
            showAlert("Blad", "Wybierz studenta.");
            return;
        }

        studentPrzedmiotTable.setItems(
                FXCollections.observableArrayList(studentSemestrRepository.getStudentDashboardData(student.getId()))
        );
    }

    private void odswiezPrzedmiotySemestru() {
        Semestr semestr = semestrCombo.getValue();
        if (semestr == null) {
            semestrPrzedmiotTable.setItems(FXCollections.observableArrayList());
            return;
        }

        semestrPrzedmiotTable.setItems(
                FXCollections.observableArrayList(przedmiotRepository.getPrzedmiotyDlaSemestru(semestr.getId()))
        );
    }

    private void aktualizujStatusSemestru(int studentId, int semestrId) {
        List<Double> grades = studentSemestrRepository.getOcenyStudentaWSemestrze(studentId, semestrId);
        String status = grades.isEmpty() ? "W trakcie" : semesterCompletionService.getStatusForStudent(grades);
        studentRepository.setStatusSemestru(studentId, status);
    }

    private void refreshAll() {
        studentTable.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        przedmiotTable.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));

        studentCombo.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        semestrCombo.setItems(FXCollections.observableArrayList(semestrRepository.getAllSemestry()));
        przedmiotCombo.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));
        odswiezPrzedmiotySemestru();
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
        przedmiotEctsField.clear();
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
