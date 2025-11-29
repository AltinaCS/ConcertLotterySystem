package org.example.concertlotterysystem.entities;

public class LotteryEntry {
    private String entryId;
    private String eventId;
    private String memberId;
    private String result;

    public LotteryEntry(String entryId, String eventId, String memberId) {
        this.entryId = entryId;
        this.eventId = eventId;
        this.memberId = memberId;
        this.result = "PENDING";
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
    public String getResult(){
        return result;
    }
}
