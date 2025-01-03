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
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button registerButton;

	private static final String SERVER_IP = "localhost";
	private static final int SERVER_PORT = 8888;

	private User user;

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
		user = new User("visitor", "visitor");
		user.syncData();
		user.updateLevel(1);
		switchToLevelSelect();
	}

	private boolean validateLogin(PrintWriter out, BufferedReader in) throws IOException {
		out.println("LOGIN|" + usernameField.getText() + "|" + passwordField.getText());
		user = new User(usernameField.getText(), passwordField.getText());
		String response = in.readLine();
		String[] parts = response.split("\\|");

		if ("SUCCESS".equals(parts[0])) {
			System.out.println("登录成功！");
			System.out.println("得分：" + parts[1]);
			System.out.println("昵称：" + parts[2]);
			System.out.println("等级：" + parts[3]);
			user.setNickname(parts[2]);
			user.setLevel(Integer.parseInt(parts[3]));
			return true;
		} else {
			System.out.println("登录失败：" + parts[1]);
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
			alert.setTitle("Login Failed");
			alert.setHeaderText(null);
			alert.setContentText("Invalid username or password");
			alert.showAndWait();
			return false;
		}
	}

	private void switchToLevelSelect() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LevelSelectView.fxml"));
		Parent root = loader.load();
		LevelSelector controller = loader.getController();
		controller.setUser(user);
		Stage stage = (Stage) loginButton.getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.show();
	}
}