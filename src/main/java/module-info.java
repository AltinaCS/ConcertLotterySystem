module org.example.concertlotterysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // 1. JDBC 依賴 (正確)
    requires java.sql;



    // 2. 匯出/開放 啟動與控制層
    exports org.example.concertlotterysystem.application;
    opens org.example.concertlotterysystem.controllers to javafx.fxml;

    // -------------------------------------------------------------
    // 🚨 修正點：開放 FXML 所在的根套件給 javafx.fxml
    // -------------------------------------------------------------
    opens org.example.concertlotterysystem to javafx.fxml;

    // 3. 匯出業務層 (正確)
    exports org.example.concertlotterysystem.services;
    exports org.example.concertlotterysystem.entities;
    exports org.example.concertlotterysystem.repository;
}