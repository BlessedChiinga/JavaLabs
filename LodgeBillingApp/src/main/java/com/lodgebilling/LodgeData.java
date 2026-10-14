package com.lodgebilling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LodgeData {

    public final ObservableList<Bill> bills =
            FXCollections.observableArrayList();

    public final ObservableList<Reservation> reservations =
            FXCollections.observableArrayList();

    private int billCounter = 1;
    private int reservationCounter = 1;

    public String nextBillNumber() {
        return "B" + String.format("%04d", billCounter++);
    }

    public String nextReservationNumber() {
        return "R" + String.format("%04d", reservationCounter++);
    }

    public double roomPrice(String room) {
        return switch (room) {
            case "Standard" -> 350.00;
            case "Deluxe" -> 550.00;
            case "Family" -> 750.00;
            default -> throw new IllegalArgumentException(
                    "Select a valid room type.");
        };
    }

    public int totalRoomsReserved() {
        return reservations.stream()
                .mapToInt(Reservation::getRooms)
                .sum();
    }

    public double totalCollected() {
        return bills.stream()
                .mapToDouble(Bill::getPaid)
                .sum();
    }
}