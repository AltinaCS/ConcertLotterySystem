package org.example.concertlotterysystem.test;
import java.util.Arrays;
import java.util.List;
import java.sql.*;

import static org.example.concertlotterysystem.constants.Constants.DB_URL;

public class test {

    // 🚨 修正 1: DB_URL 未定義
    private static final String DB_URL = "jdbc:sqlite:lottery.db"; // 假設您的資料庫 URL

    public static void printAllMembersAndEvents() { // 🚨 修正 2: 重新命名方法以匹配功能

        // 修正 3: SQL 語句清單，使用 List<String>
        List<String> sqls = Arrays.asList(
                "SELECT m.member_id, m.name, m.email FROM members m JOIN credentials c ON m.member_id = c.member_id", // 查詢成員
                "SELECT event_id, title, location FROM events" // 查詢活動，🚨 確保欄位名稱存在
        );

        System.out.println("--- 🚨 DEBUG: ALL DATA TABLES ---");

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            for (String sql : sqls) {

                // 打印當前執行的查詢標題
                if (sql.contains("members")) {
                    System.out.println("\n--- MEMBERS & CREDENTIALS ---");
                } else if (sql.contains("events")) {
                    System.out.println("\n--- EVENTS ---");
                }

                // 🚨 修正 4: PreparedStatement 和 ResultSet 必須在 try-with-resources 塊內或手動關閉
                try (PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) { // 🚨 修正 5: 缺少分號

                    // 🚨 修正 6: 處理兩種不同的查詢結果
                    if (sql.contains("members")) {
                        while (rs.next()) {
                            System.out.printf(
                                    "| ID: %s | Name: %s | Email: %s |\n",
                                    rs.getString("member_id"),
                                    rs.getString("name"),
                                    rs.getString("email")
                            );
                        }
                    } else if (sql.contains("events")) {
                        // 針對 events 表，使用其特有的欄位
                        while (rs.next()) {
                            System.out.printf(
                                    "| Event ID: %s | Title: %s | Location: %s |\n",
                                    rs.getString("event_id"), // 🚨 假設 events 表有 event_id
                                    rs.getString("title"),
                                    rs.getString("location")
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
