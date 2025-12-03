package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;
import org.example.concertlotterysystem.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
            String perMemberLimitStr,
            EventStatus status
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

// 若沒選狀態，給預設值（依你們 business rule 調整）
        if (status == null) {
            status = EventStatus.OPEN; // 或 EventStatus.DRAFT
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

    // 將 "yyyy-MM-dd HH:mm" 轉成 LocalDateTime
    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value.trim(), dateTimeFormatter);
    }

    // 產生簡單的 eventId，你之後可以改成 UUID.randomUUID().toString()
    private String generateEventId() {
        return "EVT-" + System.currentTimeMillis();
    }
}