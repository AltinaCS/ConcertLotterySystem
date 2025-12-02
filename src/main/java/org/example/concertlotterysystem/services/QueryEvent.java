package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.repository.EventDAO;

import java.util.List;

public class QueryEvent {

    private EventDAO eventDAO;

    public QueryEvent() {
        this.eventDAO = new EventDAO();
    }

    public List<Event> searchEvents(String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        String safeKeyword = keyword.trim();
        return eventDAO.searchEventsByKeyword(safeKeyword);
    }
}