module pl.usos.usossystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens pl.usos.usossystem to javafx.fxml;
    opens pl.usos.usossystem.model to javafx.base;
    opens pl.usos.usossystem.config to javafx.fxml;

    exports pl.usos.usossystem;
    exports pl.usos.usossystem.model;
    exports pl.usos.usossystem.config;
    exports pl.usos.usossystem.repository;
}