package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.EventStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteEventRepository implements EventRepository {

    // 要跟 DBInitializer 用同一個檔名
    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void create(Event event) throws SQLException {
        String sql = "INSERT INTO events (" +
                "event_id, title, description, location, status, quota, " +
                "per_member_limit, event_time, start_time, end_time, draw_time" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getEventId());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());          // 允許為 null
            ps.setString(4, event.getLocation());             // 允許為 null
            ps.setString(5, event.getStatus().name());        // enum 存 name()
            ps.setInt(6, event.getQuota());
            ps.setInt(7, event.getPerMemberLimit());

            setDateTime(ps, 8, event.getEventTime());
            setDateTime(ps, 9, event.getStartTime());
            setDateTime(ps, 10, event.getEndTime());
            setDateTime(ps, 11, event.getDrawTime());

            ps.executeUpdate();
        }
    }

    @Override
    public Event findById(String eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEvent(rs);
                }
            }
        }
        return null; // 找不到就回傳 null
    }

    @Override
    public List<Event> findAll() throws SQLException {
        String sql = "SELECT * FROM events ORDER BY event_time";

        List<Event> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToEvent(rs));
            }
        }
        return result;
    }

    @Override
    public List<Event> findOpenEvents(LocalDateTime now) throws SQLException {
        // 依目前 Event.isRegistrationOpen 的條件：status = OPEN 且 now 介於 start_time & end_time
        String sql = "SELECT * FROM events " +
                "WHERE status = ? " +
                "AND start_time IS NOT NULL " +
                "AND end_time IS NOT NULL " +
                "AND start_time <= ? " +
                "AND end_time >= ?";

        List<Event> result = new ArrayList<>();
        String nowStr = (now != null) ? now.toString() : LocalDateTime.now().toString();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, EventStatus.OPEN.name());
            ps.setString(2, nowStr);
            ps.setString(3, nowStr);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToEvent(rs));
                }
            }
        }
        return result;
    }
    @Override
    public void updateStatuses(List<Event> events) {
        String sql = "UPDATE events SET status = ? WHERE event_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // 開始事務

            for (Event event : events) {
                stmt.setString(1, event.getStatus().name());
                stmt.setString(2, event.getEventId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit(); // 提交事務

        } catch (SQLException e) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL);
                conn.rollback();
            } catch (SQLException rollbackE) {
                // 忽略回滾失敗
            }
            throw new RuntimeException("Failed to update event statuses in batch: " + e.getMessage(), e);
        }
    }
    // ====== Helper methods ======

    private void setDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.VARCHAR);  // TEXT 欄位
        } else {
            ps.setString(index, value.toString()); // 以 ISO-8601 字串存入
        }
    }

    private LocalDateTime getDateTime(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        return (value != null) ? LocalDateTime.parse(value) : null;
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        String eventId = rs.getString("event_id");
        String title = rs.getString("title");
        int quota = rs.getInt("quota");

        // 用簡化建構子先建，再補其他欄位（符合你現在的 Event.java）
        Event event = new Event(eventId, title, quota);

        event.setDescription(rs.getString("description"));
        event.setLocation(rs.getString("location"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            event.setStatus(EventStatus.valueOf(statusStr));
        }

        event.setPerMemberLimit(rs.getInt("per_member_limit"));
        event.setEventTime(getDateTime(rs, "event_time"));
        event.setStartTime(getDateTime(rs, "start_time"));
        event.setEndTime(getDateTime(rs, "end_time"));
        event.setDrawTime(getDateTime(rs, "draw_time"));

        return event;
    }
}
