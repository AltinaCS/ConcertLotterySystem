package org.example.concertlotterysystem.entities;

public class LotteryEntry {
    private String entryId;
    private String eventId;
    private String memberId;
    private LotteryEntryStatus status;

    public LotteryEntry(String entryId, String eventId, String memberId, LotteryEntryStatus status){
        this.entryId = entryId;
        this.eventId = eventId;
        this.memberId = memberId;
        this.status = status;
    }

    public String getEntryId(){
        return entryId;
    }
    public String getEventId(){
        return eventId;
    }
    public String getMemberId(){
        return memberId;
    }
    public LotteryEntryStatus getStatus(){
        return status;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    public void setStatus(LotteryEntryStatus status){
        this.status = status;
    }
}
