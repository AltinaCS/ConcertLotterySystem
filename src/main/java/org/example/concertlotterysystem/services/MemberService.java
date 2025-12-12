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
            System.out.println("註冊失敗：Email 已被註冊。");
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

    public Member authenticate(String email, String password) {
        Member member = memberDAO.findByEmail(email);
        if (member == null) {
            System.out.println("登入失敗：Email 或密碼錯誤。");
            return null;
        }

        String storedHash = credentialDAO.findHashedPasswordByMemberId(member.getMemberId());
        if (storedHash == null) {
            System.err.println("系統錯誤：用戶 ID [" + member.getMemberId() + "] 沒有密碼。");
            return null;
        }
        if (BCrypt.checkpw(password, storedHash)) {
            SessionManager.getInstance().login(member);

            System.out.println("登入成功！歡迎 " + member.getName());
            return member;
        } else {
            System.out.println("登入失敗：Email 或密碼錯誤。");
            return null;
        }
    }
}