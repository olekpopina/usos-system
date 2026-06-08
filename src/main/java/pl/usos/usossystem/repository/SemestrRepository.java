package pl.usos.usossystem.repository;

import pl.usos.usossystem.config.DatabaseConnection;
import pl.usos.usossystem.model.Semestr;
import pl.usos.usossystem.service.SemesterGateway;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SemestrRepository implements SemesterGateway {

    public List<Semestr> getAllSemestry() {
        List<Semestr> list = new ArrayList<>();
        String sql = "SELECT * FROM semestr ORDER BY numer";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Semestr(
                        rs.getInt("id"),
                        rs.getInt("numer"),
                        rs.getString("nazwa")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Semestr getNextSemestr(int currentNumer) {
        String sql = "SELECT * FROM semestr WHERE numer = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentNumer + 1);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Semestr(
                        rs.getInt("id"),
                        rs.getInt("numer"),
                        rs.getString("nazwa")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Semestr getSemestrById(int semestrId) {
        String sql = "SELECT * FROM semestr WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestrId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Semestr(
                            rs.getInt("id"),
                            rs.getInt("numer"),
                            rs.getString("nazwa")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
