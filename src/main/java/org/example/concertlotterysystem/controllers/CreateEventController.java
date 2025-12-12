package org.example.concertlotterysystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;
import org.example.concertlotterysystem.repository.SqliteEventRepository;
import org.example.concertlotterysystem.services.EventService;
import org.example.concertlotterysystem.services.PageRouterService;

public class CreateEventController {


    @FXML
    private TextField titleField;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea descriptionArea;


    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField eventTimeField;     // HH:mm

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField drawTimeField;

    @FXML
    private TextField quotaField;


    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;
    private final EventService eventService;

    public CreateEventController() {

        this.eventService = new EventService(new SqliteEventRepository());
    }


    @FXML
    public void initialize() {

    }

    /**
     * 按下 Create 按鈕
     * Day 5：收集表單字串 → 丟給 EventService.createEvent(...) → 寫入 DB
     */
    @FXML
    private void onCreateClicked(ActionEvent eventAction) {
        try {
            String title = titleField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();

            String eventDateStr = (eventDatePicker.getValue() != null)
                    ? eventDatePicker.getValue().toString()
                    : null;

            String eventTimeStr = eventTimeField.getText();      // HH:mm
            String regStartStr = startTimeField.getText();       // yyyy-MM-dd HH:mm
            String regEndStr   = endTimeField.getText();         // yyyy-MM-dd HH:mm
            String drawTimeStr = drawTimeField.getText();        // yyyy-MM-dd HH:mm

            String quotaStr = quotaField.getText();




            Event created = eventService.createEvent(
                    title,
                    description,
                    location,
                    eventDateStr,
                    eventTimeStr,
                    regStartStr,
                    regEndStr,
                    drawTimeStr,
                    quotaStr,
                    "1"
            );

            showInfo("Event created",
                    "Event \"" + created.getTitle() + "\" has been saved to database.");
            clearForm();

        } catch (IllegalArgumentException ex) {
            showError("Validation Error", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error", "Failed to create event: " + ex.getMessage());
        }
    }

    /**
     * 按下 Cancel：目前版本先簡單清空表單
     * 之後若要返回上一畫面，可以在這裡加上場景切換邏輯
     */
    @FXML
    private void onCancelClicked(ActionEvent eventAction) {
        clearForm();
        PageRouterService.changeThePage("main-view.fxml",600,400);
    }

// --------- 工具方法區 ---------

    /** 清空表單欄位 */
    private void clearForm() {
        titleField.clear();
        locationField.clear();
        descriptionArea.clear();
        eventDatePicker.setValue(null);
        eventTimeField.clear();
        startTimeField.clear();
        endTimeField.clear();
        drawTimeField.clear();
        quotaField.clear();

    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}