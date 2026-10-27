
package com.lodgebilling;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Function;

public class DashboardView {

    private final Main main;
    private final LodgeController controller;
    private final LodgeData data;
    private final String username;

    private BorderPane root;
    private VBox content;

    public DashboardView(
            Main main,
            LodgeController controller,
            String username
    ) {
        this.main = main;
        this.controller = controller;
        this.data = controller.getData();
        this.username = username;

        build();
    }

    public Parent getView() {
        return root;
    }

    private void build() {
        root = new BorderPane();
        root.getStyleClass().add("photo-background");
        root.setTop(createHeader());

        content = new VBox(20);
        content.setPadding(new Insets(26));
        content.setAlignment(Pos.TOP_CENTER);
        content.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.getStyleClass().add("transparent-scroll");

        root.setCenter(scroll);

        showDashboard();
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(16, 22, 16, 22));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("glass-header");

        Label brand = new Label("⌂  LUXURY LODGE");
        brand.getStyleClass().add("brand-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashboard = navButton("Dashboard");
        Button billing = navButton("Billing");
        Button reservations = navButton("Reservations");
        Button bills = navButton("Saved Bills");

        Label user = new Label("●  " + username);
        user.getStyleClass().add("user-chip");

        Button logout = new Button("Logout");
        logout.getStyleClass().add("logout-button");

        dashboard.setOnAction(e -> showDashboard());
        billing.setOnAction(e -> showBilling());
        reservations.setOnAction(e -> showReservations());
        bills.setOnAction(e -> showBills());

        logout.setOnAction(e -> {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Do you want to log out?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            alert.setTitle("Logout");
            alert.setHeaderText("End your session?");

            alert.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    main.showLogin();
                }
            });
        });

        header.getChildren().addAll(
                brand, spacer, dashboard, billing,
                reservations, bills, user, logout
        );

        return header;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        return button;
    }

    private void setPage(String title, String description) {
        content.getChildren().clear();

        Label heading = new Label(title);
        heading.getStyleClass().add("page-title");
        heading.setWrapText(true);

        Label subtitle = new Label(description);
        subtitle.getStyleClass().add("muted-text");
        subtitle.setWrapText(true);

        VBox pageHeading = new VBox(6, heading, subtitle);
        pageHeading.setMaxWidth(1250);
        pageHeading.setFillWidth(true);

        content.getChildren().add(pageHeading);
    }

    private VBox statCard(String title, String value, String symbol) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("glass-card");

        Label icon = new Label(symbol);
        icon.getStyleClass().add("stat-icon");

        Label heading = new Label(title);
        heading.getStyleClass().add("muted-text");
        heading.setWrapText(true);

        Label number = new Label(value);
        number.getStyleClass().add("stat-value");
        number.setWrapText(true);

        card.getChildren().addAll(icon, heading, number);
        return card;
    }

    private void showDashboard() {
        setPage(
                "Overview",
                "Welcome back. Here is your lodge activity."
        );

        GridPane stats = new GridPane();
        stats.setHgap(14);
        stats.setVgap(14);
        stats.setMaxWidth(1250);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(25);
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            stats.getColumnConstraints().add(column);
        }

        stats.add(statCard(
                "Reservations",
                String.valueOf(data.reservations.size()),
                "▤"
        ), 0, 0);

        stats.add(statCard(
                "Rooms reserved",
                String.valueOf(data.totalRoomsReserved()),
                "⌂"
        ), 1, 0);

        stats.add(statCard(
                "Bills generated",
                String.valueOf(data.bills.size()),
                "▧"
        ), 2, 0);

        stats.add(statCard(
                "Money collected",
                money(data.totalCollected()),
                "K"
        ), 3, 0);

        Label recent = new Label("Recent Bills");
        recent.getStyleClass().add("section-title");

        TableView<Bill> table = createBillTable();
        table.setPrefHeight(320);
        table.setMaxWidth(1250);

        content.getChildren().addAll(stats, recent, table);
    }

    private void showBilling() {
        setPage(
                "Guest Billing",
                "Find a reservation, calculate the charges, and record a payment."
        );

        VBox panel = new VBox(16);
        panel.setPadding(new Insets(24));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMaxWidth(1000);
        panel.getStyleClass().add("glass-card");

        Label searchTitle = new Label("Find a Reservation");
        searchTitle.getStyleClass().add("section-title");

        TextField search = new TextField();
        search.setPromptText(
                "Search guest name, phone, reservation ID, or room..."
        );
        search.setMaxWidth(Double.MAX_VALUE);

        ListView<Reservation> matches = new ListView<>();
        matches.setPrefHeight(145);
        matches.setPlaceholder(
                new Label("No matching reservations.")
        );
        matches.setVisible(false);
        matches.setManaged(false);

        FilteredList<Reservation> filtered = new FilteredList<>(
                data.reservations,
                reservation -> false
        );
        matches.setItems(filtered);

        TextField guest = new TextField();
        guest.setPromptText("Guest full name");

        ComboBox<String> room = new ComboBox<>();
        room.getItems().addAll("Standard", "Deluxe", "Family");
        room.setValue("Standard");
        room.setMaxWidth(Double.MAX_VALUE);

        TextField nights = new TextField("1");
        TextField roomsCount = new TextField("1");
        TextField extras = new TextField("0");
        TextField paid = new TextField("0");

        Label selectedReservationLabel = new Label(
                "No reservation selected. Manual billing is also available."
        );
        selectedReservationLabel.getStyleClass().add("muted-text");
        selectedReservationLabel.setWrapText(true);

        // Keeps track of the reservation selected by the user.
        final Reservation[] selectedReservation = {null};

        search.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    String query = newValue == null
                            ? ""
                            : newValue.trim().toLowerCase(Locale.ROOT);

                    // Typing a new search clears the previous selection.
                    selectedReservation[0] = null;
                    selectedReservationLabel.setText(
                            "No reservation selected. Manual billing is also available."
                    );

                    filtered.setPredicate(reservation -> {
                        if (query.isEmpty()) {
                            return false;
                        }

                        return contains(reservation.getGuest(), query)
                                || contains(reservation.getPhone(), query)
                                || contains(reservation.getId(), query)
                                || contains(reservation.getRoom(), query);
                    });

                    boolean showMatches =
                            !query.isEmpty() && !filtered.isEmpty();

                    matches.setVisible(showMatches);
                    matches.setManaged(showMatches);
                }
        );

        matches.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(
                    Reservation reservation,
                    boolean empty
            ) {
                super.updateItem(reservation, empty);

                if (empty || reservation == null) {
                    setText(null);
                } else {
                    setText(
                            reservation.getId()
                                    + " | " + reservation.getGuest()
                                    + " | " + reservation.getRoom()
                                    + " | " + reservation.getPhone()
                    );
                }
            }
        });

        matches.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldReservation, reservation) -> {
                    if (reservation == null) {
                        return;
                    }

                    selectedReservation[0] = reservation;

                    guest.setText(reservation.getGuest());
                    room.setValue(reservation.getRoom());

                    long stayNights = ChronoUnit.DAYS.between(
                            reservation.getCheckIn(),
                            reservation.getCheckOut()
                    );

                    nights.setText(String.valueOf(stayNights));
                    roomsCount.setText(
                            String.valueOf(reservation.getRooms())
                    );

                    selectedReservationLabel.setText(
                            "Selected: " + reservation.getId()
                                    + " — " + reservation.getGuest()
                                    + " — " + stayNights + " night(s)"
                                    + " — " + reservation.getRooms() + " room(s)"
                    );

                    matches.setVisible(false);
                    matches.setManaged(false);
                }
        );

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(150);

        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHgrow(Priority.ALWAYS);
        inputColumn.setFillWidth(true);

        form.getColumnConstraints().addAll(
                labelColumn,
                inputColumn
        );

        addField(form, "Guest name", guest, 0);
        addField(form, "Room type", room, 1);
        addField(form, "Number of nights", nights, 2);
        addField(form, "Number of rooms", roomsCount, 3);
        addField(form, "Extras (K)", extras, 4);
        addField(form, "Amount paid (K)", paid, 5);

        Label prices = new Label(
                "STANDARD: K350/night     DELUXE: K550/night     FAMILY: K750/night\n"
                        + "Bookings of 5+ nights receive a 5% discount on room charges."
        );
        prices.getStyleClass().add("info-panel");
        prices.setWrapText(true);

        Label calculationResult = new Label("Total: K 0.00");
        calculationResult.getStyleClass().add("section-title");
        calculationResult.setWrapText(true);

        Button calculate = new Button("Calculate");
        Button save = new Button("Save Bill & Receipt");
        Button clear = new Button("Clear");

        calculate.getStyleClass().add("secondary-button");
        save.getStyleClass().add("primary-button");
        clear.getStyleClass().add("nav-button");

        calculate.setOnAction(e -> {
            try {
                BillingAmounts amounts = calculateAmounts(
                        room.getValue(),
                        parsePositiveInt(nights.getText(), "Nights"),
                        parsePositiveInt(roomsCount.getText(), "Number of rooms"),
                        parseAmount(extras.getText(), "Extras"),
                        parseAmount(paid.getText(), "Amount paid")
                );

                validateGuest(guest.getText());

                calculationResult.setText(
                        "Room charge: " + money(amounts.roomCharge())
                                + "    Discount: " + money(amounts.discount())
                                + "\nExtras: " + money(amounts.extras())
                                + "    Total: " + money(amounts.total())
                                + "\nPaid: " + money(amounts.paid())
                                + "    Balance: " + money(
                                amounts.total() - amounts.paid()
                        )
                );
            } catch (IllegalArgumentException ex) {
                showError(messageOf(ex));
            }
        });

        save.setOnAction(e -> {
            try {
                validateGuest(guest.getText());

                int nightCount = parsePositiveInt(
                        nights.getText(), "Nights"
                );
                int roomCount = parsePositiveInt(
                        roomsCount.getText(), "Number of rooms"
                );
                double extraAmount = parseAmount(
                        extras.getText(), "Extras"
                );
                double paidAmount = parseAmount(
                        paid.getText(), "Amount paid"
                );

                BillingAmounts amounts = calculateAmounts(
                        room.getValue(),
                        nightCount,
                        roomCount,
                        extraAmount,
                        paidAmount
                );

                if (paidAmount > amounts.total()) {
                    throw new IllegalArgumentException(
                            "Payment cannot exceed the total bill."
                    );
                }

                Reservation reservation = selectedReservation[0];
                Bill bill;

                if (reservation != null) {
                    // Uses the reservation-aware controller method.
                    bill = controller.createBillFromReservation(
                            reservation,
                            extraAmount,
                            paidAmount
                    );
                } else {
                    if (roomCount != 1) {
                        throw new IllegalArgumentException(
                                "For manual billing, use one room per bill. "
                                        + "Select a reservation to bill multiple rooms."
                        );
                    }

                    bill = controller.createBill(
                            guest.getText().trim(),
                            room.getValue(),
                            nightCount,
                            extraAmount,
                            paidAmount
                    );
                }

                showReceipt(bill);
                showDashboard();

            } catch (IllegalArgumentException ex) {
                showError(messageOf(ex));
            }
        });

        clear.setOnAction(e -> {
            search.clear();
            guest.clear();
            room.setValue("Standard");
            nights.setText("1");
            roomsCount.setText("1");
            extras.setText("0");
            paid.setText("0");

            selectedReservation[0] = null;

            calculationResult.setText("Total: K 0.00");
            selectedReservationLabel.setText(
                    "No reservation selected. Manual billing is also available."
            );

            matches.getSelectionModel().clearSelection();
            matches.setVisible(false);
            matches.setManaged(false);
        });

        HBox actions = new HBox(12, calculate, save, clear);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(8, 0, 0, 0));

        panel.getChildren().addAll(
                searchTitle,
                search,
                matches,
                selectedReservationLabel,
                new Separator(),
                form,
                prices,
                calculationResult,
                actions
        );

        content.getChildren().add(panel);
    }

    private record BillingAmounts(
            double roomCharge,
            double discount,
            double extras,
            double total,
            double paid
    ) {}

    private BillingAmounts calculateAmounts(
            String room,
            int nights,
            int roomCount,
            double extras,
            double paid
    ) {
        if (nights <= 0) {
            throw new IllegalArgumentException(
                    "Nights must be greater than zero."
            );
        }

        if (roomCount <= 0) {
            throw new IllegalArgumentException(
                    "Number of rooms must be greater than zero."
            );
        }

        if (!Double.isFinite(extras) || extras < 0) {
            throw new IllegalArgumentException(
                    "Extras must be a valid non-negative amount."
            );
        }

        if (!Double.isFinite(paid) || paid < 0) {
            throw new IllegalArgumentException(
                    "Amount paid must be a valid non-negative amount."
            );
        }

        double roomCharge = data.roomPrice(room) * nights * roomCount;
        double discount = nights >= 5 ? roomCharge * 0.05 : 0;
        double total = roomCharge - discount + extras;

        if (paid > total) {
            throw new IllegalArgumentException(
                    "Payment cannot exceed the total bill."
            );
        }

        return new BillingAmounts(
                roomCharge, discount, extras, total, paid
        );
    }

    private void validateGuest(String guest) {
        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the guest's name."
            );
        }
    }

    private int parsePositiveInt(String text, String fieldName) {
        try {
            int value = Integer.parseInt(text.trim());

            if (value <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than zero."
                );
            }

            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Enter a valid whole number for " + fieldName + "."
            );
        }
    }

    private double parseAmount(String text, String fieldName) {
        try {
            double value = Double.parseDouble(text.trim());

            if (!Double.isFinite(value) || value < 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be a valid non-negative amount."
                );
            }

            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Enter a valid amount for " + fieldName + "."
            );
        }
    }

    private boolean contains(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void showReservations() {
        setPage(
                "Room Reservations",
                "Register guests and manage their room bookings."
        );

        VBox panel = new VBox(16);
        panel.setPadding(new Insets(22));
        panel.setMaxWidth(1100);
        panel.getStyleClass().add("glass-card");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(12);

        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(150);

        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);

        form.getColumnConstraints().addAll(labels, fields);

        TextField guest = new TextField();
        guest.setPromptText("Guest full name");

        TextField phone = new TextField();
        phone.setPromptText("Phone number");

        ComboBox<String> room = new ComboBox<>();
        room.getItems().addAll("Standard", "Deluxe", "Family");
        room.setValue("Standard");

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        Spinner<Integer> rooms = new Spinner<>(1, 100, 1);
        rooms.setEditable(true);

        Spinner<Integer> guests = new Spinner<>(1, 500, 1);
        guests.setEditable(true);

        TextArea notes = new TextArea();
        notes.setPromptText("Additional notes");
        notes.setPrefRowCount(2);
        notes.setWrapText(true);

        addField(form, "Guest name", guest, 0);
        addField(form, "Phone", phone, 1);
        addField(form, "Room type", room, 2);
        addField(form, "Check-in", checkIn, 3);
        addField(form, "Check-out", checkOut, 4);
        addField(form, "Rooms reserved", rooms, 5);
        addField(form, "Number of guests", guests, 6);
        addField(form, "Notes", notes, 7);

        Button add = new Button("Add Reservation");
        add.getStyleClass().add("primary-button");

        add.setOnAction(e -> {
            try {
                // Commit edited spinner values before reading them.
                rooms.commitValue();
                guests.commitValue();

                controller.createReservation(
                        guest.getText(),
                        phone.getText(),
                        room.getValue(),
                        checkIn.getValue(),
                        checkOut.getValue(),
                        rooms.getValue(),
                        guests.getValue(),
                        notes.getText()
                );

                showReservations();
                showInfo("Success", "Reservation added successfully.");

            } catch (Exception ex) {
                showError(messageOf(ex));
            }
        });

        TableView<Reservation> table = createReservationTable();
        table.setPrefHeight(320);
        table.setMaxWidth(Double.MAX_VALUE);

        Button delete = new Button("Delete Selected Reservation");
        delete.getStyleClass().add("logout-button");

        delete.setOnAction(e -> {
            Reservation selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Select a reservation first.");
                return;
            }

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete reservation for " + selected.getGuest() + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            confirm.setTitle("Delete Reservation");
            confirm.setHeaderText("Confirm deletion");

            confirm.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    data.reservations.remove(selected);
                    showReservations();
                }
            });
        });

        Label tableTitle = new Label("All Reservations");
        tableTitle.getStyleClass().add("section-title");

        panel.getChildren().addAll(
                form,
                add,
                new Separator(),
                tableTitle,
                table,
                delete
        );

        content.getChildren().add(panel);
    }

    private void showBills() {
        setPage(
                "Saved Bills",
                "View, print, and manage guest bills."
        );

        VBox panel = new VBox(16);
        panel.setPadding(new Insets(22));
        panel.setMaxWidth(1200);
        panel.getStyleClass().add("glass-card");

        TableView<Bill> table = createBillTable();
        table.setPrefHeight(420);
        table.setMaxWidth(Double.MAX_VALUE);

        Button receipt = new Button("View / Print Receipt");
        receipt.getStyleClass().add("primary-button");

        Button delete = new Button("Delete Selected Bill");
        delete.getStyleClass().add("logout-button");

        receipt.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Select a bill first.");
            } else {
                showReceipt(selected);
            }
        });

        delete.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Select a bill first.");
                return;
            }

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete bill " + selected.getBillNumber() + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            confirm.setTitle("Delete Bill");
            confirm.setHeaderText("Confirm deletion");

            confirm.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    data.bills.remove(selected);
                    showBills();
                }
            });
        });

        HBox actions = new HBox(12, receipt, delete);
        actions.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(table, actions);
        content.getChildren().add(panel);
    }

    private TableView<Bill> createBillTable() {
        TableView<Bill> table = new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.getColumns().addAll(
                billColumn("Bill No.", Bill::getBillNumber),
                billColumn("Guest", Bill::getGuest),
                billColumn("Room", Bill::getRoom),
                billColumn("Nights", b -> String.valueOf(b.getNights())),
                billColumn("Total", b -> money(b.getTotal())),
                billColumn("Paid", b -> money(b.getPaid())),
                billColumn("Balance", b -> money(b.getBalance()))
        );

        table.setItems(data.bills);
        return table;
    }

    private TableView<Reservation> createReservationTable() {
        TableView<Reservation> table = new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.getColumns().addAll(
                reservationColumn("ID", Reservation::getId),
                reservationColumn("Guest", Reservation::getGuest),
                reservationColumn("Phone", Reservation::getPhone),
                reservationColumn("Room", Reservation::getRoom),
                reservationColumn(
                        "Check-in",
                        r -> r.getCheckIn().toString()
                ),
                reservationColumn(
                        "Check-out",
                        r -> r.getCheckOut().toString()
                ),
                reservationColumn(
                        "Rooms",
                        r -> String.valueOf(r.getRooms())
                ),
                reservationColumn(
                        "Guests",
                        r -> String.valueOf(r.getGuests())
                )
        );

        table.setItems(data.reservations);
        return table;
    }

    private TableColumn<Bill, String> billColumn(
            String title,
            Function<Bill, String> getter
    ) {
        TableColumn<Bill, String> column = new TableColumn<>(title);

        column.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        getter.apply(cell.getValue())
                )
        );

        return column;
    }

    private TableColumn<Reservation, String> reservationColumn(
            String title,
            Function<Reservation, String> getter
    ) {
        TableColumn<Reservation, String> column =
                new TableColumn<>(title);

        column.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        getter.apply(cell.getValue())
                )
        );

        return column;
    }

    private void addField(
            GridPane grid,
            String title,
            Control control,
            int row
    ) {
        Label label = new Label(title);
        label.getStyleClass().add("field-label");

        control.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(control, Priority.ALWAYS);

        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private void showReceipt(Bill bill) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Receipt " + bill.getBillNumber());
        dialog.setHeaderText("Guest Payment Receipt");

        TextArea text = new TextArea(Receipt.format(bill));
        text.setEditable(false);
        text.setWrapText(false);
        text.setPrefColumnCount(52);
        text.setPrefRowCount(24);
        text.getStyleClass().add("receipt-paper");

        ButtonType printType = new ButtonType(
                "Print Receipt",
                ButtonBar.ButtonData.OTHER
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                printType,
                ButtonType.CLOSE
        );

        dialog.getDialogPane().setContent(text);

        Node printButton =
                dialog.getDialogPane().lookupButton(printType);

        printButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            printReceipt(text);
        });

        dialog.showAndWait();
    }

    private void printReceipt(TextArea text) {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            showError("No printer is available.");
            return;
        }

        if (root.getScene() == null) {
            showError("The application window is not ready.");
            job.cancelJob();
            return;
        }

        if (job.showPrintDialog(root.getScene().getWindow())) {
            if (job.printPage(text)) {
                job.endJob();
            } else {
                job.cancelJob();
                showError("Printing failed.");
            }
        } else {
            job.cancelJob();
        }
    }

    private String money(double value) {
        return String.format(Locale.ROOT, "K %,.2f", value);
    }

    private String messageOf(Exception exception) {
        return exception.getMessage() == null
                ? "Please check the entered values."
                : exception.getMessage();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lodge Billing System");
        alert.setHeaderText("Unable to complete the operation");
        alert.setContentText(
                message == null ? "An error occurred." : message
        );
        alert.showAndWait();
    }
}
