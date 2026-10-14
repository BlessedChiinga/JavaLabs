package com.lodgebilling;

import java.time.format.DateTimeFormatter;

public class Receipt {

    public static String format(Bill bill) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

        return """
                ========================================
                       LODGE BILLING SYSTEM
                            GUEST RECEIPT
                ========================================

                Receipt No:  %s
                Date:        %s

                Guest:       %s
                Room:        %s
                Nights:      %d

                ----------------------------------------
                Room charge:       K %10.2f
                Discount:          K %10.2f
                Extras:            K %10.2f
                ----------------------------------------
                TOTAL:             K %10.2f
                Amount paid:       K %10.2f
                BALANCE:           K %10.2f
                ========================================

                   Thank you for staying with us!
                ========================================
                """.formatted(
                bill.getBillNumber(),
                bill.getDate().format(formatter),
                bill.getGuest(),
                bill.getRoom(),
                bill.getNights(),
                bill.getRoomCharge(),
                bill.getDiscount(),
                bill.getExtras(),
                bill.getTotal(),
                bill.getPaid(),
                bill.getBalance()
        );
    }
}