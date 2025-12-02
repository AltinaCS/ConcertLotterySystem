package org.example.concertlotterysystem.controllers;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import org.example.concertlotterysystem.services.*;
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
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    @FXML
    private Label usernameLabel;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private GridPane eventGrid;

    private QueryEvent queryEvent;
    private static final int COLUMNS = 3;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.queryEvent = new QueryEvent();
        initializeUserState();
        loadEvents(null);
    }
    @FXML
    public void handleSearch(ActionEvent actionEvent) {
    }
    @FXML
    public void handleUserMenu(ActionEvent actionEvent) {
        Member member = SessionManager.getInstance().getCurrentMember();
        if (member == null) {
            handleLogout();
            return;
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewOrders = new MenuItem("查看訂單");
        viewOrders.setOnAction(e -> handleViewOrders());
        MenuItem createActivity = new MenuItem("建立活動");
        createActivity.setOnAction(e -> handleCreateActivity());
        MenuItem logout = new MenuItem("登出");
        logout.setOnAction(e -> handleLogout());
        Bounds bounds = userMenuButton.localToScreen(userMenuButton.getBoundsInLocal());
        double showX = bounds.getMaxX();
        double showY = bounds.getMaxY();
        contextMenu.show(userMenuButton, showX, showY);
        System.out.println(showX+"and"+showY);


        contextMenu.getItems().add(viewOrders);
        if (member.getQualification() != null &&
                member.getQualification().name().equals("ADMIN")) {

            contextMenu.getItems().add(createActivity);
        }

        contextMenu.getItems().add(logout);
        contextMenu.show(userMenuButton, showX, showY);
    }
    private void initializeUserState(){
        Member member = SessionManager.getInstance().getCurrentMember();
        if (member == null) {
            handleLogout();
            return;
        }
        usernameLabel.setText(member.getName());
    }
    private void loadEvents(String keyword){
        eventGrid.getChildren().clear();

        List<Event> events = queryEvent.searchEvents(keyword);

        int row = 0;
        int col = 0;

        for (Event event : events) {

            AnchorPane card = createEventCard(event);

            eventGrid.add(card, col, row);

            col++;
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
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
    private AnchorPane createEventCard(Event event) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(200, 300);
        card.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: #ffffff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        VBox content = new VBox(5);
        content.setPadding(new Insets(10));

        Label title = new Label(event.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        title.setWrapText(true);

        Label statusLabel = new Label("狀態: " + event.getStatus().name());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label quotaLabel = new Label("名額: " + event.getQuota());
        quotaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button actionButton = new Button("查看詳情");
        actionButton.setPrefWidth(Double.MAX_VALUE);
        actionButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        actionButton.setOnAction(e -> handleEventDetails(event));

        content.getChildren().addAll(title, statusLabel, quotaLabel, spacer, actionButton);

        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);

        card.getChildren().add(content);
        return card;
    }
    private void handleEventDetails(Event event) {
        PageRouterService.changeThePage("event-details-view.fxml", 800, 600);
    }

}
