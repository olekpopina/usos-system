package pl.usos.usossystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/usos_db";
    private static final String SERVER_URL = "jdbc:mysql://127.0.0.1:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "usos";
    private static final Path INIT_SQL_PATH = Path.of("database", "init.sql");
    private static boolean initialized;

    public static Connection connect() {
        initializeDatabaseIfNeeded();

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Polaczono z baza danych MySQL.");
            return connection;
        } catch (SQLException e) {
            System.out.println("Blad polaczenia z baza danych.");
            e.printStackTrace();
            throw new IllegalStateException("Nie mozna polaczyc sie z baza danych MySQL.", e);
        }
    }

    private static synchronized void initializeDatabaseIfNeeded() {
        if (initialized) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD)) {
            for (String statement : loadSqlStatements()) {
                try (Statement sqlStatement = connection.createStatement()) {
                    sqlStatement.execute(statement);
                }
            }
            initialized = true;
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Nie mozna przygotowac bazy danych usos_db.", e);
        }
    }

    private static List<String> loadSqlStatements() throws IOException {
        String sqlScript;

        if (Files.exists(INIT_SQL_PATH)) {
            sqlScript = Files.readString(INIT_SQL_PATH, StandardCharsets.UTF_8);
        } else {
            try (InputStream stream = DatabaseConnection.class.getClassLoader().getResourceAsStream("database/init.sql")) {
                if (stream == null) {
                    throw new IOException("Nie znaleziono pliku init.sql.");
                }
                sqlScript = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        return splitStatements(sqlScript);
    }

    private static List<String> splitStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : sqlScript.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }

            current.append(line).append('\n');

            if (trimmed.endsWith(";")) {
                String statement = current.toString().trim();
                statements.add(statement.substring(0, statement.length() - 1));
                current.setLength(0);
            }
        }

        if (current.length() > 0) {
            statements.add(current.toString().trim());
        }

        return statements;
    }
}
