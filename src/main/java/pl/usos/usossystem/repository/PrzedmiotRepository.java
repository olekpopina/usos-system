package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.Przedmiot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrzedmiotRepository {

    public List<Przedmiot> getAllPrzedmioty() {
        List<Przedmiot> przedmioty = new ArrayList<>();
        String sql = "SELECT * FROM przedmiot ORDER BY id";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                przedmioty.add(new Przedmiot(
                        rs.getInt("id"),
                        rs.getString("nazwa")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return przedmioty;
    }

    public void addPrzedmiot(String nazwa) {
        String sql = "INSERT INTO przedmiot (nazwa) VALUES (?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nazwa);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePrzedmiot(int id, String nazwa) {
        String sql = "UPDATE przedmiot SET nazwa = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nazwa);
            stmt.setInt(2, id);
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