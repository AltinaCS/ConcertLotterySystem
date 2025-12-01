package org.example.concertlotterysystem.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;
import org.example.concertlotterysystem.repository.DBInitializer;
import org.example.concertlotterysystem.services.PageRouterService;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DBInitializer.createNewTables();
        PageRouterService.setPrimaryPage(stage);
        PageRouterService.ChangeThePage("login.fxml", 600, 400);
    }
}
