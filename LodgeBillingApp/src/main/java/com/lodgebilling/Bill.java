package com.lodgebilling;

import java.time.LocalDateTime;

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

    public Bill(String billNumber, String guest, String room,
                int nights, double roomCharge, double discount,
                double extras, double total, double paid) {
        this.billNumber = billNumber;
        this.guest = guest;
        this.room = room;
        this.nights = nights;
        this.roomCharge = roomCharge;
        this.discount = discount;
        this.extras = extras;
        this.total = total;
        this.paid = paid;
        this.date = LocalDateTime.now();
    }

    public String getBillNumber() { return billNumber; }
    public String getGuest() { return guest; }
    public String getRoom() { return room; }
    public int getNights() { return nights; }
    public double getRoomCharge() { return roomCharge; }
    public double getDiscount() { return discount; }
    public double getExtras() { return extras; }
    public double getTotal() { return total; }
    public double getPaid() { return paid; }
    public LocalDateTime getDate() { return date; }

    public double getBalance() {
        return total - paid;
    }
}