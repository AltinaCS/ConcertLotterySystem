package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.repository.EventDAO;

import java.util.List;

public class QueryEventService {

    private EventDAO eventDAO;

    public QueryEventService() {
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