package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.LotteryEntry;
import org.example.concertlotterysystem.entities.LotteryEntryStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LotteryEntryDAO {

    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    public void save(LotteryEntry entry) {
        String sql = "INSERT INTO lottery_entries (entry_id, event_id, member_id,result,timestamp) VALUES (?, ?, ?, ? ,?)";
        LocalDateTime now = LocalDateTime.now();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }
            stmt.setString(1, entry.getEntryId());
            stmt.setString(2, entry.getEventId());
            stmt.setString(3, entry.getMemberId());
            stmt.setString(4, entry.getStatus().toString());
            stmt.setString(5, now.toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save lottery entry", e);
        }
    }
    public LotteryEntry findByMemberAndEvent(String memberId, String eventId) {

        String sql = "SELECT entry_id, member_id, event_id, result FROM lottery_entries WHERE member_id = ? AND event_id = ? AND result = 'PENDING'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    String entryId = rs.getString("entry_id");
                    String resultStr = rs.getString("result");


                    if (resultStr == null || resultStr.isEmpty()) {
                        resultStr = LotteryEntryStatus.PENDING.name();
                    }

                    LotteryEntryStatus status = LotteryEntryStatus.valueOf(resultStr);


                    return new LotteryEntry(entryId, eventId, memberId, status);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing entry: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            System.err.println("資料庫裡的狀態字串跟 Enum 對不上: " + e.getMessage());
        }
        return null;
    }
    public List<LotteryEntry> findByMemberId(String memberId) {
        List<LotteryEntry> list = new ArrayList<>();
        String sql = "SELECT entry_id, event_id, member_id, result FROM lottery_entries WHERE member_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String entryId = rs.getString("entry_id");
                    String eventId = rs.getString("event_id");
                    String memberIdFromDB = rs.getString("member_id");
                    String resultStr = rs.getString("result");

                    if (resultStr == null || resultStr.isEmpty()) {
                        resultStr = LotteryEntryStatus.PENDING.name();
                    }

                    LotteryEntryStatus status = LotteryEntryStatus.valueOf(resultStr);
                    list.add(new LotteryEntry(entryId, eventId, memberIdFromDB, status));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read member entries: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            System.err.println("資料庫裡的狀態字串跟 Enum 對不上: " + e.getMessage());
        }
        return list;
    }
    public void updateStatusBatch(List<LotteryEntry> entries) {

        String sql = "UPDATE lottery_entries SET result = ? WHERE entry_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // 開始事務

            for (LotteryEntry entry : entries) {
                stmt.setString(1, entry.getStatus().name());
                stmt.setString(2, entry.getEntryId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            try {

                Connection conn = DriverManager.getConnection(DB_URL);
                conn.rollback();
            } catch (SQLException rollbackE) {

            }
            throw new RuntimeException("Failed to update lottery results in batch: " + e.getMessage(), e);
        }
    }
    public void updateStatusByMemberAndEvent(String memberId, String eventId, LotteryEntryStatus result) throws SQLException {
        String sql = "UPDATE lottery_entries SET result = ? WHERE member_id = ? AND event_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, result.name());
            pstmt.setString(2, memberId);
            pstmt.setString(3, eventId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("警告：找不到 Member ID: " + memberId + " 和 Event ID: " + eventId + " 的紀錄來更新狀態。");
            } else {
                System.out.println("成功更新 " + rowsAffected + " 筆紀錄的狀態為: " + result.name());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
