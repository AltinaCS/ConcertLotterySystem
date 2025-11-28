package org.example.concertlotterysystem.entities;

public class LotteryEntry {
    private String entryId;
    private String eventId;  // 對應 UML 的關聯
    private String memberId; // 對應 UML 的關聯
    private String result;   // 對應 UML 的 result enum

    public LotteryEntry(String entryId, String eventId, String memberId) {
        this.entryId = entryId;
        this.eventId = eventId;
        this.memberId = memberId;
        this.result = "PENDING"; // 預設值
    }

    // Getters
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
