
package com.lodgebilling;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final Main main;
    private final StackPane root = new StackPane();

    public LoginView(Main main) {
        this.main = main;
        buildUI();
    }

    public Parent getView() {
        return root;
    }

    private void buildUI() {

        // Full-screen lodge photograph.
        root.getStyleClass().add("photo-background");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setMinSize(0, 0);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Main transparent glass card.
        VBox loginCard = new VBox(15);
        loginCard.getStyleClass().add("glass-card");
        loginCard.getStyleClass().add("login-glass-card");

        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(38, 42, 35, 42));
        loginCard.setSpacing(16);
        loginCard.setPrefWidth(440);
        loginCard.setMaxWidth(440);
        loginCard.setMinWidth(300);

        // Lodge emblem.
        Label logo = new Label("L");
        logo.getStyleClass().add("login-logo");
        logo.setAlignment(Pos.CENTER);

        // Heading.
        Label title = new Label("LOGIN");
        title.getStyleClass().add("login-heading");

        Label subtitle = new Label("Login to your account");
        subtitle.getStyleClass().add("login-subtitle");

        // Username.
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.getStyleClass().add("login-field-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("login-text-field");
        usernameField.setPrefHeight(48);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Password.
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.getStyleClass().add("login-field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("login-text-field");
        passwordField.setPrefHeight(48);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Visible password field.
        TextField visiblePassword = new TextField();
        visiblePassword.setPromptText("Enter your password");
        visiblePassword.getStyleClass().add("login-text-field");
        visiblePassword.setPrefHeight(48);
        visiblePassword.setMaxWidth(Double.MAX_VALUE);
        visiblePassword.setManaged(false);
        visiblePassword.setVisible(false);

        visiblePassword.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        StackPane passwordContainer = new StackPane(
                passwordField,
                visiblePassword
        );
        passwordContainer.setMaxWidth(Double.MAX_VALUE);

        // Show password.
        CheckBox showPassword = new CheckBox("Show password");
        showPassword.getStyleClass().add("login-checkbox");

        showPassword.selectedProperty().addListener(
                (observable, oldValue, show) -> {
                    passwordField.setVisible(!show);
                    passwordField.setManaged(!show);

                    visiblePassword.setVisible(show);
                    visiblePassword.setManaged(show);
                }
        );

        // Error message.
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("login-error");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Login button.
        Button loginButton = new Button("LOGIN");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefHeight(50);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setDefaultButton(true);

        // Footer.
        Label footer = new Label(
                "HORIZON 21  |  MANAGEMENT SYSTEM"
        );
        footer.getStyleClass().add("login-footer-text");
        footer.setWrapText(true);

        Label credentialsHint = new Label(
                "Demo login: admin / admin"
        );
        credentialsHint.getStyleClass().add("login-hint");

        // Login validation.
        Runnable loginAction = () -> {

            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError(
                        errorLabel,
                        "Please enter your username and password."
                );
                return;
            }

            if (username.equals("admin") && password.equals("admin")) {

                errorLabel.setVisible(false);
                errorLabel.setManaged(false);

                main.showDashboard(username);

            } else {

                showError(
                        errorLabel,
                        "Incorrect username or password. Try again."
                );

                passwordField.clear();
                visiblePassword.clear();

                passwordField.requestFocus();
            }
        };

        loginButton.setOnAction(event -> loginAction.run());

        // Press Enter to log in.
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> loginAction.run());
        visiblePassword.setOnAction(event -> loginAction.run());

        // Assemble the login card.
        loginCard.getChildren().addAll(
                logo,
                title,
                subtitle,
                createSpacer(8),
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordContainer,
                showPassword,
                errorLabel,
                createSpacer(5),
                loginButton,
                footer,
                credentialsHint
        );

        // Center the card over the photograph.
        root.getChildren().add(loginCard);
        StackPane.setAlignment(loginCard, Pos.CENTER);
    }

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        spacer.setPrefHeight(height);
        spacer.setMaxHeight(height);
        return spacer;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }
}
