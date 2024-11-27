// LoginController.java
package com.sokoban.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button cancelButton;
    @FXML private Button registerButton;

    @FXML
    private void cancelRegister(ActionEvent event) throws Exception {
        switchToLogin();
    }

    @FXML
    private void confirmRegister(ActionEvent event) throws Exception {
        if (validRegister()) {
            // 切换到登记选择
            switchToLevelSelect();
        }
    }


    private boolean validRegister() {
        return true;
    }

    private void switchToLevelSelect() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/LevelSelectView.fxml"));
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void switchToLogin() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}