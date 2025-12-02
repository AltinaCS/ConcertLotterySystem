package org.example.concertlotterysystem.controllers;// MainController.java (位於 controllers 套件中)

// ... (其他引入保持不變) ...
// 🚨 假設 PageRouterService 位於 utilities/services 套件中，且已正確引入

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import org.example.concertlotterysystem.services.*; // 修正引入名稱
import org.example.concertlotterysystem.entities.Event; // 修正引入名稱
import org.example.concertlotterysystem.entities.Member;
import org.example.concertlotterysystem.services.SessionManager;
import org.example.concertlotterysystem.services.PageRouterService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;
// ... (其他引入) ...


public class MainController implements Initializable {

    // ... (FXML 綁定, Service 依賴和 initialize 方法保持不變) ...

    // -------------------------------------------------------------
    // 用戶狀態處理 (右上角)
    // -------------------------------------------------------------
    // ... (initializeUserState 保持不變) ...
    @FXML
    private Label usernameLabel;         // 綁定 fx:id="usernameLabel"

    @FXML
    private Button userMenuButton;        // 綁定 fx:id="userMenuButton"
    @FXML
    private Button searchButton;
    // 2. 搜尋區
    @FXML
    private TextField searchField;       // 綁定 fx:id="searchField"

    // 3. 活動列表區
    @FXML
    private GridPane eventGrid;           // 綁定 fx:id="eventGrid"


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Member member = SessionManager.getInstance().getCurrentMember();
        if (member == null) {
            handleLogout(); // 如果沒有登入狀態，直接登出
            return;
        }
        usernameLabel.setText(member.getName());
    }
    @FXML
    public void handleSearch(ActionEvent actionEvent) {
    }
    @FXML
    public void handleUserMenu(ActionEvent actionEvent) {
        // 1. 獲取當前使用者物件
        Member member = SessionManager.getInstance().getCurrentMember();
        if (member == null) {
            handleLogout(); // 如果沒有登入狀態，直接登出
            return;
        }

        // 2. 創建上下文菜單
        ContextMenu contextMenu = new ContextMenu();

        // --- 創建三個選項 ---

        // 選項一：查看訂單
        MenuItem viewOrders = new MenuItem("查看訂單");
        viewOrders.setOnAction(e -> handleViewOrders());

        // 選項二：建立活動 (需要 ADMIN 資格)
        MenuItem createActivity = new MenuItem("建立活動");
        createActivity.setOnAction(e -> handleCreateActivity());

        // 選項三：登出
        MenuItem logout = new MenuItem("登出");
        logout.setOnAction(e -> handleLogout());
        // 1. 獲取按鈕在螢幕上的坐標
        Bounds bounds = userMenuButton.localToScreen(userMenuButton.getBoundsInLocal());

        // 3. 計算 X 坐標：讓 ContextMenu 的右側對齊按鈕的右側
        //    公式：按鈕右側 X - 菜單寬度
        double showX = bounds.getMaxX();

        // 4. 計算 Y 坐標：讓 ContextMenu 的頂部對齊按鈕的底部 (正下方)
        double showY = bounds.getMaxY();

        // 5. 顯示菜單 (使用螢幕絕對坐標)
        contextMenu.show(userMenuButton, showX, showY);
        System.out.println(showX+"and"+showY);
        // --- 根據資格添加菜單項 ---

        contextMenu.getItems().add(viewOrders);

        // 檢查會員資格是否為 ADMIN
        // 🚨 註意：這裡假設 MemberQualificationStatus.ADMIN 是正確的枚舉名稱
        if (member.getQualification() != null &&
                member.getQualification().name().equals("ADMIN")) {

            contextMenu.getItems().add(createActivity);
        }

        contextMenu.getItems().add(logout);

        // 顯示菜單 (以 userMenuButton 為錨點)
        contextMenu.show(userMenuButton, showX, showY);
    }
    private void handleLogout() {
        // 清除 Session 狀態
        SessionManager.getInstance().logout();
        PageRouterService.changeThePage("login.fxml", 600, 400);
    }
    private void handleCreateActivity() {

        PageRouterService.changeThePage("create-activity-view.fxml", 800, 600);
    }
    private void handleViewOrders() {
        PageRouterService.changeThePage("user-order-view.fxml", 600, 400);
    }

}
