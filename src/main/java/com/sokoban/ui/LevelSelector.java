package com.sokoban.ui;

import com.sokoban.controllers.SokobanGame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LevelSelector {
	@FXML
	private Button level1Button;
	@FXML
	private Button level2Button;
	@FXML
	private Button level3Button;
	@FXML
	private Button level4Button;
	@FXML
	private Button level5Button;
	@FXML
	private Button level6Button;

	private User user;

	@FXML
	public void levelSelect1(ActionEvent event) throws Exception {
		Stage stage = (Stage) level1Button.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();
		sokobanGame.setLevel(1);
		sokobanGame.setUser(user);
		sokobanGame.start(new Stage());
	}

	@FXML
	public void levelSelect2(ActionEvent event) throws Exception {
		Stage stage = (Stage) level1Button.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();
		sokobanGame.setLevel(2);
		sokobanGame.setUser(user);
		sokobanGame.start(new Stage());
	}

	@FXML
	public void levelSelect3(ActionEvent event) throws Exception {
		Stage stage = (Stage) level1Button.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();
		sokobanGame.setLevel(3);
		sokobanGame.setUser(user);
		sokobanGame.start(new Stage());
	}

	@FXML
	public void levelSelect4(ActionEvent event) throws Exception {
		Stage stage = (Stage) level1Button.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();
		sokobanGame.setLevel(4);
		sokobanGame.setUser(user);
		sokobanGame.start(new Stage());
	}

	@FXML
	public void levelSelect5(ActionEvent event) throws Exception {
		Stage stage = (Stage) level1Button.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();
		sokobanGame.setLevel(5);
		sokobanGame.setUser(user);
		sokobanGame.start(new Stage());
	}

	@FXML
	public void levelSelect6(ActionEvent event) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/views/StorageSelect.fxml"));
		Stage stage = (Stage) level6Button.getScene().getWindow();
		stage.setScene(new Scene(root));
	}

	public void setUser(User user) {
		this.user = user;
		int level = user.getLevel();
		switch (level) {
			case 1:
				level2Button.setDisable(true);
				level3Button.setDisable(true);
				level4Button.setDisable(true);
				level5Button.setDisable(true);
				// level6Button.setDisable(true);
				break;

			case 2:
				level3Button.setDisable(true);
				level4Button.setDisable(true);
				level5Button.setDisable(true);
				// level6Button.setDisable(true);
				break;

			case 3:
				level4Button.setDisable(true);
				level5Button.setDisable(true);
				// level6Button.setDisable(true);
				break;

			case 4:
				level5Button.setDisable(true);
				break;

			case 5:
				break;

			default:
				level2Button.setDisable(true);
				level3Button.setDisable(true);
				level4Button.setDisable(true);
				level5Button.setDisable(true);
				// level6Button.setDisable(true);
				break;
		}
	}
}
