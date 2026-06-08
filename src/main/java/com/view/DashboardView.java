package com.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class DashboardView {
    private VBox root;

    public DashboardView() {

        root = new VBox(20);

        root.setAlignment(Pos.CENTER);

        Button btnUser = new Button("User Management");

        Button btnReward = new Button("Reward Management");

        Button btnLogout = new Button("Logout");

        root.getChildren().addAll(
                btnUser,
                btnReward,
                btnLogout);
    }

    public Parent getView() {
        return root;
    }
}
