package org.example.concertlotterysystem.controllers;

import javafx.scene.control.Button;
import org.example.concertlotterysystem.entities.LotteryEntry;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.LotteryEntryStatus;
import org.example.concertlotterysystem.repository.LotteryEntryDAO;
import org.example.concertlotterysystem.repository.EventDAO;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import org.example.concertlotterysystem.services.PageRouterService;
import org.example.concertlotterysystem.services.SessionManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserOrderController implements Initializable {

    @FXML private VBox entryContainer;
    @FXML private Button backButton;

    private final LotteryEntryDAO entryDAO = new LotteryEntryDAO();
    private final EventDAO eventDAO = new EventDAO();


    private String currentMemberId;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private static class EntryView {
        public String eventTitle;
        public String eventLocation;
        public java.time.LocalDateTime eventTime;
        public LotteryEntryStatus entryStatus;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMemberEntries();
    }

    /**
     * 從 DAO 協調資料並渲染到 UI。
     */
    private void loadMemberEntries() {
        entryContainer.getChildren().clear();
        currentMemberId = SessionManager.getInstance().getCurrentMember().getMemberId();
        if (currentMemberId == null || currentMemberId.isBlank()) {
            entryContainer.getChildren().add(new Label("請先登入，以查看您的登記記錄。"));
            return;
        }

        try {

            List<LotteryEntry> rawEntries = entryDAO.findByMemberId(currentMemberId);
            List<EntryView> viewData = assembleViewData(rawEntries);

            if (viewData.isEmpty()) {
                entryContainer.getChildren().add(new Label("您尚未登記任何活動。"));
                return;
            }

            entryContainer.getChildren().add(createHeaderRow());
            for (EntryView entry : viewData) {
                entryContainer.getChildren().add(createEntryCard(entry));
            }

        } catch (Exception e) {
            entryContainer.getChildren().add(new Label("載入訂單失敗：服務發生錯誤。"));
            e.printStackTrace();
        }
    }

    /**
     * 協調方法：將 LotteryEntry 與對應的 Event 資訊結合。
     * 這是 Controller 層次進行資料組裝的邏輯。
     */
    private List<EntryView> assembleViewData(List<LotteryEntry> rawEntries) {
        List<EntryView> viewData = new ArrayList<>();

        for (LotteryEntry entry : rawEntries) {
            Event event = eventDAO.getEventById(entry.getEventId());

            EntryView view = new EntryView();
            view.entryStatus = entry.getStatus();

            if (event != null) {
                view.eventTitle = event.getTitle();
                view.eventLocation = event.getLocation();
                view.eventTime = event.getEventTime();
            } else {
                view.eventTitle = "活動已移除或不存在";
                view.eventLocation = "N/A";
                view.eventTime = null;
            }

            viewData.add(view);
        }
        return viewData;
    }




    private HBox createHeaderRow() {

        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 10, 10, 10));
        header.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #333333;");

        Label title = new Label("活動名稱"); title.setPrefWidth(250); title.setFont(Font.font("System", 14));
        Label location = new Label("地點"); location.setPrefWidth(150); location.setFont(Font.font("System", 14));
        Label time = new Label("活動時間"); time.setPrefWidth(180); time.setFont(Font.font("System", 14));
        Label status = new Label("抽籤結果"); status.setPrefWidth(150); status.setFont(Font.font("System", 14));

        header.getChildren().addAll(title, location, time, status);
        return header;
    }

    private HBox createEntryCard(EntryView entry) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10, 10, 10, 10));
        card.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");

        String eventTimeStr = (entry.eventTime != null) ? entry.eventTime.format(DISPLAY_FORMATTER) : "N/A";

        Label title = new Label(entry.eventTitle); title.setPrefWidth(250);
        Label location = new Label(entry.eventLocation); location.setPrefWidth(150);
        Label time = new Label(eventTimeStr); time.setPrefWidth(180);
        Label status = new Label(getStatusDisplay(entry.entryStatus)); status.setPrefWidth(150);

        if (entry.entryStatus == LotteryEntryStatus.WON) {
            status.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else if (entry.entryStatus == LotteryEntryStatus.LOST) {
            status.setStyle("-fx-text-fill: red;");
        }

        card.getChildren().addAll(title, location, time, status);
        return card;
    }

    private String getStatusDisplay(LotteryEntryStatus status) {
        switch (status) {
            case WON: return "✅ 已中籤";
            case LOST: return "❌ 未中籤";
            case PENDING: return "⏳ 待抽籤";
            case CANCELLED: return "\uD83D\uDDD1 已取消";
            default: return "未知狀態";
        }
    }
    @FXML
    private void handleBack() {
        PageRouterService.changeThePageWithController("main-view.fxml", 600, 400);
    }
}
