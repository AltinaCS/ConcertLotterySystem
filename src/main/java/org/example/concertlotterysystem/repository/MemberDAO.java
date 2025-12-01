package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.Member;

import java.sql.*;

public class MemberDAO {
    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    // ... 原始的 save 方法 ...

    // 新增：根據 Email 查詢會員（用於檢查唯一性）
    public Member findByEmail(String email) {
        String sql = "SELECT member_id, name, email FROM members WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getString("member_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
