package com.view;

import com.api.AuthService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterView {
    private VBox root;

    public RegisterView() {

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Title
        Label title = new Label("STEPUP");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #1a73e8;");

        Label subtitle = new Label("Buat akun baru");
        subtitle.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");

        // Form Container
        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(350);
        formBox.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        TextField txtNama = new TextField();
        txtNama.setPromptText("Nama Lengkap");
        txtNama.setMaxWidth(280);
        txtNama.setStyle(
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #ddd;");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        txtEmail.setMaxWidth(280);
        txtEmail.setStyle(
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #ddd;");

        TextField txtNim = new TextField();
        txtNim.setPromptText("NIM");
        txtNim.setMaxWidth(280);
        txtNim.setStyle(
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #ddd;");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setMaxWidth(280);
        txtPassword.setStyle(
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #ddd;");

        Button btnRegister = new Button("DAFTAR");
        btnRegister.setPrefWidth(280);
        btnRegister.setPrefHeight(40);
        btnRegister.setStyle(
                "-fx-background-color: #34a853;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");

        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        btnRegister.setOnAction(e -> {

            String nama = txtNama.getText().trim();
            String email = txtEmail.getText().trim();
            String nim = txtNim.getText().trim();
            String password = txtPassword.getText().trim();

            if (nama.isEmpty() || email.isEmpty()
                    || nim.isEmpty() || password.isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Semua field wajib diisi!");
                alert.showAndWait();
                return;
            }

            lblStatus.setText("Mendaftarkan akun...");
            lblStatus.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

            boolean success = AuthService.register(nama, email, password, nim);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Registrasi berhasil! Silakan login.");
                alert.showAndWait();

                // Navigate ke LoginView
                LoginView loginView = new LoginView();
                Stage stage = (Stage) root.getScene().getWindow();
                Scene loginScene = new Scene(loginView.getView(), 900, 600);
                stage.setTitle("StepUp Login");
                stage.setScene(loginScene);

            } else {
                String msg = AuthService.getRegisterMessage(nama, email, password, nim);
                lblStatus.setText(msg);
                lblStatus.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            }
        });

        formBox.getChildren().addAll(
                txtNama,
                txtEmail,
                txtNim,
                txtPassword,
                btnRegister,
                lblStatus);

        // Login link
        HBox loginBox = new HBox(5);
        loginBox.setAlignment(Pos.CENTER);

        Label lblHaveAccount = new Label("Sudah punya akun?");
        lblHaveAccount.setStyle("-fx-text-fill: #666;");

        Hyperlink linkLogin = new Hyperlink("Login di sini");
        linkLogin.setStyle("-fx-text-fill: #1a73e8; -fx-font-weight: bold;");

        linkLogin.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Stage stage = (Stage) root.getScene().getWindow();
            Scene loginScene = new Scene(loginView.getView(), 900, 600);
            stage.setTitle("StepUp Login");
            stage.setScene(loginScene);
        });

        loginBox.getChildren().addAll(lblHaveAccount, linkLogin);

        root.getChildren().addAll(
                title,
                subtitle,
                formBox,
                loginBox);
    }

    public Parent getView() {
        return root;
    }
}
