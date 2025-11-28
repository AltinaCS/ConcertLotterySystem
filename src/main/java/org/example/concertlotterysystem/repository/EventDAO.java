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
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
}