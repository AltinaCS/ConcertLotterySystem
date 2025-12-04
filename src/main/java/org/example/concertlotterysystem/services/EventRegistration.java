package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.LotteryEntry;
import org.example.concertlotterysystem.repository.EventDAO;
import org.example.concertlotterysystem.repository.LotteryEntryDAO;
import java.util.UUID;

public class EventRegistration {

    public void registerForEvent(String memberId, String eventId){
        EventDAO eventDAO = new EventDAO();
        Event event = eventDAO.getEventById(eventId);

        if (event == null) {
            System.out.println("找不到該活動。");
            return;
        }

        if (!event.isRegistrationOpen()){
            System.out.println("這個活動尚未開放報名。");
            return;
        }

        // 先使用UUID幫Entry生成ID
        String newEntryId = UUID.randomUUID().toString();
        LotteryEntry lotteryEntry = new LotteryEntry(newEntryId, eventId, memberId);

        LotteryEntryDAO lotteryEntryDAO = new LotteryEntryDAO();
        lotteryEntryDAO.save(lotteryEntry);
    }
}