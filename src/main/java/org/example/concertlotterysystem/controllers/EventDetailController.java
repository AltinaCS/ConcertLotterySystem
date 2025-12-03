package org.example.concertlotterysystem.controllers;

import javafx.scene.control.Alert;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.services.PageRouterService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.time.format.DateTimeFormatter;

public class EventDetailController {

    @FXML private Label titleLabel;
    @FXML private Label locationLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label regPeriodLabel;
    @FXML private Label drawTimeLabel;
    @FXML private Label quotaLabel;
    @FXML private Label limitLabel;
    @FXML private Label statusLabel;

    private Event currentEvent;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public void initializeData(Event event) {
        this.currentEvent = event;

        if (event != null) {
            titleLabel.setText(event.getTitle());
            locationLabel.setText(event.getLocation());
            descriptionLabel.setText(event.getDescription());
            statusLabel.setText(event.getStatus().name());
            quotaLabel.setText(String.valueOf(event.getQuota()));
            limitLabel.setText(String.valueOf(event.getPerMemberLimit()));

            String eventTime = (event.getEventTime() != null) ? event.getEventTime().format(DISPLAY_FORMATTER) : "N/A";
            eventTimeLabel.setText(eventTime);

            String startTime = (event.getStartTime() != null) ? event.getStartTime().format(DISPLAY_FORMATTER) : "N/A";
            String endTime = (event.getEndTime() != null) ? event.getEndTime().format(DISPLAY_FORMATTER) : "N/A";
            regPeriodLabel.setText(startTime + " - " + endTime);

            String drawTime = (event.getDrawTime() != null) ? event.getDrawTime().format(DISPLAY_FORMATTER) : "N/A";
            drawTimeLabel.setText(drawTime);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // TODO: 在這裡實作檢查資格和跳轉到登記流程的邏輯
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動登記");
        alert.setHeaderText(null);
        alert.setContentText("您已嘗試為活動 [" + currentEvent.getTitle() + "] 進行登記。");
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        PageRouterService.changeThePage("main-view.fxml", 600, 400);
    }
}
