package com.view;

import com.api.UserService;
import com.models.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

/**
 * View untuk Admin: mengelola data User dengan TableView.
 * CRUD: lihat, edit role/status, hapus user.
 */
public class UserManagementView {
    private VBox root;
    private TableView<User> tableView;
    private ObservableList<User> userData;

    public UserManagementView() {

        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("User Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(
                "-fx-background-color: #1a73e8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadData());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, btnRefresh);

        // Table
        tableView = new TableView<>();
        tableView.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #ddd;" +
                "-fx-border-radius: 5;");
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Kolom ID
        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        // Kolom Nama
        TableColumn<User, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(150);

        // Kolom Email
        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        // Kolom NIM
        TableColumn<User, String> colNim = new TableColumn<>("NIM");
        colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        colNim.setPrefWidth(120);

        // Kolom Role
        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(80);
        colRole.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("admin".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #1a73e8; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #666;");
                    }
                }
            }
        });

        // Kolom Status
        TableColumn<User, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(80);
        colStatus.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("active".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #34a853; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ea4335; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Kolom Aksi
        TableColumn<User, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(180);
        colAksi.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Hapus");
            private final HBox box = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setStyle(
                        "-fx-background-color: #fbbc04;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;");

                btnDelete.setStyle(
                        "-fx-background-color: #ea4335;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;");

                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditDialog(user);
                });

                btnDelete.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        tableView.getColumns().addAll(colId, colNama, colEmail, colNim, colRole, colStatus, colAksi);

        // Info label
        Label lblInfo = new Label("Data user diambil dari API WEBPRO-STEPUP");
        lblInfo.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        root.getChildren().addAll(header, tableView, lblInfo);

        // Load data awal
        loadData();
    }

    private void loadData() {
        List<User> users = UserService.getAllUsers();
        userData = FXCollections.observableArrayList(users);
        tableView.setItems(userData);
    }

    private void showEditDialog(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getNama());

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Role ComboBox
        Label lblRole = new Label("Role:");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("user", "admin");
        cbRole.setValue(user.getRole());
        cbRole.setPrefWidth(200);

        // Status ComboBox
        Label lblStatus = new Label("Status:");
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("active", "inactive");
        cbStatus.setValue(user.getStatus() != null ? user.getStatus() : "active");
        cbStatus.setPrefWidth(200);

        content.getChildren().addAll(lblRole, cbRole, lblStatus, cbStatus);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = UserService.updateUser(
                    user.getId(),
                    cbRole.getValue(),
                    cbStatus.getValue());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "User berhasil diupdate!");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal mengupdate user.");
            }
        }
    }

    private void handleDelete(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Hapus user \"" + user.getNama() + "\"?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = UserService.deleteUser(user.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "User berhasil dihapus!");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal menghapus user.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}
