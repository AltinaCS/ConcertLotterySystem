package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;
import org.example.concertlotterysystem.repository.EventRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**

 負責：

 驗證表單輸入

 把字串轉成正確型別 (int / LocalDateTime)

 new Event 並呼叫 EventRepository 寫進 SQLite
 */
public class EventService {

    private final EventRepository eventRepository;

    // yyyy-MM-dd HH:mm，對應你 TextField 的註解格式
    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // DatePicker 的日期字串 (或你也可以直接在 Controller 傳 LocalDate 進來)
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 事件時間欄位 (HH:mm)
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**

     從表單字串建立 Event，並寫入資料庫。

     @throws IllegalArgumentException 驗證失敗（欄位缺失 / 格式錯誤 / 時間順序錯誤）時丟出

     @throws Exception DB 相關錯誤會往上丟（SQLException 的上層）
     */
    public Event createEvent(
            String title,
            String description,
            String location,
            String eventDateStr, // 來自 DatePicker 的日期字串 (yyyy-MM-dd)
            String eventTimeStr, // HH:mm
            String regStartStr, // yyyy-MM-dd HH:mm
            String regEndStr, // yyyy-MM-dd HH:mm
            String drawTimeStr, // yyyy-MM-dd HH:mm
            String quotaStr,
            String perMemberLimitStr
    ) throws Exception {

// ===== 1. 必填欄位檢查 =====
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (quotaStr == null || quotaStr.isBlank()) {
            throw new IllegalArgumentException("Quota is required.");
        }
        if (perMemberLimitStr == null || perMemberLimitStr.isBlank()) {
            throw new IllegalArgumentException("Per-member limit is required.");
        }
        if (regStartStr == null || regStartStr.isBlank()
                || regEndStr == null || regEndStr.isBlank()) {
            throw new IllegalArgumentException("Registration start and end time are required.");
        }
        if (drawTimeStr == null || drawTimeStr.isBlank()) {
            throw new IllegalArgumentException("Draw time is required.");
        }

// ===== 2. 數字轉型與範圍檢查 =====
        int quota;
        int perMemberLimit;
        try {
            quota = Integer.parseInt(quotaStr.trim());
            perMemberLimit = Integer.parseInt(perMemberLimitStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quota and per-member limit must be valid integers.");
        }

        if (quota <= 0) {
            throw new IllegalArgumentException("Quota must be greater than 0.");
        }
        if (perMemberLimit <= 0) {
            throw new IllegalArgumentException("Per-member limit must be greater than 0.");
        }
        if (perMemberLimit > quota) {
            throw new IllegalArgumentException("Per-member limit cannot exceed total quota.");
        }
        description = (description != null && !description.isBlank()) ? description.trim() : null;
        location = (location != null && !location.isBlank()) ? location.trim() : null;
// ===== 3. 時間字串轉 LocalDateTime =====
        LocalDateTime regStart = parseDateTime(regStartStr);
        LocalDateTime regEnd = parseDateTime(regEndStr);
        LocalDateTime drawTime = parseDateTime(drawTimeStr);

        if (!regEnd.isAfter(regStart)) {
            throw new IllegalArgumentException("Registration end must be after registration start.");
        }
        if (!drawTime.isAfter(regEnd)) {
            throw new IllegalArgumentException("Draw time must be after registration end.");
        }

// eventTime = eventDate + eventTimeStr（若兩者都有填）
        LocalDateTime eventTime = null;
        if (eventDateStr != null && !eventDateStr.isBlank()
                && eventTimeStr != null && !eventTimeStr.isBlank()) {
            LocalDate date = LocalDate.parse(eventDateStr.trim(), dateFormatter);
            LocalTime time = LocalTime.parse(eventTimeStr.trim(), timeFormatter);
            eventTime = LocalDateTime.of(date, time);
        }
        if (eventTime != null) {
            if (!eventTime.isAfter(LocalDateTime.now()) || !regEnd.isAfter(LocalDateTime.now()) || !regStart.isAfter(LocalDateTime.now())){
                throw new IllegalArgumentException("你無法回到過去");
            }
            // 檢查 eventTime 是否晚於 regStart
            if (!eventTime.isAfter(regStart)) {
                throw new IllegalArgumentException("Event time must be after registration start time.");
            }

            // 檢查 eventTime 是否晚於 regEnd
            if (!eventTime.isAfter(regEnd)) {
                throw new IllegalArgumentException("Event time must be after registration end time.");
            }

            // 檢查 eventTime 是否晚於 drawTime (這是最重要的檢查)
            if (!eventTime.isAfter(drawTime)) {
                throw new IllegalArgumentException("Event time must be strictly after the draw time.");
            }
        }

// TODO:這塊要做修改 改成利用時間去判斷狀態而自行設定
        EventStatus status;
        LocalDateTime now = LocalDateTime.now(); // 獲取當前時間

        if (now.isBefore(regStart)) {
            // 報名開始時間尚未到
            status = EventStatus.DRAFT;

        } else if (now.isAfter(regEnd)) {
            // 報名已經截止 (活動已經過期或即將進行抽籤)
            // 由於這是創建新活動，如果當前時間已經過了截止時間，通常設定為 CLOSED 或 PENDING_DRAW
            // 這裡選擇 CLOSED 除非您有另一個 PENDING_DRAW 狀態
            status = EventStatus.CLOSED;

        } else {
            // 當前時間在 regStart 和 regEnd 之間
            status = EventStatus.OPEN;
        }

// ===== 4. 產生 eventId（暫時用時間戳，可改用 UUID） =====
        String eventId = generateEventId();

// ===== 5. new Event 物件 =====
        Event event = new Event(
                eventId,
                title,
                description,
                location,
                status,
                quota,
                perMemberLimit,
                eventTime,
                regStart,
                regEnd,
                drawTime,
                null // entries 先不處理，給 null 讓 Event 內部自己 new ArrayList
        );

// ===== 6. 寫進資料庫 =====
        eventRepository.create(event);

        return event;
    }
    public void syncEventStatuses() {

        List<Event> allEvents;

        try {
            // 🚨 修正：呼叫 repository 的 findAll()，並處理 SQLException
            allEvents = eventRepository.findAll();
        } catch (SQLException e) {
            // 將底層的 SQLException 封裝為 RuntimeException，以便上層 Controller 捕捉和處理
            throw new RuntimeException("Failed to load events for status synchronization.", e);
        }

        // 如果沒有活動，則直接返回
        if (allEvents.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Event> eventsToUpdate = new ArrayList<>();

        for (Event event : allEvents) {
            // 只有非 DRAWN 的活動才需要時間同步
            if (event.getStatus() == EventStatus.DRAWN) {
                continue;
            }

            // 避免 NullPointerException
            if (event.getStartTime() == null || event.getEndTime() == null || event.getDrawTime() == null) {
                continue;
            }

            EventStatus currentStatus = event.getStatus();
            EventStatus newStatus = determineStatusByTime(now, event);

            // 如果計算出來的新狀態與當前狀態不同，則需要更新
            if (currentStatus != newStatus) {
                event.setStatus(newStatus); // 更新記憶體物件
                eventsToUpdate.add(event);  // 加入待更新列表
            }
        }

        // 批量更新資料庫 (假設 updateStatuses 不拋出 SQLException，而是拋出 RuntimeException)
        if (!eventsToUpdate.isEmpty()) {
            // 🚨 假設 eventRepository.updateStatuses() 已經實作並處理了 DB 錯誤
            eventRepository.updateStatuses(eventsToUpdate);
        }
    }
    private EventStatus determineStatusByTime(LocalDateTime now, Event event) {

        // 檢查順序：UPCOMING -> OPEN -> CLOSED
        if (now.isBefore(event.getStartTime())) {
            return EventStatus.DRAFT;

        } else if (now.isBefore(event.getEndTime())) {
            return EventStatus.OPEN;

        } else if (now.isBefore(event.getDrawTime())) {
            return EventStatus.CLOSED;

        } else {
            // 報名截止時間和抽籤時間都已過，但尚未 DRAWN，系統維持 CLOSED
            return EventStatus.CLOSED;
        }
    }
    // 將 "yyyy-MM-dd HH:mm" 轉成 LocalDateTime
    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value.trim(), dateTimeFormatter);
    }

    // 產生簡單的 eventId，你之後可以改成 UUID.randomUUID().toString()
    private String generateEventId() {
        return "EVT-" + System.currentTimeMillis();
    }
}