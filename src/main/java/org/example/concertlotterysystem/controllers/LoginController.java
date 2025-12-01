package org.example.concertlotterysystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void ChangePageToForgetPwdPage(){
        PageRouterService.setPrimaryPage((Stage) loginPage.getScene().getWindow());
        PageRouterService.ChangeThePage("forgetpwd.fxml",600,400);
    }
    @FXML
    public void ChangePageToForgetRegisterPage(){
        PageRouterService.setPrimaryPage((Stage) loginPage.getScene().getWindow());
        PageRouterService.ChangeThePage("signup.fxml",600,400);
    }
    @FXML
    public void login(){

    }


}
