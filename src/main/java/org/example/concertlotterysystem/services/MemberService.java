// MemberService.java (位於 services 套件中)

package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Member;
import org.example.concertlotterysystem.repository.MemberDAO;
import org.example.concertlotterysystem.repository.CredentialDAO;
import org.mindrot.jbcrypt.BCrypt; // 使用 jBCrypt 函式庫
import java.sql.SQLException;
import java.util.UUID;

public class MemberService {

    private final MemberDAO memberDAO;
    private final CredentialDAO credentialDAO;

    // 構造函數：注入 DAO 依賴
    public MemberService(MemberDAO memberDAO, CredentialDAO credentialDAO) {
        this.memberDAO = memberDAO;
        this.credentialDAO = credentialDAO;
    }

    // -------------------------------------------------------------
    // 核心方法一：會員註冊 (Registration)
    // -------------------------------------------------------------
    public Member createMember(String name, String email, String password) {

        // 1. 業務檢查：Email 唯一性
        if (memberDAO.findByEmail(email) != null) {
            System.out.println("⚠️ 註冊失敗：Email 已被註冊。");
            return null;
        }

        try {
            // 2. 準備數據：ID 和雜湊密碼
            String newMemberId = UUID.randomUUID().toString();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // 雜湊密碼

            // 3. 創建 Member 實體 (不包含密碼)
            Member newMember = new Member(newMemberId, name, email);

            // 4. 執行資料庫操作：儲存會員和憑證（這裡需要交易控制來保證兩者都成功）
            memberDAO.save(newMember); // 儲存會員資訊到 members 表
            credentialDAO.save(newMemberId, hashedPassword); // 儲存憑證到 credentials 表

            System.out.println("✅ 會員註冊成功！ID: " + newMemberId);
            return newMember;

        } catch (SQLException e) {
            System.err.println("❌ 註冊失敗 (資料庫操作): " + e.getMessage());
            // 在實際應用中，這裡應包含事務回滾 (Transaction Rollback) 邏輯
            return null;
        }
    }

    // -------------------------------------------------------------
    // 核心方法二：帳號登入 (Login / Authentication)
    // -------------------------------------------------------------
    public Member authenticate(String email, String password) {

        // 1. 根據 Email 獲取會員資料
        Member member = memberDAO.findByEmail(email);
        if (member == null) {
            // 找不到會員，或 Email 不存在
            System.out.println("⚠️ 登入失敗：Email 或密碼錯誤。");
            return null;
        }

        // 2. 根據會員 ID 獲取雜湊密碼
        String storedHash = credentialDAO.findHashedPasswordByMemberId(member.getMemberId());
        if (storedHash == null) {
            // 系統錯誤：會員存在但沒有密碼憑證
            System.err.println("❌ 系統錯誤：用戶 ID [" + member.getMemberId() + "] 缺少密碼憑證。");
            return null;
        }

        // 3. 安全處理：使用 jBCrypt 比對密碼
        if (BCrypt.checkpw(password, storedHash)) {
            // 驗證成功

            // 4. 維持登入狀態：更新 SessionManager
            SessionManager.getInstance().login(member);

            System.out.println("✅ 登入成功！歡迎 " + member.getName());
            return member;
        } else {
            // 密碼不匹配
            System.out.println("⚠️ 登入失敗：Email 或密碼錯誤。");
            return null;
        }
    }
}