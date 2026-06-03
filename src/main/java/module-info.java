module com.stepup {
    requires javafx.controls;
    requires javafx.fxml;
     requires javafx.graphics;
     requires java.sql;
    requires mysql.connector.j;

    opens com.stepup to javafx.fxml;
    exports com.stepup;
}
