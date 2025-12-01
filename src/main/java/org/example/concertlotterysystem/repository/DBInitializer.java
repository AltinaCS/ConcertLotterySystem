package org.example.concertlotterysystem.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DBInitializer {
    private static final String DB_URL = "jdbc:sqlite:lottery.db";

    public static void createNewTables() {
        String sqlMembers = "CREATE TABLE IF NOT EXISTS members ("
                + " member_id TEXT PRIMARY KEY,"
                + " name TEXT NOT NULL,"
                + " email TEXT NOT NULL"
                + ");";

        String sqlEvents = "CREATE TABLE IF NOT EXISTS events ("
                + " event_id TEXT PRIMARY KEY,"
                + " title TEXT NOT NULL,"
                + " description TEXT,"
                + " location TEXT,"
                + " status TEXT NOT NULL,"
                + " quota INTEGER NOT NULL,"
                + " per_member_limit INTEGER,"
                + " event_time TEXT,"
                + " start_time TEXT,"
                + " end_time TEXT"
                + " draw_time TEXT"
                + ");";

        String sqlEntries = "CREATE TABLE IF NOT EXISTS lottery_entries ("
                + " entry_id TEXT PRIMARY KEY,"
                + " event_id TEXT NOT NULL,"
                + " member_id TEXT NOT NULL,"
                + " result TEXT,"
                + " timestamp TEXT,"
                + " FOREIGN KEY (event_id) REFERENCES events(event_id),"
                + " FOREIGN KEY (member_id) REFERENCES members(member_id)"
                + ");";
        //需要擴充Table的話請往下寫

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlMembers);
            stmt.execute(sqlEvents);
            stmt.execute(sqlEntries);

        } catch (SQLException e) {
            System.err.println("❌ 資料庫初始化失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }
}