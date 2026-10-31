package com.lodgebilling;

import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Help & Support page:
 *   1. Contact Admin
 *   2. Report a Problem  (saved to a text file the admin can read)
 *   3. Common Problems   (expandable questions and answers)
 */
public final class HelpView {

    // ===== EDIT THESE DETAILS =====
    private static final String ADMIN_NAME  = "Blessed Chiinga";
    private static final String ADMIN_PHONE = "+260 774 402 147";
    private static final String ADMIN_EMAIL = "admin@Horizon21lodge.com";
    private static final String ADMIN_HOURS = "Monday to Saturday, 08:00 - 17:00";

    // Problem reports are appended to this file.
    private static final Path REPORT_FILE = Path.of(
            System.getProperty("user.home"),
            "Horizon21Lodge",
            "problem-reports.txt"
    );

    private HelpView() {
    }

    /** Builds the whole Help page. Add the result to the dashboard content. */
    public static VBox build(String username) {
        VBox page = new VBox(20);
        page.setMaxWidth(1000);
        page.setFillWidth(true);

        page.getChildren().addAll(
                contactCard(),
                reportCard(username),
                commonProblemsCard()
        );

        return page;
    }

    // ---------------------------------------------------------
    // 1. Contact Admin
    // ---------------------------------------------------------

    private static VBox contactCard() {
        VBox card = card();

        Label title = new Label("Contact Admin");
        title.getStyleClass().add("section-title");

        Label intro = muted(
                "Need a password reset, access to a feature, or help with "
                        + "something that is not listed below? Reach the administrator:"
        );

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(120);
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, valueColumn);

        addRow(grid, 0, "Name", ADMIN_NAME);
        addRow(grid, 1, "Phone", ADMIN_PHONE);
        addRow(grid, 2, "Email", ADMIN_EMAIL);
        addRow(grid, 3, "Available", ADMIN_HOURS);

        Button copyPhone = new Button("Copy Phone");
        copyPhone.getStyleClass().add("secondary-button");
        copyPhone.setOnAction(e -> {
            copy(ADMIN_PHONE);
            info("Copied", "The admin's phone number was copied.");
        });

        Button copyEmail = new Button("Copy Email");
        copyEmail.getStyleClass().add("secondary-button");
        copyEmail.setOnAction(e -> {
            copy(ADMIN_EMAIL);
            info("Copied", "The admin's email address was copied.");
        });

        HBox actions = new HBox(12, copyPhone, copyEmail);

