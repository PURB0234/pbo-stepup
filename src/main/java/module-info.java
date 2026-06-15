module com.stepup {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.net.http;
    requires mysql.connector.j;
    requires jbcrypt;
    requires com.google.gson;

    opens com.stepup to javafx.fxml;
    opens com.models to com.google.gson, javafx.base;
    exports com.stepup;
    exports com.view;
    exports com.api;
    exports com.models;
}
