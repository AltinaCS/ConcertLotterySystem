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

public class EventService {

    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
            String eventDateStr,
            String eventTimeStr, // HH:mm
            String regStartStr, // yyyy-MM-dd HH:mm
            String regEndStr, // yyyy-MM-dd HH:mm
            String drawTimeStr, // yyyy-MM-dd HH:mm
            String quotaStr,
            String perMemberLimitStr
    ) throws Exception {


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
        System.out.println(description);

        LocalDateTime regStart = parseDateTime(regStartStr);
        LocalDateTime regEnd = parseDateTime(regEndStr);
        LocalDateTime drawTime = parseDateTime(drawTimeStr);

        if (!regEnd.isAfter(regStart)) {
            throw new IllegalArgumentException("Registration end must be after registration start.");
        }
        if (!drawTime.isAfter(regEnd)) {
            throw new IllegalArgumentException("Draw time must be after registration end.");
        }


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
            if (!eventTime.isAfter(regStart)) {
                throw new IllegalArgumentException("Event time must be after registration start time.");
            }

            if (!eventTime.isAfter(regEnd)) {
                throw new IllegalArgumentException("Event time must be after registration end time.");
            }

            if (!eventTime.isAfter(drawTime)) {
                throw new IllegalArgumentException("Event time must be strictly after the draw time.");
            }
        }


        EventStatus status;
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(regStart)) {
            status = EventStatus.DRAFT;

        } else if (now.isAfter(regEnd)) {
            status = EventStatus.CLOSED;

        } else {
            status = EventStatus.OPEN;
        }


        String eventId = generateEventId();
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
                null
        );
        eventRepository.create(event);

        return event;
    }
    public void syncEventStatuses() {

        List<Event> allEvents;

        try {
            allEvents = eventRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events for status synchronization.", e);
        }

        if (allEvents.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Event> eventsToUpdate = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getStatus() == EventStatus.DRAWN) {
                continue;
            }

            if (event.getStartTime() == null || event.getEndTime() == null || event.getDrawTime() == null) {
                continue;
            }

            EventStatus currentStatus = event.getStatus();
            EventStatus newStatus = determineStatusByTime(now, event);
            if (currentStatus != newStatus) {
                event.setStatus(newStatus);
                eventsToUpdate.add(event);
            }
        }

        if (!eventsToUpdate.isEmpty()) {
            eventRepository.updateStatuses(eventsToUpdate);
        }
    }
    private EventStatus determineStatusByTime(LocalDateTime now, Event event) {
        if (now.isBefore(event.getStartTime())) {
            return EventStatus.DRAFT;

        } else if (now.isBefore(event.getEndTime())) {
            return EventStatus.OPEN;

        } else if (now.isBefore(event.getDrawTime())) {
            return EventStatus.CLOSED;

        } else {
            return EventStatus.CLOSED;
        }
    }
    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value.trim(), dateTimeFormatter);
    }
    private String generateEventId() {
        return "EVT-" + System.currentTimeMillis();
    }
}