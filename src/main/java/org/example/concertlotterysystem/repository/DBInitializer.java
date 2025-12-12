package org.example.concertlotterysystem.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.concertlotterysystem.constants.Constants;

import static org.example.concertlotterysystem.constants.Constants.DB_URL;

public class DBInitializer {


    public static void createNewTables() {
        String sqlMembers = "CREATE TABLE IF NOT EXISTS members ("
                + " member_id TEXT PRIMARY KEY,"
                + " name TEXT NOT NULL,"
                + " email TEXT NOT NULL,"
                + " qualification TEXT NOT NULL DEFAULT 'MEMBER' "
                + ");";

        String sqlCredentials = "CREATE TABLE IF NOT EXISTS credentials ("
                + " member_id TEXT PRIMARY KEY," // FK，也是 PK
                + " hashed_password TEXT NOT NULL,"
                + " FOREIGN KEY (member_id) REFERENCES members(member_id)"
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
                + " end_time TEXT,"
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
        //需要擴充Table時這個一定要擴充
        /** **/
        String[] sql_tables = {sqlMembers,sqlEvents,sqlCredentials,sqlEntries};
        /** **/
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            for (String table: sql_tables){
                stmt.execute(table);
            }
        } catch (SQLException e) {
            System.err.println("資料庫初始化失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
