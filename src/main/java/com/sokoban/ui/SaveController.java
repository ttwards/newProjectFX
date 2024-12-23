package com.sokoban.ui;

import javafx.scene.control.Alert;
import javafx.scene.Scene;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import com.sokoban.controllers.Backend;

public class SaveController implements Initializable {
	@FXML private Button confirm;
	@FXML private Button cancel;
	@FXML private TextField nameBox;
	
	private Backend map;

	private String name;

	private User user;

	@FXML
	private void handleConfirm() {
		if(nameBox.getText().isEmpty()) {
			// 提示用户输入存档名称
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please enter a save name.");
			alert.showAndWait();
			return;
		}
		try {
			if(map.saveMap(nameBox.getText(), user.getUsername())) {
				// 提示用户存档成功
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Information");
				alert.setHeaderText(null);
				alert.setContentText("Save successfully.");
				alert.showAndWait();
			} else {
				// 提示用户存档失败	
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Save failed.");
				alert.showAndWait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCancel() {
		Scene scene = cancel.getScene();
		scene.getWindow().hide();
	}

	public void setMap(Backend map) {
		this.map = map;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public void initialize(URL location, ResourceBundle resources) {
		// 设置列的单元格值工厂
        nameBox.setText(name);
	}

	public void setUser(User user) {
		this.user = user;
	}
}
