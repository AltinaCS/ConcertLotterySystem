// MemberService.java (位於 services 套件中)

package org.example.concertlotterysystem.services;

import org.example.concertlotterysystem.entities.Member;
import org.example.concertlotterysystem.repository.MemberDAO;
import org.example.concertlotterysystem.repository.CredentialDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.UUID;

public class MemberService {

    private final MemberDAO memberDAO;
    private final CredentialDAO credentialDAO;


    public MemberService(MemberDAO memberDAO, CredentialDAO credentialDAO) {
        this.memberDAO = memberDAO;
        this.credentialDAO = credentialDAO;
    }
    public Member createMember(String name, String email, String password) {


        if (memberDAO.findByEmail(email) != null) {
            System.out.println("⚠註冊失敗：Email 已被註冊。");
            return null;
        }

        try {

            String newMemberId = UUID.randomUUID().toString();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Member newMember = new Member(newMemberId, name, email);
            memberDAO.save(newMember);
            credentialDAO.save(newMemberId, hashedPassword);

            System.out.println("會員註冊成功！ID: " + newMemberId);
            return newMember;

        } catch (SQLException e) {
            System.err.println("註冊失敗 (資料庫操作): " + e.getMessage());
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