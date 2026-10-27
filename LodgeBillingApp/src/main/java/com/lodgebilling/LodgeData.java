
package com.lodgebilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LodgeData {

    // Stores all bills while the application is running.
    public final ObservableList<Bill> bills =
            FXCollections.observableArrayList();

    // Stores all reservations while the application is running.
    public final ObservableList<Reservation> reservations =
            FXCollections.observableArrayList();

    private int billCounter = 1;
    private int reservationCounter = 1;

    // Generates bill numbers: B0001, B0002, B0003...
    public String nextBillNumber() {
        return "B" + String.format("%04d", billCounter++);
    }

    // Generates reservation numbers: R0001, R0002, R0003...
    public String nextReservationNumber() {
        return "R" + String.format("%04d", reservationCounter++);
    }

    // Returns the price for one room per night.
    public double roomPrice(String room) {
        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a room type."
            );
        }

        return switch (room.trim()) {
            case "Standard" -> 350.00;
            case "Deluxe" -> 550.00;
            case "Family" -> 750.00;
            default -> throw new IllegalArgumentException(
                    "Invalid room type. Select Standard, Deluxe, or Family."
            );
        };
    }

    // Calculates the total number of rooms in all reservations.
    public int totalRoomsReserved() {
        return reservations.stream()
                .mapToInt(Reservation::getRooms)
                .sum();
    }

    // Calculates the total payments received.
    public double totalCollected() {
        return bills.stream()
                .mapToDouble(Bill::getPaid)
                .sum();
    }

    // Calculates the total outstanding balance on all bills.
    public double totalOutstanding() {
        return bills.stream()
                .mapToDouble(Bill::getBalance)
                .sum();
    }

    // Returns the number of reservations.
    public int totalReservations() {
        return reservations.size();
    }

    // Returns the number of bills created.
    public int totalBills() {
        return bills.size();
    }
}
