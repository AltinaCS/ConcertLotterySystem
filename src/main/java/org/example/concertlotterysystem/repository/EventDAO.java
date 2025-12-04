package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private static final String DB_URL = "jdbc:sqlite:lottery.db";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Event> searchEventsByKeyword(String keyword) {
        List<Event> resultList = new ArrayList<>();

        String sql = "SELECT * FROM events WHERE title LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // 1. 轉換資料
                Event event = new Event(
                        rs.getString("event_id"),
                        rs.getString("title"),
                        rs.getInt("quota")
                );

                // 2. 設定狀態與時間
                event.setStatus(EventStatus.valueOf(rs.getString("status")));
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");

                if (start != null) event.setStartTime(LocalDateTime.parse(start, FORMATTER));
                if (end != null) event.setEndTime(LocalDateTime.parse(end, FORMATTER));

                resultList.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public Event getEventById(String eventId){
        Event event = null;
        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // initialize data with constructor
                event = new Event(
                        rs.getString("event_id"),
                        rs.getString("title"),
                        rs.getInt("quota")
                );

                event.setDescription(rs.getString("description"));
                event.setLocation(rs.getString("location"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));
                event.setPerMemberLimit(rs.getInt("per_member_limit"));

                String eventTime = rs.getString("event_time");
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");
                String draw = rs.getString("draw_time");

                if (eventTime != null) event.setEventTime(LocalDateTime.parse(eventTime, FORMATTER));
                if (start != null) event.setStartTime(LocalDateTime.parse(start, FORMATTER));
                if (end != null) event.setEndTime(LocalDateTime.parse(end, FORMATTER));
                if (draw != null) event.setDrawTime(LocalDateTime.parse(draw, FORMATTER));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event;
    }

}