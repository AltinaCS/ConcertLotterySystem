package org.example.concertlotterysystem.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventId;
    private String title;
    private EventStatus status;
    private int quota;

    //time control
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    //Sign Up
    private List<LotteryEntry> entries;

    public Event(String id, String title, int quota){
        this.eventId = id;
        this.title = title;
        this.quota = quota;
        this.entries = new ArrayList<>();
        this.status = EventStatus.OPEN;
    }

    public boolean isRegistrationOpen(){
        LocalDateTime now = LocalDateTime.now();
        return status == EventStatus.OPEN &&
                now.isAfter(startTime) &&
                now.isBefore(endTime);
    }

    //Getter&Setter
    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

}
