package com.lodgebilling;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private final Main main;

    public LoginView(Main main) {
        this.main = main;
    }

    public Parent getView() {
        StackPane root = new StackPane();
        root.getStyleClass().add("photo-background");

        VBox card = new VBox(18);
        card.setMaxWidth(390);
        card.setPadding(new Insets(38));
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("glass-card");

        Label icon = new Label("⌂");
        icon.getStyleClass().add("lodge-icon");

        Label title = new Label("LODGE BILLING");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label(
                "Your lodge, managed beautifully");
        subtitle.getStyleClass().add("muted-text");

        TextField username = new TextField();
        username.setPromptText("Username");
        username.getStyleClass().add("glass-input");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("glass-input");

        Button login = new Button("SIGN IN");
        login.setMaxWidth(Double.MAX_VALUE);
        login.getStyleClass().add("primary-button");

        Label message = new Label();
        message.getStyleClass().add("error-text");

        Label demo = new Label("DEMO ACCESS  •  admin / admin");
        demo.getStyleClass().add("muted-text");

        login.setOnAction(event -> {
            if (username.getText().trim().equals("admin")
                    && password.getText().equals("admin")) {
                main.showDashboard("admin");
            } else {
                message.setText(
                        "Incorrect username or password.");
            }
        });

        password.setOnAction(event -> login.fire());

        card.getChildren().addAll(
                icon, title, subtitle,
                username, password, login, message, demo
        );

        root.getChildren().add(card);
        return root;
    }
}