import java.sql.*;

public class LotteryEntryDAO {

    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    public void save(LotteryEntry entry) {
        String sql = "INSERT INTO sqlEntries (entry_id, event_id, member_id) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getEntryId());
            stmt.setString(2, entry.getEventId());
            stmt.setString(3, entry.getMemberId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save lottery entry", e);
        }
    }
}
