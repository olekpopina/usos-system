package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.Przedmiot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrzedmiotRepository {

    public List<Przedmiot> getAllPrzedmioty() {
        List<Przedmiot> przedmioty = new ArrayList<>();
        String sql = """
                SELECT p.id,
                       p.nazwa,
                       p.ects,
                       COALESCE(GROUP_CONCAT(s.nazwa ORDER BY s.numer SEPARATOR ', '), '-') AS semestry
                FROM przedmiot p
                LEFT JOIN semestr_przedmiot sp ON sp.przedmiot_id = p.id
                LEFT JOIN semestr s ON s.id = sp.semestr_id
                GROUP BY p.id, p.nazwa, p.ects
                ORDER BY p.id
                """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                przedmioty.add(new Przedmiot(
                        rs.getInt("id"),
                        rs.getString("nazwa"),
                        rs.getInt("ects"),
                        rs.getString("semestry")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return przedmioty;
    }

    public List<Przedmiot> getPrzedmiotyDlaSemestru(int semestrId) {
        List<Przedmiot> przedmioty = new ArrayList<>();
        String sql = """
                SELECT p.id, p.nazwa, p.ects, sem.nazwa AS semestry
                FROM semestr_przedmiot sp
                JOIN przedmiot p ON p.id = sp.przedmiot_id
                JOIN semestr sem ON sem.id = sp.semestr_id
                WHERE sp.semestr_id = ?
                ORDER BY p.nazwa
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestrId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    przedmioty.add(new Przedmiot(
                            rs.getInt("id"),
                            rs.getString("nazwa"),
                            rs.getInt("ects"),
                            rs.getString("semestry")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return przedmioty;
    }

    public void addPrzedmiot(String nazwa, int ects) {
        String sql = "INSERT INTO przedmiot (nazwa, ects) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nazwa);
            stmt.setInt(2, ects);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePrzedmiot(int id, String nazwa, int ects) {
        String sql = "UPDATE przedmiot SET nazwa = ?, ects = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nazwa);
            stmt.setInt(2, ects);
            stmt.setInt(3, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePrzedmiot(int id) {
        String sql = "DELETE FROM przedmiot WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
