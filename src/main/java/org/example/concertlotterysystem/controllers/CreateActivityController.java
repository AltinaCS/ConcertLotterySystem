package org.example.concertlotterysystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.UUID;

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
     * Day 4 版本：先把表單資料讀出來，組成 Event，印在 console
     * Day 5 再把這邊改成呼叫 EventService / Repository 寫入 DB
     */
    @FXML
    private void onCreateClicked(ActionEvent eventAction) {
        try {
            String title = titleField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();

            // 解析活動日期 + 時間
            LocalDateTime eventTime = parseEventDateTime(eventDatePicker, eventTimeField);

            // 解析報名起訖與開獎時間（允許留空 -> 變成 null）
            LocalDateTime regOpenTime = parseDateTimeField(startTimeField);
            LocalDateTime regCloseTime = parseDateTimeField(endTimeField);
            LocalDateTime drawTime = parseDateTimeField(drawTimeField);

            int quota = parseIntField(quotaField, "Total Quota");
            int perMemberLimit = parseIntField(perMemberLimitField, "Per-member Limit");

            EventStatus status = statusComboBox.getValue();
            if (status == null) {
                status = EventStatus.OPEN; // fallback
            }

            // 先用簡化版建構子建立 Event，再補其他欄位
            String generatedId = UUID.randomUUID().toString();
            Event newEvent = new Event(generatedId, title, quota);
            newEvent.setLocation(location);
            newEvent.setDescription(description);
            newEvent.setEventTime(eventTime);
            newEvent.setStartTime(regOpenTime);
            newEvent.setEndTime(regCloseTime);
            newEvent.setDrawTime(drawTime);
            newEvent.setPerMemberLimit(perMemberLimit);
            newEvent.setStatus(status);
            newEvent.setEntries(new ArrayList<>());

            // Day 4：先印 log，確認資料都有抓到
            System.out.println("[CreateActivityController] New Event drafted:");
            System.out.println("  " + newEvent);

            showInfo("Event drafted",
                    "Event \"" + title + "\" has been created in memory.\n" +
                            "Day 5 會再把它寫進資料庫。");

            // 建立完成後先清空表單
            clearForm();

        } catch (IllegalArgumentException ex) {
            // 目前 parse 失敗會丟 IllegalArgumentException
            showError("Invalid input", ex.getMessage());
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

    /** 解析「活動日期 + 時間」；若任一為空，就回傳 null */
    private LocalDateTime parseEventDateTime(DatePicker datePicker, TextField timeField) {
        LocalDate date = datePicker.getValue();
        String timeText = timeField.getText();

        if (date == null || timeText == null || timeText.isBlank()) {
            return null;
        }

        try {
            LocalTime time = LocalTime.parse(timeText.trim(),
                    DateTimeFormatter.ofPattern("HH:mm"));
            return date.atTime(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Event Time 格式錯誤，請使用 HH:mm，例如 19:30");
        }
    }

    /** 解析 yyyy-MM-dd HH:mm；空字串時回傳 null */
    private LocalDateTime parseDateTimeField(TextField field) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "時間欄位格式錯誤（" + text + "），請使用 yyyy-MM-dd HH:mm，例如 2025-12-10 10:00");
        }
    }

    /** 解析整數欄位；若失敗丟 IllegalArgumentException */
    private int parseIntField(TextField field, String fieldName) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(fieldName + " 不可為空");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " 必須是整數");
        }
    }

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
