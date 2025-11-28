package org.example.concertlotterysystem.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final String LOGIN_PAGE = Constants.PATH_TO_FXML_PAGE+"login.fxml";
        // 使用 getClass().getResource(String path)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LOGIN_PAGE));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
