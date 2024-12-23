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

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button cancelButton;
    @FXML private Button registerButton;

	private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    @FXML
    private void cancelRegister(ActionEvent event) throws Exception {
        switchToLogin();
    }

    @FXML
    private void confirmRegister() throws IOException, Exception {
		try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            if (validRegister(out, in)) {
				switchToLevelSelect();
			}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    private boolean validRegister(PrintWriter out, BufferedReader in) throws IOException, Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        out.println("REGISTER|" + username + "|" + password);
        String response = in.readLine();
        String[] parts = response.split("\\|");

        if ("SUCCESS".equals(parts[0])) {
			out.println("UPDATE|" + username + "|level|" + 0);
            String updateResponse = in.readLine();
            System.out.println(updateResponse.equals("UPDATE_SUCCESS") ? "更新成功！" : "更新失败！");
            User user = new User(username, password);
			user.updateLevel(1);
			return true;
        } else {
            System.out.println("注册失败：" + parts[1]);
			return false;
        }
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