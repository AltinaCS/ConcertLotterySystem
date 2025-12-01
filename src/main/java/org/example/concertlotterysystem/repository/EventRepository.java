package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.Event;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository {

    /**
     * Insert a new event into the database.
     */
    void create(Event event) throws SQLException;

    /**
     * Find an event by its ID.
     *
     * @param eventId the event ID
     * @return Event if found, otherwise null
     */
    Event findById(String eventId) throws SQLException;

    /**
     * Get all events.
     */
    List<Event> findAll() throws SQLException;

    /**
     * Find events that are currently open for registration at the given time.
     */
    List<Event> findOpenEvents(LocalDateTime now) throws SQLException;
}
