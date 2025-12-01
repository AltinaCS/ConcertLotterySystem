package org.example.concertlotterysystem.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;

import java.io.IOException;

public class PageRouterService {
    // 使用 PageRouter.class 來定位資源 (假設 FXML 資源路徑設定正確)
    private static Stage primaryStage;
    private static final Class<?> ROUTER_CLASS = PageRouterService.class;
    public static void setPrimaryPage(Stage currentStage){
        PageRouterService.primaryStage = currentStage;
    }
    public static void ChangeThePage(String fxml_file, double width, double height) {

        final String PAGE = Constants.PATH_TO_FXML_PAGE + fxml_file;

        try {
            // 修正 FXML 載入：使用 PageRouter.class 來定位資源
            FXMLLoader fxmlLoader = new FXMLLoader(ROUTER_CLASS.getResource(PAGE));

            // 修正 Stage 存取：使用傳入的 currentStage
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            primaryStage.setTitle("Concert Lottery System");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("無法載入 FXML 頁面：" + PAGE);
            e.printStackTrace();
        }
    }
}
