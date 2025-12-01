package org.example.concertlotterysystem.repository;
import java.sql.*;
public class CredentialDAO {
    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    // 註冊時：儲存新會員的密碼憑證
    public void save(String memberId, String hashedPassword) throws SQLException {
        String sql = "INSERT INTO credentials (member_id, hashed_password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    // 登入時：根據 memberId 查詢密碼雜湊值
    public String findHashedPasswordByMemberId(String memberId) {
        String sql = "SELECT hashed_password FROM credentials WHERE member_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("hashed_password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 找不到憑證或 SQL 錯誤
    }
}
