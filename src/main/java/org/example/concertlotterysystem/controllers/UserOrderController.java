package org.example.concertlotterysystem.controllers;

import javafx.scene.control.Button;
import org.example.concertlotterysystem.entities.LotteryEntry;
import org.example.concertlotterysystem.entities.Event; // 假設 Event 實體存在
import org.example.concertlotterysystem.entities.LotteryEntryStatus;
import org.example.concertlotterysystem.repository.LotteryEntryDAO;
import org.example.concertlotterysystem.repository.EventDAO; // 假設 EventDAO 存在

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
    // 🚨 協調類別: Controller 直接使用多個 DAO
    private final LotteryEntryDAO entryDAO = new LotteryEntryDAO();
    private final EventDAO eventDAO = new EventDAO(); // 假設 EventDAO 存在

    // ⚠️ 替換為實際獲取登入會員 ID 的方法或變數
    private String currentMemberId;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    // 專門為 UI 協調資料而定義的 View Model (在 Controller 內部定義)
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
            // 1. 從 LotteryEntryDAO 獲取該會員的所有登記記錄
            List<LotteryEntry> rawEntries = entryDAO.findByMemberId(currentMemberId);
            List<EntryView> viewData = assembleViewData(rawEntries); // 協調資料

            if (viewData.isEmpty()) {
                entryContainer.getChildren().add(new Label("您尚未登記任何活動。"));
                return;
            }

            // 渲染 UI
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
            // 2. 查詢對應的 Event 資訊 (假設 EventDAO 有 findById 方法)
            Event event = eventDAO.getEventById(entry.getEventId());

            EntryView view = new EntryView();
            view.entryStatus = entry.getStatus();

            if (event != null) {
                // 假設 Event 實體有 getTitle(), getLocation(), getEventTime()
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


    // --- UI 渲染方法 (保持不變) ---

    private HBox createHeaderRow() {
        // ... (同前，不變) ...
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
        // ... (同前，使用 EntryView 數據) ...
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
