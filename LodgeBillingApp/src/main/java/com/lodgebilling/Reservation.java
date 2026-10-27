
package com.lodgebilling;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {

    private final String id;
    private final String guest;
    private final String phone;
    private final String room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int rooms;
    private final int guests;
    private final String notes;

    public Reservation(
            String id,
            String guest,
            String phone,
            String room,
            LocalDate checkIn,
            LocalDate checkOut,
            int rooms,
            int guests,
            String notes
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Reservation ID cannot be empty."
            );
        }

        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException(
                    "Guest name cannot be empty."
            );
        }

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException(
                    "Room type cannot be empty."
            );
        }

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required."
            );
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out must be after check-in."
            );
        }

        if (rooms < 1) {
            throw new IllegalArgumentException(
                    "At least one room must be reserved."
            );
        }

        if (guests < 1) {
            throw new IllegalArgumentException(
                    "At least one guest is required."
            );
        }

        // Validate the room type using the existing room price list.
        new LodgeData().roomPrice(room.trim());

        this.id = id.trim();
        this.guest = guest.trim();
        this.phone = phone.trim();
        this.room = room.trim();
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rooms = rooms;
        this.guests = guests;
        this.notes = notes == null ? "" : notes.trim();
    }

    public String getId() {
        return id;
    }

    public String getGuest() {
        return guest;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getRooms() {
        return rooms;
    }

    public int getGuests() {
        return guests;
    }

    public String getNotes() {
        return notes;
    }

    // Calculates the number of nights between check-in and check-out.
    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    // Calculates the total room cost for the reservation.
    public double getRoomCost() {
        return new LodgeData().roomPrice(room) * getNights() * rooms;
    }

    // Checks whether the reservation covers the given date.
    public boolean includesDate(LocalDate date) {
        return date != null
                && !date.isBefore(checkIn)
                && date.isBefore(checkOut);
    }

    @Override
    public String toString() {
        return id + " - " + guest + " (" + room + ")";
    }
}
