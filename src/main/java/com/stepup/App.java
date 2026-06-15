package com.stepup;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.view.LoginView;

/**
 * JavaFX App - StepUp Desktop Application.
 * Terhubung ke API PHP WEBPRO-STEPUP untuk autentikasi dan data management.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        LoginView loginView = new LoginView();

        Scene scene = new Scene(loginView.getView(), 900, 600);

        stage.setTitle("StepUp Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}