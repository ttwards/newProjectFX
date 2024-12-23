package com.sokoban.ui;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import javafx.scene.control.TableColumn;
import javafx.fxml.Initializable;
import com.sokoban.controllers.Backend;
import com.sokoban.controllers.SokobanGame;

import java.net.URL;

public class StorageSelector implements Initializable {
	@FXML
	private DatePicker startDate;
	@FXML
	private DatePicker endDate;
	@FXML
	private Button confirmStorage;
	@FXML
	private Button cancelButton;
	@FXML
	private TableView<StorageInfo> storagedList;
	@FXML
	private TableColumn<StorageInfo, String> mapName;
	@FXML
	private TableColumn<StorageInfo, String> mapDate;

	private List<String> mapList;

	private ObservableList<StorageInfo> records = FXCollections.observableArrayList();

	private boolean reload;

	private User user;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 设置列的单元格值工厂
		mapName.setCellValueFactory(new PropertyValueFactory<>("name"));
		mapDate.setCellValueFactory(new PropertyValueFactory<>("date"));

		loadTableData();
	}

	private void loadTableData() {
		mapList = Backend.listSavedMaps();

		for (String map : mapList) {
			int separatorIndex = map.indexOf(":|");
			if (separatorIndex != -1) {
				String name = map.substring(0, separatorIndex);
				String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd")
						.format(new java.util.Date(Long.parseLong(map.substring(separatorIndex + 2))));
				records.add(new StorageInfo(name, timestamp));
			} else {
				System.err.println("无效的存档名格式: " + map);
			}
		}
		storagedList.setItems(records);
	}

	@FXML
	private void handleConfirm() {
		// 1. 获取选中项
		StorageInfo selectedInfo = storagedList.getSelectionModel().getSelectedItem();
		if (selectedInfo == null) {
			return;
		}

		// 2. 根据选中项查找对应的map文件名
		String selectedMapFile = mapList.stream()
				.filter(map -> map.startsWith(selectedInfo.getName() + ":|"))
				.findFirst()
				.orElse(null);

		if (selectedMapFile == null) {
			return;
		}

		// 3. 加载并启动游戏
		Stage stage = (Stage) confirmStorage.getScene().getWindow();
		stage.close();

		SokobanGame sokobanGame = new SokobanGame();
		Backend map = new Backend(selectedMapFile);
		sokobanGame.setMap(map);
		sokobanGame.setReload(reload);
		sokobanGame.setUser(user);
		sokobanGame.setName(selectedInfo.getName());
		sokobanGame.start(new Stage());
	}

	@FXML
	private void handleCancel() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/LevelSelectView.fxml"));
			Stage stage = (Stage) cancelButton.getScene().getWindow();
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
