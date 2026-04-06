package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT s.id, s.imie, s.nazwisko, s.indeks, s.haslo,
                       s.aktualny_semestr_id, s.status_semestru,
                       sem.nazwa AS semestr_nazwa
                FROM student s
                LEFT JOIN semestr sem ON s.aktualny_semestr_id = sem.id
                ORDER BY s.id
                """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getInt("indeks"),
                        rs.getString("haslo"),
                        (Integer) rs.getObject("aktualny_semestr_id"),
                        rs.getString("status_semestru")
                );
                s.setAktualnySemestrNazwa(rs.getString("semestr_nazwa"));
                students.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public void addStudent(String imie, String nazwisko, int indeks) {
        String sql = "INSERT INTO student (imie, nazwisko, indeks, haslo, status_semestru) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setInt(3, indeks);
            stmt.setString(4, "student");
            stmt.setString(5, "W trakcie");
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudent(int id, String imie, String nazwisko, int indeks) {
        String sql = "UPDATE student SET imie = ?, nazwisko = ?, indeks = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setInt(3, indeks);
            stmt.setInt(4, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Student loginStudent(int indeks, String haslo) {
        String sql = """
                SELECT s.id, s.imie, s.nazwisko, s.indeks, s.haslo,
                       s.aktualny_semestr_id, s.status_semestru,
                       sem.nazwa AS semestr_nazwa
                FROM student s
                LEFT JOIN semestr sem ON s.aktualny_semestr_id = sem.id
                WHERE s.indeks = ? AND s.haslo = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indeks);
            stmt.setString(2, haslo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getInt("indeks"),
                        rs.getString("haslo"),
                        (Integer) rs.getObject("aktualny_semestr_id"),
                        rs.getString("status_semestru")
                );
                s.setAktualnySemestrNazwa(rs.getString("semestr_nazwa"));
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setStudentSemestr(int studentId, int semestrId) {
        String sql = "UPDATE student SET aktualny_semestr_id = ?, status_semestru = 'W trakcie' WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestrId);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStatusSemestru(int studentId, String status) {
        String sql = "UPDATE student SET status_semestru = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}