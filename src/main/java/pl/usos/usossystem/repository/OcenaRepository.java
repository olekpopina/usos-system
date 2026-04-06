package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.OcenaView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OcenaRepository {

    public void addOcena(int studentId, int przedmiotId, int semestrId, double ocena) {
        String sql = "INSERT INTO ocena (student_id, przedmiot_id, semestr_id, ocena) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, przedmiotId);
            stmt.setInt(3, semestrId);
            stmt.setDouble(4, ocena);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OcenaView> getAllOcenyView() {
        List<OcenaView> oceny = new ArrayList<>();

        String sql = """
                SELECT o.id, s.imie, s.nazwisko, p.nazwa AS przedmiot, o.ocena
                FROM ocena o
                JOIN student s ON o.student_id = s.id
                JOIN przedmiot p ON o.przedmiot_id = p.id
                ORDER BY o.id
                """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                oceny.add(new OcenaView(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("przedmiot"),
                        rs.getDouble("ocena")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return oceny;
    }
}