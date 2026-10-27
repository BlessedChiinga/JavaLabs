
package com.lodgebilling;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LodgeController {

    private final LodgeData data = new LodgeData();

    public LodgeData getData() {
        return data;
    }

    /**
     * Creates a bill for a guest entered manually or selected
     * from the reservation search.
     */
    public Bill createBill(
            String guest,
            String room,
            int nights,
            double extras,
            double paid
    ) {
        validateGuest(guest);
        validateNights(nights);
        validateAmount("Extras", extras);
        validateAmount("Payment", paid);

        double roomCharge = data.roomPrice(room) * nights;
        double discount = nights >= 5 ? roomCharge * 0.05 : 0;
        double total = roomCharge - discount + extras;

        validatePayment(paid, total);

        Bill bill = new Bill(
                data.nextBillNumber(),
                guest.trim(),
                room,
                nights,
                roomCharge,
                discount,
                extras,
                total,
                paid
        );

        data.bills.add(bill);
        return bill;
    }

    /**
     * Creates a bill using an existing reservation.
     * The stay length is calculated from check-in and check-out.
     *
     * This overload is useful when the billing search finds
     * an existing reservation.
     */
    public Bill createBillFromReservation(
            Reservation reservation,
            double extras,
            double paid
    ) {
        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Select a reservation before creating a bill."
            );
        }

        validateAmount("Extras", extras);
        validateAmount("Payment", paid);

        // Prevent a second bill for the same guest and stay.
        for (Bill existing : data.bills) {
            boolean sameGuest = existing.getGuest().equalsIgnoreCase(
                    reservation.getGuest()
            );

            boolean sameRoom = existing.getRoom().equalsIgnoreCase(
                    reservation.getRoom()
            );

            if (sameGuest && sameRoom) {
                throw new IllegalArgumentException(
                        "A bill already exists for this guest and room. "
                                + "Check Saved Bills before creating another."
                );
            }
        }

        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        if (checkIn == null || checkOut == null
                || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "The reservation has invalid check-in or check-out dates."
            );
        }

        long nightsLong = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (nightsLong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "The reservation stay is too long."
            );
        }

        int nights = (int) nightsLong;

        double roomCharge = data.roomPrice(reservation.getRoom()) * nights;
        double discount = nights >= 5 ? roomCharge * 0.05 : 0;
        double total = roomCharge - discount + extras;

        validatePayment(paid, total);

        Bill bill = new Bill(
                data.nextBillNumber(),
                reservation.getGuest().trim(),
                reservation.getRoom(),
                nights,
                roomCharge,
                discount,
                extras,
                total,
                paid
        );

        data.bills.add(bill);
        return bill;
    }

    public Reservation createReservation(
            String guest,
            String phone,
            String room,
            LocalDate checkIn,
            LocalDate checkOut,
            int rooms,
            int guests,
            String notes
    ) {
        validateGuest(guest);

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the phone number."
            );
        }

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Choose both reservation dates."
            );
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out must be after check-in."
            );
        }

        if (rooms < 1 || guests < 1) {
            throw new IllegalArgumentException(
                    "Rooms and guests must be at least one."
            );
        }

        // Validates that this room type is supported.
        data.roomPrice(room);

        Reservation reservation = new Reservation(
                data.nextReservationNumber(),
                guest.trim(),
                phone.trim(),
                room,
                checkIn,
                checkOut,
                rooms,
                guests,
                notes == null ? "" : notes.trim()
        );

        data.reservations.add(reservation);
        return reservation;
    }

    private void validateGuest(String guest) {
        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the guest name."
            );
        }
    }

    private void validateNights(int nights) {
        if (nights <= 0) {
            throw new IllegalArgumentException(
                    "Nights must be greater than zero."
            );
        }
    }

    private void validateAmount(String name, double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                    name + " must be a valid, non-negative amount."
            );
        }
    }

    private void validatePayment(double paid, double total) {
        if (paid > total) {
            throw new IllegalArgumentException(
                    "Payment cannot exceed the total bill."
            );
        }
    }
}
