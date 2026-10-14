package com.lodgebilling;

import java.time.LocalDate;

public class LodgeController {

    private final LodgeData data = new LodgeData();

    public LodgeData getData() {
        return data;
    }

    public Bill createBill(
            String guest, String room, int nights,
            double extras, double paid) {

        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the guest name.");
        }

        if (nights <= 0) {
            throw new IllegalArgumentException(
                    "Nights must be greater than zero.");
        }

        if (!Double.isFinite(extras) || !Double.isFinite(paid)
                || extras < 0 || paid < 0) {
            throw new IllegalArgumentException(
                    "Extras and payment must be valid non-negative amounts.");
        }

        double roomCharge = data.roomPrice(room) * nights;
        double discount = nights >= 5 ? roomCharge * 0.05 : 0;
        double total = roomCharge - discount + extras;

        if (paid > total) {
            throw new IllegalArgumentException(
                    "Payment cannot exceed the total bill.");
        }

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

    public Reservation createReservation(
            String guest, String phone, String room,
            LocalDate checkIn, LocalDate checkOut,
            int rooms, int guests, String notes) {

        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the guest name.");
        }

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Enter the phone number.");
        }

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Choose both reservation dates.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out must be after check-in.");
        }

        if (rooms < 1 || guests < 1) {
            throw new IllegalArgumentException(
                    "Rooms and guests must be at least one.");
        }

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
                notes == null ? "" : notes
        );

        data.reservations.add(reservation);
        return reservation;
    }
}