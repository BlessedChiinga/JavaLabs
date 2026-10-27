
package com.lodgebilling;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        root.getStyleClass().add("login-screen");
        root.setMinSize(0, 0);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // LEFT SIDE: Lodge branding
        VBox brandingPanel = new VBox(24);
        brandingPanel.getStyleClass().add("login-branding");
        brandingPanel.setAlignment(Pos.CENTER_LEFT);
        brandingPanel.setPadding(new Insets(60, 70, 60, 80));
        brandingPanel.setMinWidth(0);
        brandingPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label brandIcon = new Label("L");
        brandIcon.getStyleClass().add("login-brand-icon");

        Label brandName = new Label("LUXURY LODGE");
        brandName.getStyleClass().add("login-brand-name");
        brandName.setWrapText(true);

        Label brandSubtitle = new Label("HOSPITALITY MANAGEMENT");
        brandSubtitle.getStyleClass().add("login-brand-subtitle");
        brandSubtitle.setWrapText(true);

        Region accentLine = new Region();
        accentLine.getStyleClass().add("login-accent-line");
        accentLine.setMinSize(90, 4);
        accentLine.setMaxSize(90, 4);

        Label welcomeText = new Label("Exceptional stays.\nEffortless management.");
        welcomeText.getStyleClass().add("login-hero-text");
        welcomeText.setWrapText(true);

        Label description = new Label(
                "Manage reservations, guest billing and receipts "
                        + "from one elegant workspace."
        );
        description.getStyleClass().add("login-description");
        description.setWrapText(true);
        description.setMaxWidth(480);

        Region brandingSpacer = new Region();
        VBox.setVgrow(brandingSpacer, Priority.ALWAYS);

        Label footer = new Label("PREMIUM HOSPITALITY  •  SECURE ACCESS");
        footer.getStyleClass().add("login-footer");
        footer.setWrapText(true);

        brandingPanel.getChildren().addAll(
                brandIcon,
                brandName,
                brandSubtitle,
                accentLine,
                welcomeText,
                description,
                brandingSpacer,
                footer
        );

        // RIGHT SIDE: Login form
        VBox loginPanel = new VBox(22);
        loginPanel.getStyleClass().add("login-form-panel");
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(45, 55, 45, 55));
        loginPanel.setMinWidth(0);
        loginPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox form = new VBox(12);
        form.getStyleClass().add("login-form");
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(430);
        form.setMinWidth(0);
        form.setFillWidth(true);

        Label eyebrow = new Label("WELCOME BACK");
        eyebrow.getStyleClass().add("login-eyebrow");

        Label title = new Label("Sign in");
        title.getStyleClass().add("login-form-title");

        Label subtitle = new Label(
                "Enter your administrator credentials to continue."
        );
        subtitle.getStyleClass().add("login-form-subtitle");
        subtitle.setWrapText(true);

        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("field-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("glass-input");
        usernameField.setPrefHeight(50);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("glass-input");
        passwordField.setPrefHeight(50);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        TextField visiblePassword = new TextField();
        visiblePassword.setPromptText("Enter your password");
        visiblePassword.getStyleClass().add("glass-input");
        visiblePassword.setPrefHeight(50);
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

        CheckBox showPassword = new CheckBox("Show password");
        showPassword.getStyleClass().add("login-show-password");

        showPassword.selectedProperty().addListener((obs, oldValue, show) -> {
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);

            visiblePassword.setVisible(show);
            visiblePassword.setManaged(show);
        });

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginButton = new Button("SIGN IN  →");
        loginButton.getStyleClass().add("login-submit-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(52);
        loginButton.setDefaultButton(true);

        Label securityNote = new Label(
                "Authorized administrator access only."
        );
        securityNote.getStyleClass().add("login-security-note");
        securityNote.setWrapText(true);

        Runnable loginAction = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please enter both username and password.");
                return;
            }

            if (username.equals("admin") && password.equals("admin")) {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                main.showDashboard(username);
            } else {
                showError(errorLabel, "Incorrect username or password.");
                passwordField.clear();
                visiblePassword.clear();
                passwordField.requestFocus();
            }
        };

        loginButton.setOnAction(event -> loginAction.run());

        HBox passwordHeading = new HBox();
        passwordHeading.setAlignment(Pos.CENTER_LEFT);
        passwordHeading.getChildren().add(passwordLabel);

        Region headingSpacer = new Region();
        HBox.setHgrow(headingSpacer, Priority.ALWAYS);

        Label requiredLabel = new Label("REQUIRED");
        requiredLabel.getStyleClass().add("login-required-label");

        passwordHeading.getChildren().addAll(headingSpacer, requiredLabel);

        form.getChildren().addAll(
                eyebrow,
                title,
                subtitle,
                spacer(12),
                usernameLabel,
                usernameField,
                passwordHeading,
                passwordContainer,
                showPassword,
                errorLabel,
                spacer(8),
                loginButton,
                securityNote
        );

        loginPanel.getChildren().add(form);

        // Full-window split layout
        HBox layout = new HBox(0, brandingPanel, loginPanel);
        layout.getStyleClass().add("login-layout");
        layout.setAlignment(Pos.CENTER);
        layout.setFillHeight(true);
        layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setHgrow(brandingPanel, Priority.ALWAYS);
        HBox.setHgrow(loginPanel, Priority.ALWAYS);

        brandingPanel.prefWidthProperty().bind(root.widthProperty().multiply(0.56));
        loginPanel.prefWidthProperty().bind(root.widthProperty().multiply(0.44));

        root.getChildren().setAll(layout);

        // Enter submits the form from either password field.
        passwordField.setOnAction(event -> loginAction.run());
        visiblePassword.setOnAction(event -> loginAction.run());
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
        return region;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }
}
