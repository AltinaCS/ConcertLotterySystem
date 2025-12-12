package org.example.concertlotterysystem.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.example.concertlotterysystem.Exceptions.CancelEventLotteryException;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;
import org.example.concertlotterysystem.entities.Member;
import org.example.concertlotterysystem.entities.MemberQualificationStatus;
import org.example.concertlotterysystem.repository.EventDAO;
import org.example.concertlotterysystem.repository.SqliteEventRepository;
import org.example.concertlotterysystem.services.EventRegistrationService;
import org.example.concertlotterysystem.services.EventService;
import org.example.concertlotterysystem.services.PageRouterService;
import org.example.concertlotterysystem.entities.LotteryDrawer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import org.example.concertlotterysystem.services.SessionManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventDetailController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label locationLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label regPeriodLabel;
    @FXML private Label drawTimeLabel;
    @FXML private Label quotaLabel;
    @FXML private Label limitLabel;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;
    @FXML private Button lotteryButton;
    private EventService eventService;
    private final ObjectProperty<Event> currentEvent = new SimpleObjectProperty<>();
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeEventService();
        eventService.syncEventStatuses();
        currentEvent.addListener((obs, oldEvent, newEvent) -> {
            if (newEvent != null) {
                updateUIWithEventData(newEvent);
                handleButtonStatus(newEvent);
            }
        });

    }

    private void initializeEventService() {
        this.eventService= new EventService(new SqliteEventRepository());
    }

    private void handleButtonStatus(Event newEvent) {
        registerButton.setDisable(!newEvent.getStatus().equals(EventStatus.OPEN));
        lotteryButton.setVisible(SessionManager.getInstance().getCurrentMember().getQualification().equals(MemberQualificationStatus.ADMIN));
        lotteryButton.setDisable(!newEvent.getStatus().equals(EventStatus.OPEN) && !newEvent.getStatus().equals(EventStatus.CLOSED));
    }

    private void updateUIWithEventData(Event event) {
        if (event != null) {
            String displayedStatus = "";
            switch (event.getStatus()){
                case DRAFT->displayedStatus= "活動尚未開放報名";
                case OPEN-> displayedStatus = "活動開放報名";
                case CLOSED->displayedStatus= "活動結束已報名，等待管理員抽籤";
                case DRAWN->displayedStatus= "管理員已抽籤，請查看抽票結果";
                case CANCELLED->displayedStatus= "活動結束已結束，或已被取消";
                default-> displayedStatus = "N/A";
            }
            titleLabel.setText(event.getTitle());
            locationLabel.setText(event.getLocation());
            descriptionLabel.setText(event.getDescription());
            statusLabel.setText(displayedStatus);
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

    public void initializeData(Event event) {
        this.currentEvent.set(event);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        Event current = currentEvent.get();
        String memberId = SessionManager.getInstance().getCurrentMember().getMemberId();

        if (current == null) return;
        if (memberId == null) {
            showAlert(Alert.AlertType.ERROR, "錯誤", "請先登入才能登記活動。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動登記");
        alert.setHeaderText(null);

        try {
            EventRegistrationService.registerForEvent(memberId, current.getEventId());

            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("您已成功登記 [" + current.getTitle() + "]。");
            alert.showAndWait();
        }
        catch (CancelEventLotteryException e) {

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("取消登記確認");
            confirmationAlert.setHeaderText("您已登記過此活動");
            confirmationAlert.setContentText("請問您是要取消這次活動的登記嗎？");

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                try {

                    EventRegistrationService.cancelRegistration(memberId, current.getEventId());
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    alert.setContentText("已成功取消 [" + current.getTitle() + "] 的登記。");
                } catch (Exception cancelEx) {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("取消登記失敗：" + cancelEx.getMessage());
                }
            } else {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("您選擇保留現有的登記狀態。");
            }
            alert.showAndWait();

        }
        catch (Exception e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("登記失敗：" + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        updateUIWithEventData(currentEvent.get());
    }
    @FXML
    private void handleLottery(){
        Event current = currentEvent.get();

        if (current == null) {
            showAlert(Alert.AlertType.WARNING, "操作錯誤", "無法識別當前活動。");
            return;
        }
        if (current.getStatus().equals(EventStatus.DRAWN) || current.getStatus().equals(EventStatus.CANCELLED)) {
            showAlert(Alert.AlertType.WARNING, "操作錯誤", "該活動已抽籤過或已經結束。");
            return;
        }

        Member member = SessionManager.getInstance().getCurrentMember();
        if (member == null || !"ADMIN".equals(member.getQualification().toString())) {
            showAlert(Alert.AlertType.ERROR, "權限不足", "只有管理員才能對此活動執行抽籤操作。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("活動抽籤：" + current.getTitle());
        alert.setHeaderText(null);

        try {
            LotteryDrawer drawer = new LotteryDrawer(current);

            drawer.runLottery();
            EventDAO eventDAO = new EventDAO();
            eventDAO.updateStatus(current, EventStatus.DRAWN);
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("抽籤已完成。結果已儲存。");

        } catch (RuntimeException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("抽籤失敗：資料庫操作或運行時錯誤。請檢查日誌。\n原因：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("抽籤失敗：發生未知錯誤。\n原因：" + e.getMessage());
        }
        eventService.syncEventStatuses();
        updateUIWithEventData(currentEvent.get());
        handleButtonStatus(currentEvent.get());
        alert.showAndWait();
    }
    @FXML
    private void handleBack(ActionEvent event) {
        PageRouterService.changeThePage("main-view.fxml", 600, 400);
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
