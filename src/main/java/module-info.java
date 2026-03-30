module pl.usos.usossystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.usos.usossystem to javafx.fxml;
    exports pl.usos.usossystem;
}