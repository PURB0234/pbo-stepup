module com.stepup {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.stepup to javafx.fxml;
    exports com.stepup;
}
