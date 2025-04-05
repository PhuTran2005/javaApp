module com.example.coursemanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics; // css

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires spring.security.crypto;

    opens com.example.coursemanagement to javafx.fxml;
    opens com.example.coursemanagement.Controllers to javafx.fxml;  // 👈
    opens com.example.coursemanagement.Controllers.Client to javafx.fxml;  // 👈
    opens com.example.coursemanagement.Controllers.Admin to javafx.fxml;//mở package cho FXML

    exports com.example.coursemanagement;
    exports com.example.coursemanagement.Controllers;
    exports com.example.coursemanagement.Controllers.Admin;
    exports com.example.coursemanagement.Controllers.Client;
    exports com.example.coursemanagement.Models;
    exports com.example.coursemanagement.Views;
    exports com.example.coursemanagement.Utils;
    exports com.example.coursemanagement.Service;
    exports com.example.coursemanagement.Respository;
    opens com.example.coursemanagement.Utils to javafx.fxml;

}
