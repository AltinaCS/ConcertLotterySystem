package org.example.concertlotterysystem.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.concertlotterysystem.constants.Constants;

import java.io.IOException;

public class PageRouterService {

    // 目前使用的主舞台（在 HelloApplication 啟動時設定）
    private static Stage primaryStage;

    // 用來定位 FXML 資源的 class
    private static final Class<?> ROUTER_CLASS = PageRouterService.class;

    /**
     * 在 Application 啟動時呼叫一次，設定全域共用的 Stage
     */
    public static void setPrimaryPage(Stage currentStage) {
        PageRouterService.primaryStage = currentStage;
    }

    /**
     * 通用的換頁方法：給 FXML 檔名（不含路徑）、寬與高
     */
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
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("❌ 無法載入 FXML 頁面：" + pagePath);
            e.printStackTrace();
        }
    }

    /**
     * 方便用的 helper：切到主畫面（例如 concert list / search 畫面）
     */
    public static void goToMainPage() {
        changeThePage("main-view.fxml", 800, 600);
    }

    /**
     * 方便用的 helper：切到 Create Activity 畫面
     * 在 controller 裡可以直接呼叫 PageRouterService.goToCreateActivity();
     */
    public static void goToCreateActivity() {
        changeThePage("create-activity-view.fxml", 800, 600);
    }
}
