
package com.lodgebilling;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private Stage stage;

    private final LodgeController controller = new LodgeController();

    private static final String APP_TITLE =
            "Luxury Lodge | Billing Management System";

    private static final double MIN_WIDTH = 1050;
    private static final double MIN_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        stage.setTitle(APP_TITLE);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        // Open the application in a maximized window.
        stage.setMaximized(true);

        showLogin();

        stage.show();
    }

    private Scene createScene(Parent view, double width, double height) {
        Scene scene = new Scene(view, width, height);

        URL stylesheet = getClass().getResource(
                "/com/lodgebilling/style.css"
        );

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        } else {
            System.err.println(
                    "WARNING: style.css was not found at "
                            + "/com/lodgebilling/style.css"
            );
        }

        return scene;
    }

    public void showLogin() {
        LoginView view = new LoginView(this);

        Scene scene = createScene(view.getView(), 1200, 800);

        stage.setScene(scene);
        stage.setMaximized(true);
    }

    public void showDashboard(String username) {
        DashboardView view =
                new DashboardView(this, controller, username);

        Scene scene = createScene(view.getView(), 1250, 800);

        stage.setScene(scene);
        stage.setMaximized(true);
    }

    public Stage getStage() {
        return stage;
    }

    public LodgeController getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
