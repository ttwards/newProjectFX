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
	@FXML private DatePicker startDate;
	@FXML private DatePicker endDate;
	@FXML private Button confirmStorage;
	@FXML private Button cancelButton;
	@FXML private TableView<StorageInfo> storagedList;
	@FXML private TableColumn<StorageInfo, String> mapName;
	@FXML private TableColumn<StorageInfo, String> mapDate;

	private List<String> mapList;

    private ObservableList<StorageInfo> records = FXCollections.observableArrayList();

	@Override
    public void initialize(URL location, ResourceBundle resources) {
		// 设置列的单元格值工厂
        mapName.setCellValueFactory(new PropertyValueFactory<>("name"));
        mapDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
		loadTableData();
	}

	private void loadTableData() {
		mapList = Backend.listSavedMaps();
		// 从服务器获取数据
		for(String map : mapList) {
			String parts[] = map.split("\\|");
			records.add(new StorageInfo(parts[0], parts[1]));
		}
		storagedList.setItems(records);
	}

	@FXML
	private void handleConfirm() {
		Stage stage = (Stage) confirmStorage.getScene().getWindow();
		stage.close();
		SokobanGame sokobanGame = new SokobanGame();

		int selectedMap = storagedList.getSelectionModel().getFocusedIndex();
		
		Backend map = new Backend(mapList.get(selectedMap));
		sokobanGame.setMap(map);
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
}
