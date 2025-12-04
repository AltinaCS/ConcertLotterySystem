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

            stmt.setString(1, entry.getEntryId());
            stmt.setString(2, entry.getEventId());
            stmt.setString(3, entry.getMemberId());
            stmt.setString(4, "PENDING");
            stmt.setString(5, LocalDateTime.now().toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save lottery entry", e);
        }
    }
    public LotteryEntry findByMemberAndEvent(String memberId, String eventId) {
        // 查詢 SQL：WHERE 條件需要同時滿足 member_id 和 event_id
        String sql = "SELECT entry_id, member_id, event_id, result FROM lottery_entries WHERE member_id = ? AND event_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 只需讀取第一筆結果
                    String entryId = rs.getString("entry_id");
                    String resultStr = rs.getString("result");

                    // 處理可能為 null 的狀態
                    if (resultStr == null || resultStr.isEmpty()) {
                        resultStr = LotteryEntryStatus.PENDING.name();
                    }

                    LotteryEntryStatus status = LotteryEntryStatus.valueOf(resultStr);

                    // 返回找到的 LotteryEntry 物件
                    return new LotteryEntry(entryId, eventId, memberId, status);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing entry: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            System.err.println("資料庫裡的狀態字串跟 Enum 對不上: " + e.getMessage());
        }
        return null; // 未找到記錄
    }
    public List<LotteryEntry> findByMemberId(String memberId) {
        List<LotteryEntry> list = new ArrayList<>();
        // 查詢 SQL：從 result 欄位讀取狀態
        String sql = "SELECT entry_id, event_id, member_id, result FROM lottery_entries WHERE member_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String entryId = rs.getString("entry_id");
                    String eventId = rs.getString("event_id");
                    String memberIdFromDB = rs.getString("member_id");
                    String resultStr = rs.getString("result"); // 從 result 欄位讀取

                    // 檢查 resultStr 是否為 null (確保舊資料或未抽籤資料不會拋出 NPE/IllegalArgumentException)
                    if (resultStr == null || resultStr.isEmpty()) {
                        resultStr = LotteryEntryStatus.PENDING.name();
                    }

                    LotteryEntryStatus status = LotteryEntryStatus.valueOf(resultStr);

                    // 使用 (entryId, eventId, memberId, status) 建構子
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
        // 🚨 注意：資料庫欄位名稱為 result
        String sql = "UPDATE lottery_entries SET result = ? WHERE entry_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // 開始事務

            for (LotteryEntry entry : entries) {
                stmt.setString(1, entry.getStatus().name()); // WON 或 LOST
                stmt.setString(2, entry.getEntryId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit(); // 提交事務

        } catch (SQLException e) {
            try {
                // 嘗試回滾
                Connection conn = DriverManager.getConnection(DB_URL);
                conn.rollback();
            } catch (SQLException rollbackE) {
                // 忽略回滾失敗
            }
            throw new RuntimeException("Failed to update lottery results in batch: " + e.getMessage(), e);
        }
    }
}
