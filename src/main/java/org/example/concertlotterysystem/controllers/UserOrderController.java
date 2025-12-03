package org.example.concertlotterysystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ConcertDisplayPageController {

    @FXML
    private Button createActivityButton;

    @FXML
    private Label placeholderLabel;

    @FXML
    public void initialize() {
        // 之後如果要在主畫面載入時顯示活動列表，可以在這裡實作
        if (placeholderLabel != null) {
            placeholderLabel.setText("(Event list / search results will be shown here later.)");
        }
    }

    @FXML
    private void onCreateActivityClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/concertlotterysystem/create-activity-view.fxml")
            );
            Stage stage = (Stage) createActivityButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (placeholderLabel != null) {
                placeholderLabel.setText("Failed to open Create Activity page.");
            }
        }
    }
}
