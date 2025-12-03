package org.example.concertlotterysystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;
import org.example.concertlotterysystem.repository.SqliteEventRepository;
import org.example.concertlotterysystem.services.EventService;

public class CreateActivityController {
    // 基本欄位
    @FXML
    private TextField titleField;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea descriptionArea;

    // 活動時間（日期 + 時間）
    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField eventTimeField;     // HH:mm

    // 報名起訖、開獎時間（字串輸入，格式 yyyy-MM-dd HH:mm）
    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField drawTimeField;

    // 名額與每人上限
    @FXML
    private TextField quotaField;

    @FXML
    private TextField perMemberLimitField;

    // 狀態
    @FXML
    private ComboBox<EventStatus> statusComboBox;

    // 按鈕
    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    // Service：負責驗證與呼叫 Repository
    private final EventService eventService;

    public CreateActivityController() {
        // 簡單做法：這裡直接 new Repository + Service
        this.eventService = new EventService(new SqliteEventRepository());
    }

    // 初始化：把 EventStatus 塞進下拉選單
    @FXML
    public void initialize() {
        statusComboBox.getItems().setAll(EventStatus.values());
        // 預設選一個狀態（若沒有 DRAFT 就會選第一個 enum）
        if (statusComboBox.getItems().contains(EventStatus.DRAFT)) {
            statusComboBox.getSelectionModel().select(EventStatus.DRAFT);
        } else {
            statusComboBox.getSelectionModel().selectFirst();
        }
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

            // DatePicker -> yyyy-MM-dd 字串（若沒選日期則為 null）
            String eventDateStr = (eventDatePicker.getValue() != null)
                    ? eventDatePicker.getValue().toString()
                    : null;

            String eventTimeStr = eventTimeField.getText();      // HH:mm
            String regStartStr = startTimeField.getText();       // yyyy-MM-dd HH:mm
            String regEndStr   = endTimeField.getText();         // yyyy-MM-dd HH:mm
            String drawTimeStr = drawTimeField.getText();        // yyyy-MM-dd HH:mm

            String quotaStr = quotaField.getText();
            String perMemberLimitStr = perMemberLimitField.getText();

            EventStatus status = statusComboBox.getValue();

            // 呼叫 Service 做驗證＋轉型＋寫入 DB
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
                    perMemberLimitStr,
                    status
            );

            showInfo("Event created",
                    "Event \"" + created.getTitle() + "\" has been saved to database.");

            // 建立成功後清空表單
            clearForm();

        } catch (IllegalArgumentException ex) {
            // 驗證失敗（必填、格式、時間順序等）
            showError("Validation Error", ex.getMessage());
        } catch (Exception ex) {
            // DB 或其他非預期錯誤
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
        perMemberLimitField.clear();
        statusComboBox.getSelectionModel().clearSelection();
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
