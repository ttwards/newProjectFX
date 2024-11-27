package com.sokoban.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LevelSelector {
    @FXML private Button level1Button;
    @FXML private Button level2Button;
    @FXML private Button level3Button;
    @FXML private Button level4Button;
    @FXML private Button level5Button;
    @FXML private Button level6Button;

    @FXML
    public void levelSelect1(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level1Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(1);
    }

    @FXML
    public void levelSelect2(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level2Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(2);
    }

    @FXML
    public void levelSelect3(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level3Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(3);
    }

    @FXML
    public void levelSelect4(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level4Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(4);
    }

    @FXML
    public void levelSelect5(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level5Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(5);
    }

    @FXML
    public void levelSelect6(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/GameView.fxml"));
        Stage stage = (Stage) level6Button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setUserData(6);
    }
    
}
