package org.example.concertlotterysystem.services;
import org.example.concertlotterysystem.entities.Member;

public class SessionManager {

    private static SessionManager instance = null;
    private Member currentMember = null;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    public void login(Member member) {
        this.currentMember = member;
        System.out.println("用戶 [" + member.getName() + "] 登入成功。");
    }
    public void logout() {
        this.currentMember = null;
        System.out.println("用戶登出。");
    }
    public boolean isLoggedIn() {
        return this.currentMember != null;
    }
    public Member getCurrentMember() {

        if (this.currentMember==null){
            return null;
        }
        return this.currentMember;
    }
}
