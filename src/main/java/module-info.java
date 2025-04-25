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
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.net.http;
    requires org.json;      // Yêu cầu module org.json cho JSONObject, JSONArray
    requires com.google.zxing; // Thêm để hỗ trợ ZXing core
    requires com.google.zxing.javase; // Thêm để hỗ trợ ZXing javase


    opens com.example.coursemanagement to javafx.fxml;
    opens com.example.coursemanagement.Controllers to javafx.fxml;  // 👈
    opens com.example.coursemanagement.Controllers.Client to javafx.fxml;  // 👈

    opens com.example.coursemanagement.Controllers.Admin to javafx.fxml;  // 👈


    exports com.example.coursemanagement;
    exports com.example.coursemanagement.Controllers;
    exports com.example.coursemanagement.Controllers.Admin;
    exports com.example.coursemanagement.Controllers.Client;
    exports com.example.coursemanagement.Models;
    exports com.example.coursemanagement.Views;
    exports com.example.coursemanagement.Utils;
    exports com.example.coursemanagement.Service;
    exports com.example.coursemanagement.Repository;
    opens com.example.coursemanagement.Utils to javafx.fxml;
    exports com.example.coursemanagement.Controllers.Admin.CourseController;
    opens com.example.coursemanagement.Controllers.Admin.CourseController to javafx.fxml;
    exports com.example.coursemanagement.Controllers.Client.CourseClientController;
    opens com.example.coursemanagement.Controllers.Client.CourseClientController to javafx.fxml;
    exports com.example.coursemanagement.Controllers.Client.CartController;
    opens com.example.coursemanagement.Controllers.Client.CartController to javafx.fxml;

}
