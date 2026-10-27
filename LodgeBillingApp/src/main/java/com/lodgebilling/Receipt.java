
package com.lodgebilling;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Receipt {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy  |  HH:mm");

    private Receipt() {
        // Utility class: no object is required.
    }

    public static String format(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException(
                    "Cannot create a receipt without a bill."
            );
        }

        String line = "================================================";
        String divider = "------------------------------------------------";

        return """
                %s
                           L U X U R Y   L O D G E
                           GUEST PAYMENT RECEIPT
                %s

                Receipt Number : %s
                Date           : %s

                GUEST DETAILS
                %s
                Guest Name     : %s
                Room Type      : %s
                Nights Stayed  : %d

                PAYMENT SUMMARY
                %s
                Room Charge    : %s
                Discount       : %s
                Extra Charges  : %s
                %s
                TOTAL BILL     : %s
                Amount Paid    : %s
                BALANCE DUE    : %s

                PAYMENT STATUS : %s

                %s
                    Thank you for choosing our lodge.
                    We hope to welcome you again!
                %s
                """.formatted(
                line,
                line,
                bill.getBillNumber(),
                bill.getDate().format(DATE_FORMAT),
                divider,
                bill.getGuest(),
                bill.getRoom(),
                bill.getNights(),
                divider,
                money(bill.getRoomCharge()),
                money(bill.getDiscount()),
                money(bill.getExtras()),
                divider,
                money(bill.getTotal()),
                money(bill.getPaid()),
                money(bill.getBalance()),
                bill.getPaymentStatus(),
                line,
                line
        );
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "K %,.2f", amount);
    }
}
