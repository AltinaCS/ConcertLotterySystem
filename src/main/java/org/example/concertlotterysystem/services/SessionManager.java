package org.example.concertlotterysystem.services;
import org.example.concertlotterysystem.entities.Member;

public class SessionManager {

    private static SessionManager instance = null;
    private Member currentMember = null; // 儲存當前登入的 Member 實體

    private SessionManager() {
        // 私有構造器，防止外部實例化
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * 登入成功時呼叫
     */
    public void login(Member member) {
        this.currentMember = member;
        System.out.println("用戶 [" + member.getName() + "] 登入成功。");
    }

    /**
     * 登出時呼叫
     */
    public void logout() {
        this.currentMember = null;
        System.out.println("用戶登出。");
    }

    /**
     * 檢查是否已登入
     */
    public boolean isLoggedIn() {
        return this.currentMember != null;
    }

    /**
     * 取得當前登入的會員資料
     */
    public Member getCurrentMember() {
        // 🚨 建議：這裡應檢查 null 後，返回一個 Member 的副本，避免外部直接修改 Session 數據
        if (this.currentMember==null){
            return null;
        }
        return this.currentMember;
    }
}
