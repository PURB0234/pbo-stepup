package com.view;

import com.api.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardView {
    private BorderPane root;
    private VBox contentArea;
    private Button activeButton;

    public DashboardView() {

        root = new BorderPane();

        // ========================
        // SIDEBAR (Kiri)
        // ========================
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(0));
        sidebar.setStyle("-fx-background-color: #1a1a2e;");

        // Sidebar Header
        VBox sidebarHeader = new VBox(5);
        sidebarHeader.setPadding(new Insets(20, 15, 20, 15));
        sidebarHeader.setStyle("-fx-background-color: #16213e;");

        Label lblAppName = new Label("STEPUP");
        lblAppName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblAppName.setStyle("-fx-text-fill: #1a73e8;");

        Label lblRole = new Label("Admin Panel");
        lblRole.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        String currentName = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getNama()
                : "Admin";
        Label lblWelcome = new Label("Halo, " + currentName);
        lblWelcome.setStyle("-fx-text-fill: #ccc; -fx-font-size: 13px;");

        sidebarHeader.getChildren().addAll(lblAppName, lblRole, lblWelcome);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #333;");

        // Menu Buttons
        VBox menuBox = new VBox(3);
        menuBox.setPadding(new Insets(10, 0, 0, 0));

        Button btnUsers = createMenuButton("👥  User Management");
        Button btnRewards = createMenuButton("🏆  Reward Management");
        Button btnFeed = createMenuButton("📢  Community Feed");

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("🚪  Logout");
        btnLogout.setPrefWidth(220);
        btnLogout.setPrefHeight(40);
        btnLogout.setAlignment(Pos.CENTER_LEFT);
        btnLogout.setPadding(new Insets(0, 0, 0, 15));
        btnLogout.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ea4335;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;");

        menuBox.getChildren().addAll(btnUsers, btnRewards, btnFeed);

        sidebar.getChildren().addAll(sidebarHeader, sep, menuBox, spacer, btnLogout);

        // ========================
        // CONTENT AREA (Tengah)
        // ========================
        contentArea = new VBox();
        contentArea.setStyle("-fx-background-color: #f0f2f5;");

        // ========================
        // TOP BAR
        // ========================
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white;" +
                "-fx-border-color: #eee;" +
                "-fx-border-width: 0 0 1 0;");

        Label lblPageTitle = new Label("Dashboard Admin");
        lblPageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblPageTitle.setStyle("-fx-text-fill: #333;");

        topBar.getChildren().add(lblPageTitle);

        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        // ========================
        // EVENT HANDLERS
        // ========================
        btnUsers.setOnAction(e -> {
            setActiveButton(btnUsers);
            lblPageTitle.setText("User Management");
            UserManagementView userView = new UserManagementView();
            contentArea.getChildren().setAll(userView.getView());
            VBox.setVgrow(userView.getView(), Priority.ALWAYS);
        });

        btnRewards.setOnAction(e -> {
            setActiveButton(btnRewards);
            lblPageTitle.setText("Reward Management");
            RewardManagementView rewardView = new RewardManagementView();
            contentArea.getChildren().setAll(rewardView.getView());
            VBox.setVgrow(rewardView.getView(), Priority.ALWAYS);
        });

        btnFeed.setOnAction(e -> {
            setActiveButton(btnFeed);
            lblPageTitle.setText("Community Feed");
            CommunityFeedView feedView = new CommunityFeedView();
            contentArea.getChildren().setAll(feedView.getView());
            VBox.setVgrow(feedView.getView(), Priority.ALWAYS);
        });

        btnLogout.setOnAction(e -> {
            SessionManager.getInstance().logout();
            LoginView loginView = new LoginView();
            Stage stage = (Stage) root.getScene().getWindow();
            Scene loginScene = new Scene(loginView.getView(), 900, 600);
            stage.setTitle("StepUp Login");
            stage.setScene(loginScene);
        });

        // Default: buka User Management
        btnUsers.fire();
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.setPrefHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 15));
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #bbb;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;");

        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) {
                btn.setStyle(
                        "-fx-background-color: #16213e;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: transparent;");
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != activeButton) {
                btn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #bbb;" +
                                "-fx-font-size: 14px;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: transparent;");
            }
        });

        return btn;
    }

    private void setActiveButton(Button btn) {
        // Reset previous active
        if (activeButton != null) {
            activeButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #bbb;" +
                            "-fx-font-size: 14px;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: transparent;");
        }

        // Set new active
        activeButton = btn;
        activeButton.setStyle(
                "-fx-background-color: #1a73e8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-background-radius: 0;");
    }

    public Parent getView() {
        return root;
    }
}
