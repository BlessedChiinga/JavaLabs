
package com.lodgebilling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bill {

    private final String billNumber;
    private final String guest;
    private final String room;
    private final int nights;
    private final double roomCharge;
    private final double discount;
    private final double extras;
    private final double total;
    private final double paid;
    private final LocalDateTime date;

    public Bill(
            String billNumber,
            String guest,
            String room,
            int nights,
            double roomCharge,
            double discount,
            double extras,
            double total,
            double paid
    ) {
        if (billNumber == null || billNumber.isBlank()) {
            throw new IllegalArgumentException("Bill number cannot be empty.");
        }

        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException("Guest name cannot be empty.");
        }

        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException("Room cannot be empty.");
        }

        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }

        validateAmount("Room charge", roomCharge);
        validateAmount("Discount", discount);
        validateAmount("Extras", extras);
        validateAmount("Total", total);
        validateAmount("Paid amount", paid);

        if (discount > roomCharge + extras) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed the room charge and extras."
            );
        }

        if (paid > total) {
            throw new IllegalArgumentException(
                    "Paid amount cannot exceed the bill total."
            );
        }

        this.billNumber = billNumber.trim();
        this.guest = guest.trim();
        this.room = room.trim();
        this.nights = nights;
        this.roomCharge = roomCharge;
        this.discount = discount;
        this.extras = extras;
        this.total = total;
        this.paid = paid;
        this.date = LocalDateTime.now();
    }

    private static void validateAmount(String name, double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                    name + " must be a valid, non-negative amount."
            );
        }
    }

    public String getBillNumber() {
        return billNumber;
    }

    public String getGuest() {
        return guest;
    }

    public String getRoom() {
        return room;
    }

    public int getNights() {
        return nights;
    }

    public double getRoomCharge() {
        return roomCharge;
    }

    public double getDiscount() {
        return discount;
    }

    public double getExtras() {
        return extras;
    }

    public double getTotal() {
        return total;
    }

    public double getPaid() {
        return paid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getBalance() {
        return Math.max(0.0, total - paid);
    }

    public String getPaymentStatus() {
        if (paid == 0) {
            return "UNPAID";
        }

        if (paid < total) {
            return "PARTIALLY PAID";
        }

        return "PAID";
    }

    public String getFormattedDate() {
        return date.format(
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        );
    }
}
