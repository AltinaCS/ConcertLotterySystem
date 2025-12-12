package org.example.concertlotterysystem.repository;

import org.example.concertlotterysystem.entities.Member;

import java.sql.*;

import org.example.concertlotterysystem.entities.Member;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {

    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    /**
     * [註冊時使用] 儲存新的會員記錄到 members 資料表。
     * @param member 待儲存的 Member 實體（不含密碼）
     */
    public void save(Member member) throws SQLException {

        String sql = "INSERT INTO members (member_id, name, email) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.executeUpdate();

        }
    }

    /**
     * [註冊和登入時使用] 根據 Email 查詢會員資料，用於唯一性檢查或認證。
     * @param email 會員 Email
     * @return 匹配的 Member 實體（不包含密碼），如果找不到則回傳 null。
     */
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
