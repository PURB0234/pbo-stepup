package com.view;

import com.api.RewardService;
import com.api.SessionManager;
import com.models.Reward;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

/**
 * View untuk mengelola data Reward.
 * Admin: CRUD lengkap (tambah, hapus).
 * User biasa: view only.
 */
public class RewardManagementView {
    private VBox root;
    private TableView<Reward> tableView;
    private ObservableList<Reward> rewardData;
    private boolean isAdmin;
    private Label lblPoinBalance;

    public RewardManagementView() {
        this.isAdmin = SessionManager.getInstance().isAdmin();

        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Reward Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        lblPoinBalance = new Label();
        updatePoinDisplay();
        lblPoinBalance.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblPoinBalance.setStyle("-fx-background-color: #e8f0fe; -fx-text-fill: #1a73e8; -fx-padding: 6 12; -fx-background-radius: 15;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(
                "-fx-background-color: #1a73e8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadData());

        header.getChildren().addAll(title, lblPoinBalance, spacer, btnRefresh);

        if (!isAdmin) {
            Button btnHistory = new Button("📋 Riwayat Redeem");
            btnHistory.setStyle(
                    "-fx-background-color: #1a1a2e;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 8 16;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;");
            btnHistory.setOnAction(e -> showHistoryDialog());
            header.getChildren().add(header.getChildren().size() - 1, btnHistory);
        }

        // Admin: Tambah Reward button
        if (isAdmin) {
            Button btnTambah = new Button("+ Tambah Reward");
            btnTambah.setStyle(
                    "-fx-background-color: #34a853;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 8 16;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;");
            btnTambah.setOnAction(e -> showAddDialog());

            header.getChildren().add(header.getChildren().size() - 1, btnTambah);
        }

        // Table
        tableView = new TableView<>();
        tableView.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #ddd;" +
                "-fx-border-radius: 5;");
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Kolom ID
        TableColumn<Reward, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idReward"));
        colId.setPrefWidth(60);

        // Kolom Nama
        TableColumn<Reward, String> colNama = new TableColumn<>("Nama Reward");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nameReward"));
        colNama.setPrefWidth(200);

        // Kolom Deskripsi
        TableColumn<Reward, String> colDesc = new TableColumn<>("Deskripsi");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(300);

        // Kolom Poin
        TableColumn<Reward, Integer> colPoin = new TableColumn<>("Poin");
        colPoin.setCellValueFactory(new PropertyValueFactory<>("poin"));
        colPoin.setPrefWidth(80);
        colPoin.setCellFactory(col -> new TableCell<Reward, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item) + " pts");
                    setStyle("-fx-text-fill: #1a73e8; -fx-font-weight: bold;");
                }
            }
        });

        // Kolom Stok
        TableColumn<Reward, Integer> colStok = new TableColumn<>("Stok");
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colStok.setPrefWidth(80);
        colStok.setCellFactory(col -> new TableCell<Reward, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    if (item <= 0) {
                        setStyle("-fx-text-fill: #ea4335; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #34a853; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tableView.getColumns().addAll(colId, colNama, colDesc, colPoin, colStok);

        // Kolom Aksi (Admin: Hapus, User: Redeem)
        TableColumn<Reward, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(120);
        colAksi.setCellFactory(col -> new TableCell<Reward, Void>() {
            private final Button btnAction = new Button();

            {
                if (isAdmin) {
                    btnAction.setText("Hapus");
                    btnAction.setStyle(
                            "-fx-background-color: #ea4335;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 11px;" +
                            "-fx-padding: 4 12;" +
                            "-fx-background-radius: 3;" +
                            "-fx-cursor: hand;");
                    btnAction.setOnAction(e -> {
                        Reward reward = getTableView().getItems().get(getIndex());
                        handleDelete(reward);
                    });
                } else {
                    btnAction.setText("Redeem");
                    btnAction.setStyle(
                            "-fx-background-color: #1a73e8;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 11px;" +
                            "-fx-padding: 4 12;" +
                            "-fx-background-radius: 3;" +
                            "-fx-cursor: hand;");
                    btnAction.setOnAction(e -> {
                        Reward reward = getTableView().getItems().get(getIndex());
                        handleRedeem(reward);
                    });
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (!isAdmin) {
                        Reward reward = getTableView().getItems().get(getIndex());
                        int userPoin = SessionManager.getInstance().getCurrentUser() != null 
                                ? SessionManager.getInstance().getCurrentUser().getPoin() 
                                : 0;
                        if (reward.getStok() <= 0) {
                            btnAction.setDisable(true);
                            btnAction.setText("Habis");
                            btnAction.setStyle(
                                    "-fx-background-color: #cbd5e1;" +
                                    "-fx-text-fill: #94a3b8;" +
                                    "-fx-font-size: 11px;" +
                                    "-fx-padding: 4 12;" +
                                    "-fx-background-radius: 3;");
                        } else if (userPoin < reward.getPoin()) {
                            btnAction.setDisable(true);
                            btnAction.setText("Poin Kurang");
                            btnAction.setStyle(
                                    "-fx-background-color: #cbd5e1;" +
                                    "-fx-text-fill: #94a3b8;" +
                                    "-fx-font-size: 11px;" +
                                    "-fx-padding: 4 12;" +
                                    "-fx-background-radius: 3;");
                        } else {
                            btnAction.setDisable(false);
                            btnAction.setText("Redeem");
                            btnAction.setStyle(
                                    "-fx-background-color: #1a73e8;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 11px;" +
                                    "-fx-padding: 4 12;" +
                                    "-fx-background-radius: 3;" +
                                    "-fx-cursor: hand;");
                        }
                    }
                    setGraphic(btnAction);
                }
            }
        });

        tableView.getColumns().add(colAksi);

        // Info label
        String infoText = isAdmin
                ? "Admin: Anda bisa menambah dan menghapus reward"
                : "Daftar reward yang tersedia";
        Label lblInfo = new Label(infoText);
        lblInfo.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        root.getChildren().addAll(header, tableView, lblInfo);

        // Load data awal
        loadData();
    }

    private void loadData() {
        List<Reward> rewards = RewardService.getAllRewards();
        rewardData = FXCollections.observableArrayList(rewards);
        tableView.setItems(rewardData);

        // Sync points from server
        if (SessionManager.getInstance().getCurrentUser() != null) {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            JsonObject historyResponse = RewardService.getRedemptionHistory(userId);
            if (historyResponse.has("success") && historyResponse.get("success").getAsBoolean()) {
                int currentPoin = historyResponse.get("poin_user").getAsInt();
                SessionManager.getInstance().getCurrentUser().setPoin(currentPoin);
            }
        }
        updatePoinDisplay();
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tambah Reward");
        dialog.setHeaderText("Tambah reward baru");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label lblNama = new Label("Nama Reward:");
        TextField txtNama = new TextField();
        txtNama.setPromptText("Masukkan nama reward");
        txtNama.setPrefWidth(300);

        Label lblDesc = new Label("Deskripsi:");
        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Masukkan deskripsi reward");
        txtDesc.setPrefWidth(300);
        txtDesc.setPrefHeight(80);

        Label lblPoin = new Label("Poin:");
        Spinner<Integer> spPoin = new Spinner<>();
        spPoin.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999, 100, 50));
        spPoin.setPrefWidth(150);
        spPoin.setEditable(true);

        Label lblStok = new Label("Stok:");
        Spinner<Integer> spStok = new Spinner<>();
        spStok.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 10, 1));
        spStok.setPrefWidth(150);
        spStok.setEditable(true);

        content.getChildren().addAll(
                lblNama, txtNama,
                lblDesc, txtDesc,
                lblPoin, spPoin,
                lblStok, spStok);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String nama = txtNama.getText().trim();
            String desc = txtDesc.getText().trim();
            int poin = spPoin.getValue();
            int stok = spStok.getValue();

            if (nama.isEmpty() || desc.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Nama dan deskripsi wajib diisi!");
                return;
            }

            boolean success = RewardService.addReward(nama, poin, desc, stok);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Reward berhasil ditambahkan!");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal menambahkan reward.");
            }
        }
    }

    private void handleDelete(Reward reward) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Hapus reward \"" + reward.getNameReward() + "\"?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = RewardService.deleteReward(reward.getIdReward());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Reward berhasil dihapus!");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal menghapus reward.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePoinDisplay() {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            int currentPoin = SessionManager.getInstance().getCurrentUser().getPoin();
            lblPoinBalance.setText("Saldo Poin: " + String.format("%,d", currentPoin) + " pts");
        } else {
            lblPoinBalance.setText("");
        }
    }

    private void handleRedeem(Reward reward) {
        int userId = SessionManager.getInstance().getCurrentUser() != null 
                ? SessionManager.getInstance().getCurrentUser().getId() 
                : 0;

        if (userId <= 0) {
            showAlert(Alert.AlertType.WARNING, "User session tidak valid.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Redeem");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menukarkan " + reward.getPoin() + " poin untuk \"" + reward.getNameReward() + "\"?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            JsonObject response = RewardService.redeemReward(userId, reward.getIdReward());

            if (response.has("success") && response.get("success").getAsBoolean()) {
                String successMsg = response.get("message").getAsString();
                showAlert(Alert.AlertType.INFORMATION, successMsg);

                // Update session points
                int sisaPoin = response.getAsJsonObject("data").get("sisa_poin").getAsInt();
                SessionManager.getInstance().getCurrentUser().setPoin(sisaPoin);
                updatePoinDisplay();

                // Refresh table
                loadData();
            } else {
                String errorMsg = response.has("message") ? response.get("message").getAsString() : "Gagal melakukan redeem.";
                showAlert(Alert.AlertType.ERROR, errorMsg);
            }
        }
    }

    private void showHistoryDialog() {
        int userId = SessionManager.getInstance().getCurrentUser() != null 
                ? SessionManager.getInstance().getCurrentUser().getId() 
                : 0;

        if (userId <= 0) {
            showAlert(Alert.AlertType.WARNING, "User session tidak valid.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Riwayat Redeem");
        dialog.setHeaderText("📋 Riwayat Penukaran Reward Anda");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setPrefSize(450, 350);

        TableView<RedemptionRecord> table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<RedemptionRecord, String> colNama = new TableColumn<>("Nama Reward");
        colNama.setCellValueFactory(new PropertyValueFactory<>("rewardName"));
        colNama.setPrefWidth(180);

        TableColumn<RedemptionRecord, Integer> colPoin = new TableColumn<>("Poin");
        colPoin.setCellValueFactory(new PropertyValueFactory<>("poinDigunakan"));
        colPoin.setPrefWidth(80);
        colPoin.setCellFactory(col -> new TableCell<RedemptionRecord, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("-" + String.valueOf(item) + " pts");
                    setStyle("-fx-text-fill: #ea4335; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<RedemptionRecord, String> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalRedeem"));
        colTanggal.setPrefWidth(150);

        table.getColumns().addAll(colNama, colPoin, colTanggal);

        // Fetch and load history data
        ObservableList<RedemptionRecord> historyData = FXCollections.observableArrayList();
        JsonObject response = RewardService.getRedemptionHistory(userId);

        if (response.has("success") && response.get("success").getAsBoolean()) {
            JsonArray arr = response.getAsJsonArray("data");
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.get(i).getAsJsonObject();
                historyData.add(new RedemptionRecord(
                        obj.get("nama_reward").getAsString(),
                        obj.get("poin_digunakan").getAsInt(),
                        obj.get("tanggal_redeem").getAsString()
                ));
            }
        }

        table.setItems(historyData);

        if (historyData.isEmpty()) {
            Label lblNoData = new Label("Belum ada riwayat redeem.");
            lblNoData.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            content.getChildren().addAll(lblNoData);
        } else {
            content.getChildren().addAll(table);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static class RedemptionRecord {
        private final String rewardName;
        private final int poinDigunakan;
        private final String tanggalRedeem;

        public RedemptionRecord(String rewardName, int poinDigunakan, String tanggalRedeem) {
            this.rewardName = rewardName;
            this.poinDigunakan = poinDigunakan;
            this.tanggalRedeem = tanggalRedeem;
        }

        public String getRewardName() { return rewardName; }
        public int getPoinDigunakan() { return poinDigunakan; }
        public String getTanggalRedeem() { return tanggalRedeem; }
    }

    public Parent getView() {
        return root;
    }
}
