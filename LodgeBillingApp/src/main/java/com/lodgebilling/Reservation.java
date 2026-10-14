package com.lodgebilling;

import java.time.LocalDate;

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

    public Reservation(String id, String guest, String phone,
                       String room, LocalDate checkIn,
                       LocalDate checkOut, int rooms,
                       int guests, String notes) {
        this.id = id;
        this.guest = guest;
        this.phone = phone;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rooms = rooms;
        this.guests = guests;
        this.notes = notes;
    }

    public String getId() { return id; }
    public String getGuest() { return guest; }
    public String getPhone() { return phone; }
    public String getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getRooms() { return rooms; }
    public int getGuests() { return guests; }
    public String getNotes() { return notes; }
}