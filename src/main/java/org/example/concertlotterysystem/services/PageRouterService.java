package org.example.concertlotterysystem.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;
import org.example.concertlotterysystem.controllers.EventDetailController;
import org.example.concertlotterysystem.entities.Event;

import java.io.IOException;

public class PageRouterService {
    private static Stage primaryStage;
    private static final Class<?> ROUTER_CLASS = PageRouterService.class;
    public static void setPrimaryPage(Stage currentStage) {
        PageRouterService.primaryStage = currentStage;
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void changeThePage(String fxmlFile, double width, double height) {

        if (primaryStage == null) {
            System.err.println("❌ primaryStage 尚未設定，請先呼叫 PageRouterService.setPrimaryPage(stage)");
            return;
        }

        final String pagePath = Constants.PATH_TO_FXML_PAGE + fxmlFile;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ROUTER_CLASS.getResource(pagePath));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            primaryStage.setTitle("Concert Lottery System");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("無法載入 FXML 頁面：" + pagePath);
            e.printStackTrace();
        }
    }

    public static <T> T changeThePageWithController(String fxmlFileName, double width, double height) {
        if (primaryStage == null) {
            System.err.println("PageRouterService: Primary stage not set.");
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(PageRouterService.class.getResource(Constants.PATH_TO_FXML_PAGE+fxmlFileName));
            Parent root = loader.load();
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, width, height);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
                primaryStage.sizeToScene();
            }
            primaryStage.show();
            return loader.getController();

        } catch (IOException e) {
            System.err.println("PageRouterService: 無法載入 FXML 檔案: " + fxmlFileName);
            e.printStackTrace();
            return null;
        }
    }

}
