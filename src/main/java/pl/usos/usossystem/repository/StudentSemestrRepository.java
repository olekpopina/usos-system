package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.StudentPrzedmiotView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentSemestrRepository {

    public void przypiszPrzedmiotDoSemestru(int semestrId, int przedmiotId) {
        String sql = "INSERT IGNORE INTO semestr_przedmiot (semestr_id, przedmiot_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestrId);
            stmt.setInt(2, przedmiotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void przypiszPrzedmiotySemestruStudentowi(int studentId, int semestrId) {
        String sql = """
                INSERT IGNORE INTO student_przedmiot (student_id, przedmiot_id, semestr_id, zaliczony)
                SELECT ?, sp.przedmiot_id, sp.semestr_id, FALSE
                FROM semestr_przedmiot sp
                WHERE sp.semestr_id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, semestrId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StudentPrzedmiotView> getStudentDashboardData(int studentId) {
        List<StudentPrzedmiotView> list = new ArrayList<>();

        String sql = """
                SELECT p.nazwa AS przedmiot,
                       sem.nazwa AS semestr,
                       o.ocena,
                       sp.zaliczony
                FROM student_przedmiot sp
                JOIN przedmiot p ON sp.przedmiot_id = p.id
                JOIN semestr sem ON sp.semestr_id = sem.id
                LEFT JOIN ocena o
                    ON o.student_id = sp.student_id
                   AND o.przedmiot_id = sp.przedmiot_id
                   AND o.semestr_id = sp.semestr_id
                WHERE sp.student_id = ?
                ORDER BY sem.numer, p.nazwa
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new StudentPrzedmiotView(
                        rs.getString("przedmiot"),
                        rs.getString("semestr"),
                        (Double) rs.getObject("ocena"),
                        rs.getBoolean("zaliczony")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean czyStudentMaWszystkieOcenyWSemestrze(int studentId, int semestrId) {
        String sql = """
                SELECT COUNT(*) AS brak
                FROM student_przedmiot sp
                LEFT JOIN ocena o
                    ON o.student_id = sp.student_id
                   AND o.przedmiot_id = sp.przedmiot_id
                   AND o.semestr_id = sp.semestr_id
                WHERE sp.student_id = ?
                  AND sp.semestr_id = ?
                  AND o.id IS NULL
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, semestrId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("brak") == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void oznaczPrzedmiotyJakoZaliczoneJesliMajaOceny(int studentId, int semestrId) {
        String sql = """
                UPDATE student_przedmiot sp
                JOIN ocena o
                  ON o.student_id = sp.student_id
                 AND o.przedmiot_id = sp.przedmiot_id
                 AND o.semestr_id = sp.semestr_id
                SET sp.zaliczony = TRUE
                WHERE sp.student_id = ? AND sp.semestr_id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, semestrId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}