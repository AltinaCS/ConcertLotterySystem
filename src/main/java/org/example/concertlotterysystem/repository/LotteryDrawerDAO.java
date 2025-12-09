package org.example.concertlotterysystem.repository;
import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.entities.LotteryEntry;
import org.example.concertlotterysystem.entities.LotteryEntryStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LotteryDrawerDAO {
    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    public List<LotteryEntry> getLotteryEntries(Event event){
        List<LotteryEntry> list = new ArrayList<>();

        String sql = "SELECT entry_id, event_id, member_id, result " +
                "FROM lottery_entries WHERE event_id = ? AND result = 'PENDING'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getEventId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String entryId = rs.getString("entry_id");
                    String eventId = rs.getString("event_id");
                    String memberId = rs.getString("member_id");
                    String resultStr = rs.getString("result");


                    LotteryEntryStatus resultStatus = LotteryEntryStatus.valueOf(resultStr);

                    // 5. 建立物件並加入清單
                    LotteryEntry entry = new LotteryEntry(entryId, eventId, memberId, resultStatus);
                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            System.out.println("讀取報名資料失敗: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("資料庫裡的狀態字串跟 Enum 對不上: " + e.getMessage());
        }

        return list;
    }
}
