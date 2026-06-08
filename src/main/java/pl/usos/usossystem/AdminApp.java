package pl.usos.usossystem;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Przedmiot;
import pl.usos.usossystem.model.Semestr;
import pl.usos.usossystem.model.SemesterProgressView;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentCourseRecord;
import pl.usos.usossystem.repository.OcenaRepository;
import pl.usos.usossystem.repository.PrzedmiotRepository;
import pl.usos.usossystem.repository.SemestrRepository;
import pl.usos.usossystem.repository.StudentRepository;
import pl.usos.usossystem.repository.StudentSemestrRepository;
import pl.usos.usossystem.service.SemesterCompletionService;
import pl.usos.usossystem.service.SemesterWorkflowService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AdminApp extends javafx.application.Application {

    private final StudentRepository studentRepository = new StudentRepository();
    private final PrzedmiotRepository przedmiotRepository = new PrzedmiotRepository();
    private final SemestrRepository semestrRepository = new SemestrRepository();
    private final StudentSemestrRepository studentSemestrRepository = new StudentSemestrRepository();
    private final OcenaRepository ocenaRepository = new OcenaRepository();
    private final SemesterWorkflowService workflowService = new SemesterWorkflowService(
            studentRepository,
            semestrRepository,
            studentSemestrRepository,
            ocenaRepository,
            new SemesterCompletionService()
    );

    private final TableView<Student> studentTable = new TableView<>();
    private final TableView<Przedmiot> przedmiotTable = new TableView<>();
    private final TableView<Przedmiot> semestrPrzedmiotTable = new TableView<>();
    private final TableView<StudentCourseRecord> workflowCourseTable = new TableView<>();
    private final TableView<StudentCourseRecord> studentHistoryTable = new TableView<>();

    private final TextField studentIdField = new TextField();
    private final TextField imieField = new TextField();
    private final TextField nazwiskoField = new TextField();
    private final TextField indeksField = new TextField();

    private final TextField przedmiotIdField = new TextField();
    private final TextField przedmiotNazwaField = new TextField();
    private final TextField przedmiotEctsField = new TextField();

    private final ComboBox<Student> workflowStudentCombo = new ComboBox<>();
    private final ComboBox<Semestr> workflowSemestrCombo = new ComboBox<>();
    private final ComboBox<Semestr> workflowPreviewSemestrCombo = new ComboBox<>();
    private final ComboBox<StudentCourseRecord> workflowCourseCombo = new ComboBox<>();
    private final TextField workflowOcenaField = new TextField();

    private final ComboBox<Semestr> mappingSemestrCombo = new ComboBox<>();
    private final ComboBox<Przedmiot> mappingPrzedmiotCombo = new ComboBox<>();

    private final Label studentPreviewSemestrLabel = new Label("-");
    private final Label studentPreviewStatusLabel = new Label("-");
    private final Label studentPreviewEctsLabel = new Label("-");

    private final Label workflowCurrentSemestrLabel = new Label("-");
    private final Label workflowStatusLabel = new Label("-");
    private final Label workflowEctsLabel = new Label("-");
    private final Label workflowThresholdLabel = new Label("-");
    private final Label workflowFailedLabel = new Label("-");
    private final Label workflowMissingLabel = new Label("-");
    private final Label workflowCanRegisterLabel = new Label("-");

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        Tab studenciTab = new Tab("Studenci", createStudenciTab());
        Tab przedmiotyTab = new Tab("Przedmioty i semestry", createPrzedmiotyTab());
        Tab przebiegTab = new Tab("Przebieg studiow", createPrzebiegTab());

        studenciTab.setClosable(false);
        przedmiotyTab.setClosable(false);
        przebiegTab.setClosable(false);
        tabPane.getTabs().addAll(studenciTab, przedmiotyTab, przebiegTab);

        refreshAll();

        stage.setScene(new Scene(tabPane, 1280, 860));
        stage.setTitle("Mini-USOS - Panel dziekanatu");
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

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statusSemestru"));

        studentTable.getColumns().setAll(idCol, imieCol, nazwiskoCol, indeksCol, semestrCol, statusCol);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, student) -> {
            if (student == null) {
                return;
            }
            studentIdField.setText(String.valueOf(student.getId()));
            imieField.setText(student.getImie());
            nazwiskoField.setText(student.getNazwisko());
            indeksField.setText(String.valueOf(student.getIndeks()));
            workflowStudentCombo.setValue(findStudentById(student.getId()));
            updateStudentPreview(student);
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

        GridPane preview = new GridPane();
        preview.setHgap(12);
        preview.setVgap(8);
        preview.add(new Label("Podglad aktualnego semestru"), 0, 0, 2, 1);
        preview.add(new Label("Semestr:"), 0, 1);
        preview.add(studentPreviewSemestrLabel, 1, 1);
        preview.add(new Label("Status:"), 0, 2);
        preview.add(studentPreviewStatusLabel, 1, 2);
        preview.add(new Label("ECTS:"), 0, 3);
        preview.add(studentPreviewEctsLabel, 1, 3);

        HBox lowerSection = new HBox(30, form, preview);
        VBox root = new VBox(15, new Label("Zarzadzanie studentami"), studentTable, lowerSection);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createPrzedmiotyTab() {
        TableColumn<Przedmiot, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Przedmiot, String> nazwaCol = new TableColumn<>("Nazwa");
        nazwaCol.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        TableColumn<Przedmiot, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<Przedmiot, String> semestryCol = new TableColumn<>("Semestry");
        semestryCol.setCellValueFactory(new PropertyValueFactory<>("semestry"));

        przedmiotTable.getColumns().setAll(idCol, nazwaCol, ectsCol, semestryCol);
        przedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        przedmiotTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, przedmiot) -> {
            if (przedmiot == null) {
                return;
            }
            przedmiotIdField.setText(String.valueOf(przedmiot.getId()));
            przedmiotNazwaField.setText(przedmiot.getNazwa());
            przedmiotEctsField.setText(String.valueOf(przedmiot.getEcts()));
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

        Button przypiszButton = new Button("Przypisz przedmiot do semestru");
        przypiszButton.setOnAction(e -> przypiszPrzedmiotDoSemestru());
        mappingSemestrCombo.valueProperty().addListener((obs, oldValue, value) -> odswiezPrzedmiotySemestruKonfiguracyjne());

        GridPane mappingForm = new GridPane();
        mappingForm.setHgap(10);
        mappingForm.setVgap(10);
        mappingForm.add(new Label("Semestr:"), 0, 0);
        mappingForm.add(mappingSemestrCombo, 1, 0);
        mappingForm.add(new Label("Przedmiot:"), 0, 1);
        mappingForm.add(mappingPrzedmiotCombo, 1, 1);
        mappingForm.add(przypiszButton, 0, 2, 2, 1);

        TableColumn<Przedmiot, String> mapNazwaCol = new TableColumn<>("Przedmiot semestru");
        mapNazwaCol.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        TableColumn<Przedmiot, Integer> mapEctsCol = new TableColumn<>("ECTS");
        mapEctsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<Przedmiot, String> mapSemestrCol = new TableColumn<>("Semestr");
        mapSemestrCol.setCellValueFactory(new PropertyValueFactory<>("semestry"));

        semestrPrzedmiotTable.getColumns().setAll(mapNazwaCol, mapEctsCol, mapSemestrCol);
        semestrPrzedmiotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox lowerSection = new HBox(30, form, new VBox(10, new Label("Konfiguracja semestru"), mappingForm, semestrPrzedmiotTable));
        VBox root = new VBox(15, new Label("Przedmioty i przypisania do semestrow"), przedmiotTable, lowerSection);
        root.setPadding(new Insets(15));
        return root;
    }

    private VBox createPrzebiegTab() {
        workflowStudentCombo.valueProperty().addListener((obs, oldValue, student) -> updateWorkflowForStudent(student));
        workflowPreviewSemestrCombo.valueProperty().addListener((obs, oldValue, semestr) -> showSelectedPreviewSemester());

        GridPane selectionBox = new GridPane();
        selectionBox.setHgap(10);
        selectionBox.setVgap(10);
        selectionBox.add(new Label("Student:"), 0, 0);
        selectionBox.add(workflowStudentCombo, 1, 0);
        selectionBox.add(new Label("Semestr do ustawienia:"), 0, 1);
        selectionBox.add(workflowSemestrCombo, 1, 1);
        selectionBox.add(new Label("Semestr do podgladu:"), 0, 2);
        selectionBox.add(workflowPreviewSemestrCombo, 1, 2);

        GridPane summaryBox = new GridPane();
        summaryBox.setHgap(12);
        summaryBox.setVgap(8);
        summaryBox.add(new Label("Aktualny semestr:"), 0, 0);
        summaryBox.add(workflowCurrentSemestrLabel, 1, 0);
        summaryBox.add(new Label("Status:"), 0, 1);
        summaryBox.add(workflowStatusLabel, 1, 1);
        summaryBox.add(new Label("ECTS zdobyte / wymagane:"), 0, 2);
        summaryBox.add(workflowEctsLabel, 1, 2);
        summaryBox.add(new Label("Prog warunkowy ECTS:"), 0, 3);
        summaryBox.add(workflowThresholdLabel, 1, 3);
        summaryBox.add(new Label("Niezaliczone przedmioty:"), 0, 4);
        summaryBox.add(workflowFailedLabel, 1, 4);
        summaryBox.add(new Label("Brakujace oceny:"), 0, 5);
        summaryBox.add(workflowMissingLabel, 1, 5);
        summaryBox.add(new Label("Mozna rejestrowac dalej:"), 0, 6);
        summaryBox.add(workflowCanRegisterLabel, 1, 6);

        TableColumn<StudentCourseRecord, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentCourseRecord, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<StudentCourseRecord, String> semestrCol = new TableColumn<>("Semestr");
        semestrCol.setCellValueFactory(new PropertyValueFactory<>("semestr"));

        TableColumn<StudentCourseRecord, Double> ocenaCol = new TableColumn<>("Ocena");
        ocenaCol.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        TableColumn<StudentCourseRecord, String> statusCol = new TableColumn<>("Zaliczony");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        workflowCourseTable.getColumns().setAll(przedmiotCol, ectsCol, semestrCol, ocenaCol, statusCol);
        workflowCourseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StudentCourseRecord, String> historyPrzedmiotCol = new TableColumn<>("Przedmiot");
        historyPrzedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentCourseRecord, String> historySemestrCol = new TableColumn<>("Semestr");
        historySemestrCol.setCellValueFactory(new PropertyValueFactory<>("semestr"));

        TableColumn<StudentCourseRecord, Integer> historyEctsCol = new TableColumn<>("ECTS");
        historyEctsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<StudentCourseRecord, Double> historyOcenaCol = new TableColumn<>("Ocena");
        historyOcenaCol.setCellValueFactory(new PropertyValueFactory<>("ocena"));

        TableColumn<StudentCourseRecord, String> historyStatusCol = new TableColumn<>("Zaliczony");
        historyStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentHistoryTable.getColumns().setAll(historyPrzedmiotCol, historySemestrCol, historyEctsCol, historyOcenaCol, historyStatusCol);
        studentHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        GridPane gradeForm = new GridPane();
        gradeForm.setHgap(10);
        gradeForm.setVgap(10);
        gradeForm.add(new Label("Przedmiot z aktualnego semestru:"), 0, 0);
        gradeForm.add(workflowCourseCombo, 1, 0);
        gradeForm.add(new Label("Ocena:"), 0, 1);
        gradeForm.add(workflowOcenaField, 1, 1);

        Button ustawSemestrButton = new Button("Ustaw aktualny semestr");
        Button zapiszOceneButton = new Button("Zapisz / popraw ocene");
        Button naprawButton = new Button("Napraw przypisanie przedmiotow");
        Button zaliczRecznieButton = new Button("Zalicz recznie");
        Button kolejnySemestrButton = new Button("Rejestruj na kolejny semestr");

        ustawSemestrButton.setOnAction(e -> ustawAktualnySemestrStudenta());
        zapiszOceneButton.setOnAction(e -> zapiszOceneDlaAktualnegoSemestru());
        naprawButton.setOnAction(e -> naprawPrzypisaniePrzedmiotow());
        zaliczRecznieButton.setOnAction(e -> zaliczSemestrRecznie());
        kolejnySemestrButton.setOnAction(e -> rejestrujNaKolejnySemestr());

        HBox actions = new HBox(10, ustawSemestrButton, zapiszOceneButton, naprawButton, zaliczRecznieButton, kolejnySemestrButton);

        VBox root = new VBox(
                15,
                new Label("Workflow przebiegu studiow"),
                selectionBox,
                summaryBox,
                new Label("Przedmioty wybranego semestru"),
                workflowCourseTable,
                gradeForm,
                actions,
                new Label("Historia wszystkich przedmiotow studenta"),
                studentHistoryTable
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
    }

    private void przypiszPrzedmiotDoSemestru() {
        Semestr semestr = mappingSemestrCombo.getValue();
        Przedmiot przedmiot = mappingPrzedmiotCombo.getValue();

        if (semestr == null || przedmiot == null) {
            showAlert("Blad", "Wybierz semestr i przedmiot.");
            return;
        }

        studentSemestrRepository.przypiszPrzedmiotDoSemestru(semestr.getId(), przedmiot.getId());
        odswiezPrzedmiotySemestruKonfiguracyjne();
        showInfo("OK", "Przedmiot przypisany do semestru.");
    }

    private void ustawAktualnySemestrStudenta() {
        Student student = workflowStudentCombo.getValue();
        Semestr semestr = workflowSemestrCombo.getValue();

        if (student == null || semestr == null) {
            showAlert("Blad", "Wybierz studenta i semestr.");
            return;
        }

        try {
            SemesterProgressView progressView = workflowService.assignCurrentSemester(student.getId(), semestr.getId());
            refreshAll();
            workflowStudentCombo.setValue(findStudentById(student.getId()));
            showWorkflowProgress(progressView);
            showInfo("OK", "Ustawiono aktualny semestr i przypisano wymagane przedmioty.");
        } catch (IllegalStateException ex) {
            showAlert("Blad", ex.getMessage());
        }
    }

    private void naprawPrzypisaniePrzedmiotow() {
        Student student = workflowStudentCombo.getValue();
        if (student == null || student.getAktualnySemestrId() == null) {
            showAlert("Blad", "Wybierz studenta z ustawionym aktualnym semestrem.");
            return;
        }

        SemesterProgressView progressView = workflowService.repairSemesterAssignments(student.getId(), student.getAktualnySemestrId());
        refreshAll();
        workflowStudentCombo.setValue(findStudentById(student.getId()));
        showWorkflowProgress(progressView);
        showInfo("OK", "Przypisania przedmiotow zostaly uzupelnione.");
    }

    private void zapiszOceneDlaAktualnegoSemestru() {
        Student student = workflowStudentCombo.getValue();
        StudentCourseRecord courseRecord = workflowCourseCombo.getValue();
        String gradeText = workflowOcenaField.getText().trim();

        if (student == null || student.getAktualnySemestrId() == null || courseRecord == null || gradeText.isEmpty()) {
            showAlert("Blad", "Wybierz studenta, przedmiot i wpisz ocene.");
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText);
            SemesterProgressView progressView = workflowService.saveGradeAndRecalculate(
                    student.getId(),
                    student.getAktualnySemestrId(),
                    courseRecord.getPrzedmiotId(),
                    grade
            );
            refreshAll();
            workflowStudentCombo.setValue(findStudentById(student.getId()));
            showWorkflowProgress(progressView);
            workflowOcenaField.clear();
            showInfo("OK", "Ocena zostala zapisana.");
        } catch (NumberFormatException ex) {
            showAlert("Blad", "Ocena musi byc liczba.");
        } catch (IllegalStateException ex) {
            showAlert("Blad", ex.getMessage());
        }
    }

    private void zaliczSemestrRecznie() {
        Student student = workflowStudentCombo.getValue();
        if (student == null) {
            showAlert("Blad", "Wybierz studenta.");
            return;
        }

        try {
            SemesterProgressView progressView = workflowService.markCurrentSemesterPassedManually(student.getId());
            refreshAll();
            workflowStudentCombo.setValue(findStudentById(student.getId()));
            showWorkflowProgress(progressView);
            showInfo("OK", "Semestr oznaczono jako zaliczony recznie.");
        } catch (IllegalStateException ex) {
            showAlert("Blad", ex.getMessage());
        }
    }

    private void rejestrujNaKolejnySemestr() {
        Student student = workflowStudentCombo.getValue();
        if (student == null) {
            showAlert("Blad", "Wybierz studenta.");
            return;
        }

        try {
            SemesterProgressView progressView = workflowService.registerForNextSemester(student.getId());
            refreshAll();
            workflowStudentCombo.setValue(findStudentById(student.getId()));
            showWorkflowProgress(progressView);
            showInfo("OK", "Student zostal zarejestrowany na kolejny semestr.");
        } catch (IllegalStateException ex) {
            showAlert("Blad", ex.getMessage());
        }
    }

    private void updateStudentPreview(Student student) {
        if (student == null) {
            studentPreviewSemestrLabel.setText("-");
            studentPreviewStatusLabel.setText("-");
            studentPreviewEctsLabel.setText("-");
            return;
        }

        SemesterProgressView progressView = workflowService.getCurrentSemesterProgress(student.getId());
        studentPreviewSemestrLabel.setText(progressView.getSemestrNazwa());
        studentPreviewStatusLabel.setText(progressView.getStatusLabel());
        studentPreviewEctsLabel.setText(progressView.getEctsSummary());
    }

    private void updateWorkflowForStudent(Student student) {
        if (student == null) {
            clearWorkflowView();
            return;
        }

        SemesterProgressView currentProgress = workflowService.getCurrentSemesterProgress(student.getId());
        List<StudentCourseRecord> history = workflowService.getStudentCourseHistory(student.getId());
        populateWorkflowPreviewSemesters(student, history, currentProgress);
        populateWorkflowEditorCourses(currentProgress);

        if (student.getAktualnySemestrId() != null) {
            workflowSemestrCombo.setValue(findSemestrById(student.getAktualnySemestrId()));
        }

        if (workflowPreviewSemestrCombo.getValue() == null && currentProgress.getSemestrId() > 0) {
            workflowPreviewSemestrCombo.setValue(findWorkflowPreviewSemestrById(currentProgress.getSemestrId()));
        }
        showSelectedPreviewSemester();
    }

    private void showWorkflowProgress(SemesterProgressView progressView) {
        workflowCurrentSemestrLabel.setText(progressView.getSemestrNazwa());
        workflowStatusLabel.setText(progressView.getStatusLabel());
        workflowEctsLabel.setText(progressView.getEctsSummary());
        workflowThresholdLabel.setText(String.valueOf(progressView.getConditionalEctsThreshold()));
        workflowFailedLabel.setText(String.valueOf(progressView.getFailedSubjectsCount()));
        workflowMissingLabel.setText(String.valueOf(progressView.getMissingGradesCount()));
        workflowCanRegisterLabel.setText(progressView.getCanRegisterLabel());
        workflowCourseTable.setItems(FXCollections.observableArrayList(progressView.getCourseRecords()));

        Student selectedStudent = workflowStudentCombo.getValue();
        if (selectedStudent != null) {
            studentHistoryTable.setItems(FXCollections.observableArrayList(workflowService.getStudentCourseHistory(selectedStudent.getId())));
            updateStudentPreview(selectedStudent);
        }
    }

    private void showSelectedPreviewSemester() {
        Student student = workflowStudentCombo.getValue();
        Semestr selectedPreview = workflowPreviewSemestrCombo.getValue();
        if (student == null || selectedPreview == null) {
            workflowCourseTable.setItems(FXCollections.observableArrayList());
            return;
        }

        SemesterProgressView progressView = workflowService.getSemesterProgress(student.getId(), selectedPreview.getId());
        showWorkflowProgress(progressView);
    }

    private void populateWorkflowPreviewSemesters(Student student, List<StudentCourseRecord> history, SemesterProgressView currentProgress) {
        Set<Integer> semesterIds = new LinkedHashSet<>();
        for (StudentCourseRecord record : history) {
            semesterIds.add(record.getSemestrId());
        }
        if (student.getAktualnySemestrId() != null) {
            semesterIds.add(student.getAktualnySemestrId());
        }
        if (currentProgress.getSemestrId() > 0) {
            semesterIds.add(currentProgress.getSemestrId());
        }

        List<Semestr> availableSemesters = semestrRepository.getAllSemestry().stream()
                .filter(semestr -> semesterIds.contains(semestr.getId()))
                .toList();

        Integer selectedPreviewId = workflowPreviewSemestrCombo.getValue() == null
                ? null
                : workflowPreviewSemestrCombo.getValue().getId();
        workflowPreviewSemestrCombo.setItems(FXCollections.observableArrayList(availableSemesters));

        if (selectedPreviewId != null) {
            workflowPreviewSemestrCombo.setValue(findWorkflowPreviewSemestrById(selectedPreviewId));
        }
    }

    private void populateWorkflowEditorCourses(SemesterProgressView currentProgress) {
        workflowCourseCombo.setItems(FXCollections.observableArrayList(currentProgress.getCourseRecords()));
    }

    private void clearWorkflowView() {
        workflowCurrentSemestrLabel.setText("-");
        workflowStatusLabel.setText("-");
        workflowEctsLabel.setText("-");
        workflowThresholdLabel.setText("-");
        workflowFailedLabel.setText("-");
        workflowMissingLabel.setText("-");
        workflowCanRegisterLabel.setText("-");
        workflowCourseTable.setItems(FXCollections.observableArrayList());
        workflowCourseCombo.setItems(FXCollections.observableArrayList());
        workflowPreviewSemestrCombo.setItems(FXCollections.observableArrayList());
        studentHistoryTable.setItems(FXCollections.observableArrayList());
    }

    private void odswiezPrzedmiotySemestruKonfiguracyjne() {
        Semestr semestr = mappingSemestrCombo.getValue();
        if (semestr == null) {
            semestrPrzedmiotTable.setItems(FXCollections.observableArrayList());
            return;
        }

        semestrPrzedmiotTable.setItems(FXCollections.observableArrayList(przedmiotRepository.getPrzedmiotyDlaSemestru(semestr.getId())));
    }

    private void refreshAll() {
        Integer selectedStudentId = workflowStudentCombo.getValue() == null ? null : workflowStudentCombo.getValue().getId();
        Integer selectedMappingSemesterId = mappingSemestrCombo.getValue() == null ? null : mappingSemestrCombo.getValue().getId();
        Integer selectedWorkflowSemesterId = workflowSemestrCombo.getValue() == null ? null : workflowSemestrCombo.getValue().getId();

        studentTable.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        przedmiotTable.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));

        workflowStudentCombo.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
        workflowSemestrCombo.setItems(FXCollections.observableArrayList(semestrRepository.getAllSemestry()));
        mappingSemestrCombo.setItems(FXCollections.observableArrayList(semestrRepository.getAllSemestry()));
        mappingPrzedmiotCombo.setItems(FXCollections.observableArrayList(przedmiotRepository.getAllPrzedmioty()));

        if (selectedStudentId != null) {
            workflowStudentCombo.setValue(findStudentById(selectedStudentId));
        }
        if (selectedMappingSemesterId != null) {
            mappingSemestrCombo.setValue(findSemestrById(selectedMappingSemesterId));
        }
        if (selectedWorkflowSemesterId != null) {
            workflowSemestrCombo.setValue(findSemestrById(selectedWorkflowSemesterId));
        }

        odswiezPrzedmiotySemestruKonfiguracyjne();

        if (workflowStudentCombo.getValue() != null) {
            updateWorkflowForStudent(workflowStudentCombo.getValue());
        } else {
            clearWorkflowView();
        }
    }

    private Student findStudentById(int studentId) {
        return workflowStudentCombo.getItems().stream()
                .filter(student -> student.getId() == studentId)
                .findFirst()
                .orElse(null);
    }

    private Semestr findSemestrById(int semestrId) {
        return workflowSemestrCombo.getItems().stream()
                .filter(semestr -> semestr.getId() == semestrId)
                .findFirst()
                .orElseGet(() -> mappingSemestrCombo.getItems().stream()
                        .filter(semestr -> semestr.getId() == semestrId)
                        .findFirst()
                        .orElse(null));
    }

    private Semestr findWorkflowPreviewSemestrById(int semestrId) {
        return workflowPreviewSemestrCombo.getItems().stream()
                .filter(semestr -> semestr.getId() == semestrId)
                .findFirst()
                .orElse(null);
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
