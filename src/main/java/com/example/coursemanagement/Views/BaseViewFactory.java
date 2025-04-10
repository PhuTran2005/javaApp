package com.example.coursemanagement.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public abstract class BaseViewFactory {

    protected AnchorPane loadReusableView(String fxmlPath, AnchorPane cachedView) {
        if (cachedView == null) {
            try {
                cachedView = new FXMLLoader(getClass().getResource(fxmlPath)).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedView;
    }

    protected AnchorPane loadFreshView(String fxmlPath) {
        try {
            return new FXMLLoader(getClass().getResource(fxmlPath)).load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void showWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showWindowWithController(String fxmlPath, Object controller, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
