package com.lodgebilling;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;
    private final LodgeController controller =
            new LodgeController();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Lodge Billing System");
        stage.setMinWidth(1050);
        stage.setMinHeight(700);

        showLogin();
        stage.show();
    }

    private Scene createScene(
            javafx.scene.Parent view, double width, double height) {

        Scene scene = new Scene(view, width, height);

        var stylesheet = getClass().getResource(
                "/com/lodgebilling/style.css");

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        return scene;
    }

    public void showLogin() {
        LoginView view = new LoginView(this);
        stage.setScene(createScene(view.getView(), 1100, 720));
        stage.centerOnScreen();
    }

    public void showDashboard(String username) {
        DashboardView view =
                new DashboardView(this, controller, username);

        stage.setScene(createScene(view.getView(), 1250, 800));
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}