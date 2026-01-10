package ma.emsi.sadik.billingservice.events;

import ma.emsi.sadik.billingservice.entities.Bill;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BillEventProducer {

    private final StreamBridge streamBridge;

    public BillEventProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishBillCreatedEvent(Bill bill) {
        // Calculate total amount from product items
        double totalAmount = bill.getProductItems() != null
                ? bill.getProductItems().stream()
                        .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                        .sum()
                : 0.0;

        int itemCount = bill.getProductItems() != null
                ? bill.getProductItems().size()
                : 0;

        BillEvent event = new BillEvent(
                bill.getId(),
                bill.getCustomerId(),
                totalAmount,
                bill.getBillingDate(),
                itemCount);

        boolean sent = streamBridge.send("BILLING_EVENTS", event);
        if (sent) {
            System.out.println(">>> Bill event published: Bill ID = " + bill.getId());
        } else {
            System.out.println(">>> Failed to publish bill event for Bill ID = " + bill.getId());
        }
    }
}
