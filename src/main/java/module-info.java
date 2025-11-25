module org.example.concertlotterysystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.concertlotterysystem to javafx.fxml;
    exports org.example.concertlotterysystem;
    exports org.example.concertlotterysystem.application;
    opens org.example.concertlotterysystem.application to javafx.fxml;
    exports org.example.concertlotterysystem.controllers;
    opens org.example.concertlotterysystem.controllers to javafx.fxml;
}