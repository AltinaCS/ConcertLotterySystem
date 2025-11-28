package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Event;
import org.example.concertlotterysystem.repository.EventDAO;

import java.util.List;

public class QueryEvent {

    // Service 持有一個 DAO 物件，用來呼叫它做事
    private EventDAO eventDAO;

    public QueryEvent() {
        this.eventDAO = new EventDAO();
    }

    /**
     * 提供給 UI 呼叫的搜尋功能
     * @param keyword 使用者輸入的關鍵字
     * @return 符合條件的活動列表
     */
    public List<Event> searchEvents(String keyword) {
        // 1. 邏輯處理：防呆機制
        // 如果使用者什麼都沒打 (null)，我們把它當作空字串處理
        if (keyword == null) {
            keyword = "";
        }

        // 去掉前後空白 (trim)，避免使用者不小心多按空白鍵導致查不到
        String safeKeyword = keyword.trim();

        // 2. 呼叫 DAO
        // 這裡利用 SQL LIKE '%%' 的特性：如果是空字串，會搜尋出所有活動
        return eventDAO.searchEventsByKeyword(safeKeyword);
    }
}