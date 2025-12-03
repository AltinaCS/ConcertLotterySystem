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
        // 初始化資料庫（如果資料表不存在就建立）
        DBInitializer.createNewTables();

        // 測試用：啟動時印出所有 members（若之後不需要可以移除這行）
        test.printAllMembers();

        // 將目前的 Stage 交給 PageRouterService 管理
        PageRouterService.setPrimaryPage(stage);

        // 啟動時先進登入畫面
        PageRouterService.changeThePage("login.fxml", 600, 400);
        // 如果之後想直接進主畫面，可以改成：
        // PageRouterService.goToMainPage();
    }
}
