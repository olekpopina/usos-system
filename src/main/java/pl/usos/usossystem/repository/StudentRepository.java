package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getInt("indeks")
                );
                students.add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public void addStudent(String imie, String nazwisko, int indeks) {
        String sql = "INSERT INTO student (imie, nazwisko, indeks) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, imie);
            stmt.setString(2, nazwisko);
            stmt.setInt(3, indeks);
            stmt.executeUpdate();

            System.out.println("Dodano studenta.");

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
            System.out.println("Zaktualizowano studenta.");

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

            System.out.println("Usunieto studenta.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}