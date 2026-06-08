package com.view;

import com.dao.UserDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private VBox root;

    public LoginView() {

        root = new VBox(15);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label title = new Label("STEPUP LOGIN");

        title.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;");

        TextField txtNama = new TextField();

        txtNama.setPromptText("Nama");

        txtNama.setMaxWidth(250);

        TextField txtEmail = new TextField();

        txtEmail.setPromptText("Email");

        txtEmail.setMaxWidth(250);

        PasswordField txtPassword = new PasswordField();

        txtPassword.setPromptText("Password");

        txtPassword.setMaxWidth(250);

        Button btnLogin = new Button("LOGIN");

        btnLogin.setPrefWidth(250);

        btnLogin.setOnAction(e -> {

            String nama = txtNama.getText().trim();

            String email = txtEmail.getText().trim();

            String password = txtPassword.getText().trim();

            if (nama.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()) {

                Alert alert = new Alert(
                        Alert.AlertType.WARNING);

                alert.setHeaderText(null);

                alert.setContentText(
                        "Semua field wajib diisi!");

                alert.showAndWait();

                return;
            }

            UserDAO dao = new UserDAO();

            boolean loginBerhasil = dao.login(
                    nama,
                    email,
                    password);

            if (loginBerhasil) {

                DashboardView dashboard = new DashboardView();

                Stage stage = (Stage) root
                        .getScene()
                        .getWindow();

                Scene dashboardScene = new Scene(
                        dashboard.getView(),
                        1000,
                        700);

                stage.setScene(
                        dashboardScene);

            } else {

                Alert alert = new Alert(
                        Alert.AlertType.ERROR);

                alert.setHeaderText(null);

                alert.setContentText(
                        "Nama, Email, atau Password Salah!");

                alert.showAndWait();
            }
        });

        root.getChildren().addAll(
                title,
                txtNama,
                txtEmail,
                txtPassword,
                btnLogin);
    }

    public Parent getView() {
        return root;
    }
}
