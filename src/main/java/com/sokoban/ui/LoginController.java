// LoginController.java
package com.sokoban.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    private void handleLogin(ActionEvent event) throws Exception {
        // 验证登录逻辑
        if (validateLogin()) {
            switchToLevelSelect();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws Exception {
        // 切换到注册界面
        Parent root = FXMLLoader.load(getClass().getResource("/views/RegisterView.fxml"));
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private boolean validateLogin() {
        // 实现登录验证逻辑
        return true;
    }

    private void switchToLevelSelect() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/LevelSelectView.fxml"));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}