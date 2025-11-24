module org.example.concertlotterysystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.concertlotterysystem to javafx.fxml;
    exports org.example.concertlotterysystem;
}