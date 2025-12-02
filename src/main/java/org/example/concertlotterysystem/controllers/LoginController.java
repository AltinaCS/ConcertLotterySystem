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
    private MemberService memberService;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MemberDAO memberDAO = new MemberDAO();
        CredentialDAO credentialDAO = new CredentialDAO();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("錯誤");
            alert.setHeaderText(null);
            alert.setContentText("請填寫帳號及密碼");
            alert.showAndWait();
            return;
        }
        Member authenticatedMember = memberService.authenticate(email, password);
        if (authenticatedMember != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("成功");
            alert.setHeaderText(null);
            alert.setContentText("登入成功！");
            alert.showAndWait();
            PageRouterService.changeThePage("main-view.fxml",  600, 400);

        } else {
            messageLabel.setText("Email或密碼錯誤，請重新嘗試。");
            passwordField.clear();
        }
    }


}
