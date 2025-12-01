package org.example.concertlotterysystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.concertlotterysystem.services.PageRouterService;

public class SignUpController {
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
    public void register(){

    }
    @FXML
    public void changePageToLoginPage(){
        PageRouterService.setPrimaryPage((Stage) registerPage.getScene().getWindow());
        PageRouterService.ChangeThePage("login.fxml",600,400);
    }
}
