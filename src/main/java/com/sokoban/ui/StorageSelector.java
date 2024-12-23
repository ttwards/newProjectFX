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

		// 添加日期选择器监听
		startDate.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (user != null)
				records.clear();
				mapList.clear();
			loadTableData();
		});

		endDate.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (user != null)
			records.clear();
				mapList.clear();
				loadTableData();
		});
	}

	private void loadTableData() {
		mapList = Backend.listSavedMaps(user.getUsername());

		// 获取开始和结束日期的毫秒时间戳
		long startTime = startDate.getValue() != null
				? startDate.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
				: 0;
		long endTime = endDate.getValue() != null ? endDate.getValue().plusDays(1)
				.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : Long.MAX_VALUE;

		for (String map : mapList) {
			int separatorIndex = map.indexOf(":|");
			if (separatorIndex != -1) {
				String name = map.substring(0, separatorIndex);
				long timestamp = Long.parseLong(map.substring(separatorIndex + 2));

				// 检查日期是否在范围内
				if (timestamp >= startTime && timestamp <= endTime) {
					String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd")
							.format(new java.util.Date(timestamp));
					records.add(new StorageInfo(name, dateStr));
				}
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
		Backend map = new Backend(selectedMapFile, user.getUsername());
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
		loadTableData();
	}
}
