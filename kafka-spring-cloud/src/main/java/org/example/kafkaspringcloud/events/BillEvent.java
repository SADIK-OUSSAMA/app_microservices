package org.example.kafkaspringcloud.events;

import java.util.Date;

public record BillEvent(
        Long billId,
        Long customerId,
        Double totalAmount,
        Date billingDate,
        int itemCount) {
}
