package org.example.concertlotterysystem.test;
import java.util.Arrays;
import java.util.List;
import java.sql.*;

import static org.example.concertlotterysystem.constants.Constants.DB_URL;

public class test {


    public static void printAllTablesData() {

        // 🚨 修正/補充 SQL 語句清單：包含所有四個表格
        List<String> sqls = Arrays.asList(
                // 1. MEMBERS & CREDENTIALS: 為了展示完整資訊，我們 Join 這兩個表
                "SELECT m.member_id, m.name, m.email, m.qualification, c.hashed_password " +
                        "FROM members m LEFT JOIN credentials c ON m.member_id = c.member_id",

                // 2. EVENTS
                "SELECT * FROM events",

                // 3. LOTTERY_ENTRIES
                "SELECT * FROM lottery_entries"
        );

        System.out.println("\n\n--- 🚨 DEBUG: ALL DATA TABLES ---");

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            for (String sql : sqls) {

                // 判斷當前查詢並打印標題
                if (sql.contains("members")) {
                    System.out.println("\n--- 1. MEMBERS & CREDENTIALS ---");
                } else if (sql.contains("events") && !sql.contains("lottery_entries")) {
                    System.out.println("\n--- 2. EVENTS ---");
                } else if (sql.contains("lottery_entries")) {
                    System.out.println("\n--- 3. LOTTERY ENTRIES ---");
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    if (sql.contains("members")) {
                        // 處理 MEMBERS & CREDENTIALS
                        while (rs.next()) {
                            // 注意：hashed_password 可能為 NULL (如果沒有登入憑證)
                            String hashedPassword = rs.getString("hashed_password");
                            System.out.printf(
                                    "| ID: %s | Name: %s | Email: %s | Qual: %s | HashedPwd: %s |\n",
                                    rs.getString("member_id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs.getString("qualification"),
                                    hashedPassword != null ? hashedPassword.substring(0, 10) + "..." : "[N/A]"
                            );
                        }
                    } else if (sql.contains("events") && !sql.contains("lottery_entries")) {
                        // 處理 EVENTS
                        while (rs.next()) {
                            System.out.printf(
                                    "| ID: %s | Title: %s | Loc: %s | Status: %s | Quota: %d | Limit: %d | Time: %s | Start: %s | End: %s | Draw: %s |\n",
                                    rs.getString("event_id"),
                                    rs.getString("title"),
                                    rs.getString("location"),
                                    rs.getString("status"),
                                    rs.getInt("quota"),
                                    rs.getInt("per_member_limit"),
                                    rs.getString("event_time"),
                                    rs.getString("start_time"),
                                    rs.getString("end_time"),
                                    rs.getString("draw_time")
                            );
                        }
                    } else if (sql.contains("lottery_entries")) {
                        // 處理 LOTTERY_ENTRIES
                        while (rs.next()) {
                            System.out.printf(
                                    "| EntryID: %s | EventID: %s | MemberID: %s | Result: %s | Timestamp: %s |\n",
                                    rs.getString("entry_id"),
                                    rs.getString("event_id"),
                                    rs.getString("member_id"),
                                    rs.getString("result"),
                                    rs.getString("timestamp")
                            );
                        }
                    }
                }
                System.out.println("------------------------------------");
            }

        } catch (SQLException e) {
            System.err.println("❌ Debug 查詢失敗: " + e.getMessage());
        }
    }
}
