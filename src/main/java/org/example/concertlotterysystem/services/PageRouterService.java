package org.example.concertlotterysystem.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;

import java.io.IOException;

public class PageRouterService {
    private static Stage primaryStage;
    private static final Class<?> ROUTER_CLASS = PageRouterService.class;
    public static void setPrimaryPage(Stage currentStage){
        PageRouterService.primaryStage = currentStage;
    }
    public static void changeThePage(String fxml_file, double width, double height) {

        final String PAGE = Constants.PATH_TO_FXML_PAGE+fxml_file;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ROUTER_CLASS.getResource(PAGE));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Concert Lottery System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("無法載入 FXML 頁面：" + PAGE);
            e.printStackTrace();
        }
    }
}
