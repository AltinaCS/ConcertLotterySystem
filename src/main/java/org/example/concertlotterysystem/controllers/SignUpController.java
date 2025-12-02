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

public class SignUpController implements Initializable {
    @FXML
    private AnchorPane registerPage;
    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordConfirmField;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private Button registerButton;
    @FXML
    private TextField nameField;
    @FXML
    private Label messageLabel;

    private MemberService memberService;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MemberDAO memberDAO = new MemberDAO();
        CredentialDAO credentialDAO = new CredentialDAO();
        this.memberService = new MemberService(memberDAO, credentialDAO);
    }
    @FXML
    public void register(){
        String name = nameField.getText();
        String email = accountField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("所有欄位均為必填。");
            return;
        }
        Member newMember = memberService.createMember(name, email, password);

        if (newMember != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("成功");
            alert.setHeaderText(null);
            alert.setContentText("註冊成功！");
            alert.showAndWait();
            PageRouterService.changeThePage("login.fxml",  600, 400);

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("錯誤");
            alert.setHeaderText(null);
            alert.setContentText("註冊失敗，該 Email 可能已被使用。");
            alert.showAndWait();
            messageLabel.setText("註冊失敗，該 Email 可能已被使用。");
        }
    }
    @FXML
    public void changePageToLoginPage(){
        PageRouterService.setPrimaryPage((Stage) registerPage.getScene().getWindow());
        PageRouterService.changeThePage("login.fxml",600,400);
    }


}
