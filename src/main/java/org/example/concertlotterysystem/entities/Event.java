package org.example.concertlotterysystem.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventId;
    private String title;
    private String description;
    private String location;
    private EventStatus status;
    private int quota;
    private int perMemberLimit;

    // time control
    private LocalDateTime eventTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime drawTime;

    // Sign Up
    private List<LotteryEntry> entries;

    /**
     * Full constructor：一次指定所有欄位
     */
    public Event(String eventId,
                 String title,
                 String description,
                 String location,
                 EventStatus status,
                 int quota,
                 int perMemberLimit,
                 LocalDateTime eventTime,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 LocalDateTime drawTime,
                 List<LotteryEntry> entries) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.status = status;
        this.quota = quota;
        this.perMemberLimit = perMemberLimit;
        this.eventTime = eventTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.drawTime = drawTime;
        // 避免外面傳 null
        this.entries = (entries != null) ? entries : new ArrayList<>();
    }

    /**
     * Simplified constructor：給現在快速 new 用
     * 其餘欄位先給預設值
     */
    public Event(String id, String title, int quota) {
        this(
                id,
                title,
                null,                 // description
                null,                 // location
                EventStatus.OPEN,     // 預設狀態
                quota,
                1,                    // 預設每人上限 1
                null,                 // eventTime
                null,                 // startTime
                null,                 // endTime
                null,                 // drawTime
                new ArrayList<>()     // 空的 entries
        );
    }

    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return status == EventStatus.OPEN &&
                now.isAfter(startTime) &&
                now.isBefore(endTime);
    }
    public String toString(){

        return "Event {" +
                "\n  eventId='" + eventId + '\'' +
                ", \n  title='" + title + '\'' +
                ", \n  description='" + description + '\'' +
                ", \n  location='" + location + '\'' +
                ", \n  status=" + status + // 假設 status 是 Enum 類型
                ", \n  quota=" + quota +
                ", \n  perMemberLimit=" + perMemberLimit +
                ", \n  eventTime=" + eventTime + // 假設是 LocalDateTime 或類似類型
                ", \n  startTime=" + startTime +
                ", \n  endTime=" + endTime +
                ", \n  drawTime=" + drawTime +
                ", \n  entriesSize=" + entries.size() + // 僅顯示列表大小，避免輸出大量數據
                // 如果需要顯示 entries 的完整內容，可以使用：
                // ", \n  entries=" + entries +
                "\n}";

    }
    // ===== Getter & Setter =====

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getPerMemberLimit() {
        return perMemberLimit;
    }

    public void setPerMemberLimit(int perMemberLimit) {
        this.perMemberLimit = perMemberLimit;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(LocalDateTime drawTime) {
        this.drawTime = drawTime;
    }

    public List<LotteryEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LotteryEntry> entries) {
        this.entries = entries;
    }
}
