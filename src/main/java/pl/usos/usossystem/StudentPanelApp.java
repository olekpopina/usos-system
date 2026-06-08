package pl.usos.usossystem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.SemesterProgressView;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentCourseRecord;
import pl.usos.usossystem.repository.OcenaRepository;
import pl.usos.usossystem.repository.SemestrRepository;
import pl.usos.usossystem.repository.StudentRepository;
import pl.usos.usossystem.repository.StudentSemestrRepository;
import pl.usos.usossystem.service.SemesterCompletionService;
import pl.usos.usossystem.service.SemesterWorkflowService;

public class StudentPanelApp {

    private final int studentId;
    private final StudentRepository studentRepository = new StudentRepository();
    private final SemesterWorkflowService workflowService = new SemesterWorkflowService(
            studentRepository,
            new SemestrRepository(),
            new StudentSemestrRepository(),
            new OcenaRepository(),
            new SemesterCompletionService()
    );

    private final Label fullNameLabel = new Label("-");
    private final Label indeksLabel = new Label("-");
    private final Label semestrLabel = new Label("-");
    private final Label statusLabel = new Label("-");
    private final Label ectsLabel = new Label("-");
    private final Label thresholdLabel = new Label("-");
    private final Label failedLabel = new Label("-");
    private final Label missingLabel = new Label("-");
    private final Label canRegisterLabel = new Label("-");

    private final TableView<StudentCourseRecord> currentSemesterTable = new TableView<>();
    private final TableView<StudentCourseRecord> historyTable = new TableView<>();

    public StudentPanelApp(Student student) {
        this.studentId = student.getId();
    }

    public void start(Stage stage) {
        setupCurrentSemesterTable();
        setupHistoryTable();

        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(12);
        summaryGrid.setVgap(8);
        summaryGrid.add(new Label("Imie i nazwisko:"), 0, 0);
        summaryGrid.add(fullNameLabel, 1, 0);
        summaryGrid.add(new Label("Numer indeksu:"), 0, 1);
        summaryGrid.add(indeksLabel, 1, 1);
        summaryGrid.add(new Label("Aktualny semestr:"), 0, 2);
        summaryGrid.add(semestrLabel, 1, 2);
        summaryGrid.add(new Label("Status semestru:"), 0, 3);
        summaryGrid.add(statusLabel, 1, 3);
        summaryGrid.add(new Label("ECTS zdobyte / wymagane:"), 0, 4);
        summaryGrid.add(ectsLabel, 1, 4);
        summaryGrid.add(new Label("Prog warunkowy ECTS:"), 0, 5);
        summaryGrid.add(thresholdLabel, 1, 5);
        summaryGrid.add(new Label("Niezaliczone przedmioty:"), 0, 6);
        summaryGrid.add(failedLabel, 1, 6);
        summaryGrid.add(new Label("Brakujace oceny:"), 0, 7);
        summaryGrid.add(missingLabel, 1, 7);
        summaryGrid.add(new Label("Mozliwa rejestracja dalej:"), 0, 8);
        summaryGrid.add(canRegisterLabel, 1, 8);

        Button refreshButton = new Button("Odswiez dane");
        refreshButton.setOnAction(event -> refreshView());

        Label title = new Label("Panel studenta");
        title.getStyleClass().add("page-title");
        summaryGrid.getStyleClass().add("card-panel");
        Label currentTitle = new Label("Przedmioty aktualnego semestru");
        currentTitle.getStyleClass().add("section-title");
        Label historyTitle = new Label("Historia wszystkich przypisanych przedmiotow");
        historyTitle.getStyleClass().add("section-title");

        VBox root = new VBox(
                15,
                title,
                summaryGrid,
                refreshButton,
                currentTitle,
                currentSemesterTable,
                historyTitle,
                historyTable
        );
        root.getStyleClass().add("page-root");
        root.setPadding(new Insets(20));
        VBox.setVgrow(currentSemesterTable, Priority.ALWAYS);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        refreshView();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("page-scroll");
        AppTheme.configureScrollPane(scrollPane);

        Scene scene = new Scene(scrollPane, 980, 720);
        AppTheme.apply(stage, scene);
        stage.setScene(scene);
        stage.setMinWidth(920);
        stage.setMinHeight(700);
        stage.setTitle("Mini-USOS - Konto studenta");
        stage.show();
    }

    private void setupCurrentSemesterTable() {
        TableColumn<StudentCourseRecord, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentCourseRecord, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<StudentCourseRecord, String> semestrCol = new TableColumn<>("Semestr");
        semestrCol.setCellValueFactory(new PropertyValueFactory<>("semestr"));

        TableColumn<StudentCourseRecord, String> ocenaCol = new TableColumn<>("Ocena");
        ocenaCol.setCellValueFactory(cell -> formatGrade(cell.getValue().getOcena()));

        TableColumn<StudentCourseRecord, String> statusCol = new TableColumn<>("Zaliczony");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        currentSemesterTable.getColumns().setAll(przedmiotCol, ectsCol, semestrCol, ocenaCol, statusCol);
        currentSemesterTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        currentSemesterTable.setMinHeight(220);
        currentSemesterTable.setPrefHeight(260);
    }

    private void setupHistoryTable() {
        TableColumn<StudentCourseRecord, String> semestrCol = new TableColumn<>("Semestr");
        semestrCol.setCellValueFactory(new PropertyValueFactory<>("semestr"));

        TableColumn<StudentCourseRecord, String> przedmiotCol = new TableColumn<>("Przedmiot");
        przedmiotCol.setCellValueFactory(new PropertyValueFactory<>("przedmiot"));

        TableColumn<StudentCourseRecord, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));

        TableColumn<StudentCourseRecord, String> ocenaCol = new TableColumn<>("Ocena");
        ocenaCol.setCellValueFactory(cell -> formatGrade(cell.getValue().getOcena()));

        TableColumn<StudentCourseRecord, String> statusCol = new TableColumn<>("Zaliczony");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyTable.getColumns().setAll(semestrCol, przedmiotCol, ectsCol, ocenaCol, statusCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setMinHeight(220);
        historyTable.setPrefHeight(260);
    }

    private void refreshView() {
        Student refreshedStudent = studentRepository.getStudentById(studentId);
        SemesterProgressView progressView = workflowService.getCurrentSemesterProgress(studentId);

        if (refreshedStudent == null) {
            fullNameLabel.setText("Nie znaleziono studenta");
            indeksLabel.setText("-");
        } else {
            fullNameLabel.setText(refreshedStudent.getImie() + " " + refreshedStudent.getNazwisko());
            indeksLabel.setText(String.valueOf(refreshedStudent.getIndeks()));
        }

        semestrLabel.setText(progressView.getSemestrNazwa());
        statusLabel.setText(progressView.getStatusLabel());
        ectsLabel.setText(progressView.getEctsSummary());
        thresholdLabel.setText(String.valueOf(progressView.getConditionalEctsThreshold()));
        failedLabel.setText(String.valueOf(progressView.getFailedSubjectsCount()));
        missingLabel.setText(String.valueOf(progressView.getMissingGradesCount()));
        canRegisterLabel.setText(progressView.getCanRegisterLabel());

        currentSemesterTable.setItems(FXCollections.observableArrayList(progressView.getCourseRecords()));
        historyTable.setItems(FXCollections.observableArrayList(workflowService.getStudentCourseHistory(studentId)));
    }

    private ReadOnlyStringWrapper formatGrade(Double grade) {
        return new ReadOnlyStringWrapper(grade == null ? "-" : String.format("%.1f", grade));
    }
}
