package com.lodgebilling;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.function.Function;

public class DashboardView {

    private final Main main;
    private final LodgeController controller;
    private final LodgeData data;
    private final String username;

    private BorderPane root;
    private VBox content;

    public DashboardView(
            Main main, LodgeController controller, String username) {
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
        content.setPadding(new Insets(24));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");

        root.setCenter(scroll);
        showDashboard();
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("glass-header");

        Label brand = new Label("⌂  LODGE BILLING");
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
                    ButtonType.YES, ButtonType.NO);

            alert.setHeaderText("End your session?");

            alert.showAndWait().ifPresent(answer -> {
                if (answer == ButtonType.YES) {
                    main.showLogin();
                }
            });
        });

        header.getChildren().addAll(
                brand, spacer, dashboard, billing,
                reservations, bills, user, logout);

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

        Label subtitle = new Label(description);
        subtitle.getStyleClass().add("muted-text");

        content.getChildren().addAll(heading, subtitle);
    }

    private VBox statCard(String title, String value, String symbol) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setMinWidth(170);
        card.getStyleClass().add("glass-card");

        Label icon = new Label(symbol);
        icon.getStyleClass().add("stat-icon");

        Label heading = new Label(title);
        heading.getStyleClass().add("muted-text");

        Label number = new Label(value);
        number.getStyleClass().add("stat-value");

        card.getChildren().addAll(icon, heading, number);
        return card;
    }

    private void showDashboard() {
        setPage("Overview",
                "Welcome back. Here is your lodge activity.");

        HBox stats = new HBox(14,
                statCard("Reservations",
                        String.valueOf(data.reservations.size()), "▤"),
                statCard("Rooms reserved",
                        String.valueOf(data.totalRoomsReserved()), "⌂"),
                statCard("Bills generated",
                        String.valueOf(data.bills.size()), "▧"),
                statCard("Money collected",
                        money(data.totalCollected()), "K"));

        for (Node node : stats.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        Label recent = new Label("Recent bills");
        recent.getStyleClass().add("section-title");

        TableView<Bill> table = createBillTable();
        table.setMaxHeight(300);

        content.getChildren().addAll(stats, recent, table);
    }

    private void showBilling() {
        setPage("Guest Billing",
                "Calculate room charges and record guest payments.");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("glass-card");

        TextField guest = new TextField();
        guest.setPromptText("Guest full name");

        ComboBox<String> room = new ComboBox<>();
        room.getItems().addAll("Standard", "Deluxe", "Family");
        room.setValue("Standard");

        TextField nights = new TextField("1");
        TextField extras = new TextField("0");
        TextField paid = new TextField("0");

        addField(form, "Guest name", guest, 0);
        addField(form, "Room type", room, 1);
        addField(form, "Number of nights", nights, 2);
        addField(form, "Extras (K)", extras, 3);
        addField(form, "Amount paid (K)", paid, 4);

        Label prices = new Label(
                "STANDARD: K350/night    DELUXE: K550/night"
                        + "    FAMILY: K750/night\n"
                        + "Bookings of 5+ nights receive 5% off room charges."
                        + " Extras are not discounted.");
        prices.getStyleClass().add("info-panel");

        Button calculate = new Button("Calculate");
        Button save = new Button("Save Bill & Receipt");
        Button clear = new Button("Clear");

        calculate.getStyleClass().add("secondary-button");
        save.getStyleClass().add("primary-button");
        clear.getStyleClass().add("nav-button");

        calculate.setOnAction(e -> {
            try {
                int n = Integer.parseInt(nights.getText().trim());
                double ex = Double.parseDouble(extras.getText().trim());
                double payment = Double.parseDouble(paid.getText().trim());

                if (n <= 0 || !Double.isFinite(ex) || ex < 0
                        || !Double.isFinite(payment) || payment < 0) {
                    throw new IllegalArgumentException(
                            "Enter valid positive nights and non-negative amounts.");
                }

                double charge = data.roomPrice(room.getValue()) * n;
                double discount = n >= 5 ? charge * 0.05 : 0;
                double total = charge - discount + ex;

                if (payment > total) {
                    throw new IllegalArgumentException(
                            "Payment cannot exceed the total bill.");
                }

                showInfo("Bill calculation",
                        String.format(
                                "Room charge: K%.2f\nDiscount: K%.2f\n"
                                        + "Extras: K%.2f\nTotal: K%.2f\n"
                                        + "Paid: K%.2f\nBalance: K%.2f",
                                charge, discount, ex,
                                total, payment, total - payment));
            } catch (Exception ex) {
                showError(ex.getMessage() == null
                        ? "Check your values." : ex.getMessage());
            }
        });

        save.setOnAction(e -> {
            try {
                Bill bill = controller.createBill(
                        guest.getText(),
                        room.getValue(),
                        Integer.parseInt(nights.getText().trim()),
                        Double.parseDouble(extras.getText().trim()),
                        Double.parseDouble(paid.getText().trim()));

                showReceipt(bill);
                showDashboard();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        clear.setOnAction(e -> {
            guest.clear();
            room.setValue("Standard");
            nights.setText("1");
            extras.setText("0");
            paid.setText("0");
        });

        content.getChildren().addAll(
                form, prices, new HBox(12, calculate, save, clear));
    }

    private void addField(
            GridPane grid, String title, Control control, int row) {
        Label label = new Label(title);
        label.getStyleClass().add("field-label");
        control.setMaxWidth(Double.MAX_VALUE);

        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private void showReservations() {
        setPage("Room Reservations",
                "Register guests and track the number of rooms reserved.");

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("glass-card");

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
        Spinner<Integer> guests = new Spinner<>(1, 500, 1);

        TextArea notes = new TextArea();
        notes.setPromptText("Additional notes");
        notes.setPrefRowCount(2);

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
                controller.createReservation(
                        guest.getText(), phone.getText(), room.getValue(),
                        checkIn.getValue(), checkOut.getValue(),
                        rooms.getValue(), guests.getValue(), notes.getText());

                showReservations();
                showInfo("Success", "Reservation added.");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        TableView<Reservation> table = createReservationTable();
        table.setMaxHeight(300);

        Button delete = new Button("Delete Selected Reservation");
        delete.getStyleClass().add("logout-button");

        delete.setOnAction(e -> {
            Reservation selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Select a reservation first.");
                return;
            }

            data.reservations.remove(selected);
            showReservations();
        });

        content.getChildren().addAll(
                form, add, new Label("All reservations"), table, delete);
    }

    private void showBills() {
        setPage("Saved Bills",
                "Select a bill to view or print its receipt.");

        TableView<Bill> table = createBillTable();
        table.setMaxHeight(450);

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

            data.bills.remove(selected);
            showBills();
        });

        content.getChildren().addAll(
                table, new HBox(12, receipt, delete));
    }

    private TableView<Bill> createBillTable() {
        TableView<Bill> table = new TableView<>();
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

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
                TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(
                reservationColumn("ID", Reservation::getId),
                reservationColumn("Guest", Reservation::getGuest),
                reservationColumn("Phone", Reservation::getPhone),
                reservationColumn("Room", Reservation::getRoom),
                reservationColumn("Check-in",
                        r -> r.getCheckIn().toString()),
                reservationColumn("Check-out",
                        r -> r.getCheckOut().toString()),
                reservationColumn("Rooms",
                        r -> String.valueOf(r.getRooms())),
                reservationColumn("Guests",
                        r -> String.valueOf(r.getGuests()))
        );

        table.setItems(data.reservations);
        return table;
    }

    private TableColumn<Bill, String> billColumn(
            String title, Function<Bill, String> getter) {
        TableColumn<Bill, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
                new SimpleStringProperty(getter.apply(cell.getValue())));
        return column;
    }

    private TableColumn<Reservation, String> reservationColumn(
            String title, Function<Reservation, String> getter) {
        TableColumn<Reservation, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell ->
                new SimpleStringProperty(getter.apply(cell.getValue())));
        return column;
    }

    private void showReceipt(Bill bill) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Receipt " + bill.getBillNumber());
        dialog.setHeaderText("Guest payment receipt");

        TextArea text = new TextArea(Receipt.format(bill));
        text.setEditable(false);
        text.setWrapText(false);
        text.setPrefColumnCount(42);
        text.setPrefRowCount(23);
        text.getStyleClass().add("receipt-paper");

        ButtonType printType = new ButtonType(
                "Print Receipt", ButtonBar.ButtonData.OTHER);

        dialog.getDialogPane().getButtonTypes().addAll(
                printType, ButtonType.CLOSE);
        dialog.getDialogPane().setContent(text);

        Node printButton = dialog.getDialogPane().lookupButton(printType);
        printButton.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
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

        if (job.showPrintDialog(root.getScene().getWindow())) {
            if (job.printPage(text)) {
                job.endJob();
            } else {
                job.cancelJob();
                showError("Printing failed.");
            }
        }
    }

    private String money(double value) {
        return String.format("K %,.2f", value);
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
                message == null ? "An error occurred." : message);
        alert.showAndWait();
    }
}