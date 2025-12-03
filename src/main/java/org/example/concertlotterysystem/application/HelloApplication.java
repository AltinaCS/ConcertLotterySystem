package org.example.concertlotterysystem.application;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.concertlotterysystem.repository.DBInitializer;
import org.example.concertlotterysystem.services.PageRouterService;
import org.example.concertlotterysystem.test.test;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DBInitializer.createNewTables();
        test.printAllMembers();
        PageRouterService.setPrimaryPage(stage);
        PageRouterService.changeThePage("login.fxml", 600, 400);
    }
}
