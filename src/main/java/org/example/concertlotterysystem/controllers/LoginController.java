package org.example.concertlotterysystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.concertlotterysystem.entities.Member;
import org.example.concertlotterysystem.repository.CredentialDAO;
import org.example.concertlotterysystem.repository.MemberDAO;
import org.example.concertlotterysystem.services.MemberService;
import org.example.concertlotterysystem.services.PageRouterService;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane loginPage;
    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Hyperlink forgetPwdLink;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;
    // 2. Service 實例現在作為類的成員變數，但暫時不初始化
    private MemberService memberService;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 手動初始化 Service 依賴
        MemberDAO memberDAO = new MemberDAO();
        CredentialDAO credentialDAO = new CredentialDAO();

        // 最終初始化 MemberService
        this.memberService = new MemberService(memberDAO, credentialDAO);
        passwordField.clear();
        accountField.clear();
    }
    @FXML
    public void ChangePageToForgetPwdPage(){
        PageRouterService.setPrimaryPage((Stage) loginPage.getScene().getWindow());
        PageRouterService.changeThePage("forgetpwd.fxml",600,400);
    }
    @FXML
    public void ChangePageToForgetRegisterPage(){
        PageRouterService.setPrimaryPage((Stage) loginPage.getScene().getWindow());
        PageRouterService.changeThePage("signup.fxml",600,400);
    }
    @FXML
    public void login(){
        String email = accountField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            accountField.setText("請輸入 Email 和密碼。");
            return;
        }

        // 1. 呼叫 Service 執行認證
        Member authenticatedMember = memberService.authenticate(email, password);

        // 2. 處理結果
        if (authenticatedMember != null) {
            // 登入成功：SessionManager 已經在 Service 內部更新

            // 3. 導航到主頁面
            // 💡 假設主頁 FXML 為 'main-view.fxml'
            PageRouterService.changeThePage("main-view.fxml",  600, 400);

        } else {
            // 登入失敗：Service 返回 null
            messageLabel.setText("Email 或密碼錯誤，請重新嘗試。");
            passwordField.clear();
        }
    }


}
