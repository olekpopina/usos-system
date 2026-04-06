package pl.usos.usossystem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentPrzedmiotView;
import pl.usos.usossystem.repository.StudentSemestrRepository;

public class StudentPanelApp {

    private final Student student;
    private final StudentSemestrRepository studentSemestrRepository = new StudentSemestrRepository();

    public StudentPanelApp(Student student) {
        this.student = student;
    }

    public void start(Stage stage) {
        Label titleLabel = new Label("Panel studenta");

        Label daneLabel = new Label(
                "Imię i nazwisko: " + student.getImie() + " " + student.getNazwisko() +
                        "\nNumer indeksu: " + student.getIndeks() +
                        "\nAktualny semestr: " + (student.getAktualnySemestrNazwa() == null ? "Brak" : student.getAktualnySemestrNazwa()) +
                        "\nStatus semestru: " + (student.getStatusSemestru() == null ? "Brak" : student.getStatusSemestru())
        );

        TableView<StudentPrzedmiotView> table = new TableView<>();

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

        table.getColumns().addAll(przedmiotCol, semestrCol, ocenaCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(
                studentSemestrRepository.getStudentDashboardData(student.getId())
        ));

        VBox root = new VBox(15, titleLabel, daneLabel, table);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 850, 550);
        stage.setTitle("Mini-USOS - Konto studenta");
        stage.setScene(scene);
        stage.show();
    }
}