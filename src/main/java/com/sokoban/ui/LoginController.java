// LoginController.java
package com.sokoban.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

	private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    @FXML
    private void handleLogin(ActionEvent event) throws Exception {
		try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            if (validateLogin(out, in)) {
				switchToLevelSelect();
			}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws Exception {
        // 切换到注册界面
        Parent root = FXMLLoader.load(getClass().getResource("/views/RegisterView.fxml"));
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

	@FXML
	private void handleVisitor(ActionEvent event) throws Exception {
		switchToLevelSelect();
	}

    private boolean validateLogin(PrintWriter out, BufferedReader in) throws IOException {
        out.println("LOGIN|" + usernameField.getText() + "|" + passwordField.getText());
        String response = in.readLine();
        String[] parts = response.split("\\|");

        if ("SUCCESS".equals(parts[0])) {
            System.out.println("登录成功！");
            System.out.println("得分：" + parts[1]);
            System.out.println("昵称：" + parts[2]);
            System.out.println("等级：" + parts[3]);
            return true;
        } else {
            System.out.println("登录失败：" + parts[1]);
            return false;
        }
    }

    private void switchToLevelSelect() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/LevelSelectView.fxml"));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}