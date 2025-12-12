module org.example.concertlotterysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jbcrypt;
    requires org.xerial.sqlitejdbc;

    exports org.example.concertlotterysystem.application;

    opens org.example.concertlotterysystem.controllers to javafx.fxml;
    opens org.example.concertlotterysystem to javafx.fxml;

    exports org.example.concertlotterysystem.services;
    exports org.example.concertlotterysystem.entities;
    exports org.example.concertlotterysystem.repository;
}