        card.getChildren().addAll(title, intro, grid, actions);
        return card;
    }

    private static void addRow(GridPane grid, int row, String label, String value) {
        Label key = new Label(label);
        key.getStyleClass().add("field-label");

        Label val = new Label(value);
        val.getStyleClass().add("stat-value");
        val.setWrapText(true);

        grid.add(key, 0, row);
        grid.add(val, 1, row);
    }

    // ---------------------------------------------------------
    // 2. Report a Problem
    // ---------------------------------------------------------

    private static VBox reportCard(String username) {
        VBox card = card();

        Label title = new Label("Report a Problem");
        title.getStyleClass().add("section-title");

        Label intro = muted(
                "Something not working? Tell us what happened. "
                        + "Your report is saved with a reference number you can give the admin."
        );

        Label categoryLabel = new Label("What is the problem about?");
        categoryLabel.getStyleClass().add("field-label");

        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll(
                "Login problem",
                "Billing or payment",
                "Reservations",
                "Receipt or printing",
                "Screen or display",
                "Something else"
        );
        category.setPromptText("Choose a category");
        category.setMaxWidth(Double.MAX_VALUE);

        Label detailsLabel = new Label("Describe the problem");
        detailsLabel.getStyleClass().add("field-label");

        TextArea details = new TextArea();
        details.setPromptText(
                "What were you doing, and what happened? "
                        + "Include any error message you saw."
        );
        details.setPrefRowCount(5);
        details.setWrapText(true);

        Button submit = new Button("Submit Report");
        submit.getStyleClass().add("primary-button");

        Button clear = new Button("Clear");
        clear.getStyleClass().add("nav-button");

        submit.setOnAction(e -> {
            String selected = category.getValue();
            String text = details.getText() == null
                    ? ""
                    : details.getText().trim();

            if (selected == null) {
                warn("Please choose what the problem is about.");
                return;
            }

            if (text.length() < 10) {
                warn("Please describe the problem in a little more detail "
                        + "(at least 10 characters).");
                return;
            }

            try {
                String reference = saveReport(username, selected, text);

                info(
                        "Report saved",
                        "Thank you. Your report reference is " + reference
                                + ".\nPlease quote it when you contact the admin.\n\n"
                                + "Saved to: " + REPORT_FILE
                );

                category.setValue(null);
                details.clear();

            } catch (IOException ex) {
                error("The report could not be saved: " + ex.getMessage());
            }
        });

        clear.setOnAction(e -> {
            category.setValue(null);
            details.clear();
        });

        HBox actions = new HBox(12, submit, clear);

        card.getChildren().addAll(
                title,
                intro,
                categoryLabel,
                category,
                detailsLabel,
                details,
                actions
        );

        return card;
    }

    private static String saveReport(
            String username,
            String category,
            String details
    ) throws IOException {

        LocalDateTime now = LocalDateTime.now();

        String reference = "PR-" + now.format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
        );

        String nl = System.lineSeparator();

        String entry =
                "Reference : " + reference + nl
                        + "Date      : " + now.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                ) + nl
                        + "User      : " + username + nl
                        + "Category  : " + category + nl
                        + "Details   : " + details + nl
                        + "------------------------------------------------" + nl;

        Files.createDirectories(REPORT_FILE.getParent());

        Files.writeString(
                REPORT_FILE,
                entry,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        return reference;
    }

    // ---------------------------------------------------------
    // 3. Common Problems
    // ---------------------------------------------------------

    private static VBox commonProblemsCard() {
        VBox card = card();

        Label title = new Label("Common Problems");
        title.getStyleClass().add("section-title");

        Label intro = muted("Tap a question to see the answer.");

        Accordion accordion = new Accordion();

        accordion.getPanes().addAll(
                faq("I can't log in",
                        "Check that your username and password are typed correctly. "
                                + "Both are case-sensitive, so make sure Caps Lock is off. "
                                + "Tick \"Show password\" to see what you typed. "
                                + "If it still fails, contact the admin to reset your details."),

                faq("\"Payment cannot exceed the total bill\"",
                        "The amount paid must be equal to or less than the total. "
                                + "The total is the room charge, minus any discount, plus extras. "
                                + "Press Calculate on the Guest Billing page to see the total, "
                                + "then enter an amount paid that is not higher."),

                faq("I can't find a reservation when billing",
                        "The search checks guest name, phone number, reservation ID and room type. "
                                + "The reservation must be added first under Reservations. "
                                + "If there is none, you can still bill manually for one room."),

                faq("\"For manual billing, use one room per bill\"",
                        "Manual billing supports one room at a time. "
                                + "To bill several rooms together, search for the reservation "
                                + "and select it from the results."),

                faq("How is the discount worked out?",
                        "Bookings of 5 nights or more get 5% off the room charges. "
                                + "Extras are not discounted."),

                faq("The receipt will not print",
                        "Make sure a printer is connected, switched on and set as the "
                                + "default printer in Windows. If the message says no printer "
                                + "is available, Windows has not detected one. "
                                + "You can print later from Saved Bills using View / Print Receipt."),

                faq("I deleted a bill or reservation by mistake",
                        "Deleting cannot be undone from the app. Create the reservation "
                                + "or bill again, and tell the admin so they can check your records."),

                faq("The nights, rooms or dates look wrong",
                        "Nights are counted from the check-in date to the check-out date. "
                                + "Check both dates on the reservation. If a reservation is wrong, "
                                + "delete it and add it again with the correct details."),

                faq("The window looks cut off or too small",
                        "Maximise the window with the square button at the top right. "
                                + "If it still does not fit your screen, report it above "
                                + "and mention your screen size."),

                faq("My problem is not listed here",
                        "Use Report a Problem above, or contact the admin directly "
                                + "using the details at the top of this page.")
        );

        card.getChildren().addAll(title, intro, accordion);
        return card;
    }

    private static TitledPane faq(String question, String answer) {
        Label body = new Label(answer);
        body.setWrapText(true);
        body.getStyleClass().add("muted-text");
        body.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(body);
        content.setPadding(new Insets(4, 4, 4, 4));

        TitledPane pane = new TitledPane(question, content);
        pane.setAnimated(true);
        return pane;
    }

    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------

    private static VBox card() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("glass-card");
        return card;
    }

    private static Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted-text");
        label.setWrapText(true);
        return label;
    }

    private static void copy(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void warn(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Lodge Billing System");
        alert.setHeaderText("Please check your report");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lodge Billing System");
        alert.setHeaderText("Unable to complete the operation");
        alert.setContentText(message);
        alert.showAndWait();
    }
}