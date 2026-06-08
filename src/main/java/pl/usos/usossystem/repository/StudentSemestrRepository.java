package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.StudentCourseRecord;
import pl.usos.usossystem.service.StudentSemesterGateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentSemestrRepository implements StudentSemesterGateway {

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

    @Override
    public List<StudentCourseRecord> getStudentCourseRecords(int studentId) {
        List<StudentCourseRecord> list = new ArrayList<>();

        String sql = """
                SELECT p.id AS przedmiot_id,
                       p.nazwa AS przedmiot,
                       p.ects,
                       sem.id AS semestr_id,
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

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new StudentCourseRecord(
                            rs.getInt("przedmiot_id"),
                            rs.getString("przedmiot"),
                            rs.getInt("ects"),
                            rs.getInt("semestr_id"),
                            rs.getString("semestr"),
                            toDouble(rs.getObject("ocena")),
                            rs.getBoolean("zaliczony")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<StudentCourseRecord> getStudentCourseRecordsForSemester(int studentId, int semestrId) {
        List<StudentCourseRecord> list = new ArrayList<>();
        String sql = """
                SELECT p.id AS przedmiot_id,
                       p.nazwa AS przedmiot,
                       p.ects,
                       sem.id AS semestr_id,
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
                  AND sp.semestr_id = ?
                ORDER BY sp.przedmiot_id
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, semestrId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new StudentCourseRecord(
                            rs.getInt("przedmiot_id"),
                            rs.getString("przedmiot"),
                            rs.getInt("ects"),
                            rs.getInt("semestr_id"),
                            rs.getString("semestr"),
                            toDouble(rs.getObject("ocena")),
                            rs.getBoolean("zaliczony")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int countConfiguredSubjectsForSemester(int semestrId) {
        String sql = "SELECT COUNT(*) FROM semestr_przedmiot WHERE semestr_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestrId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void synchronizujZaliczeniePrzedmiotow(int studentId, int semestrId) {
        String sql = """
                UPDATE student_przedmiot sp
                LEFT JOIN ocena o
                  ON o.student_id = sp.student_id
                 AND o.przedmiot_id = sp.przedmiot_id
                 AND o.semestr_id = sp.semestr_id
                SET sp.zaliczony = CASE
                    WHEN o.ocena >= 3.0 THEN TRUE
                    ELSE FALSE
                END
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

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new IllegalStateException("Nieoczekiwany typ oceny z bazy: " + value.getClass().getName());
    }
}
