package org.example.concertlotterysystem.test;

import java.sql.*;

import static org.example.concertlotterysystem.constants.Constants.DB_URL;

public class test {
    public static void printAllMembers() {
        String sql = "SELECT m.member_id, m.name, m.email FROM members m JOIN credentials c on m.member_id = c.member_id";

        System.out.println("--- 🚨 DEBUG: ALL MEMBERS TABLE ---");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf(
                        "| ID: %s | Name: %s | Email: %s |\n",
                        rs.getString("member_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
            System.out.println("------------------------------------");

        } catch (SQLException e) {
            System.err.println("❌ Debug 查詢失敗: " + e.getMessage());
        }
    }
}
