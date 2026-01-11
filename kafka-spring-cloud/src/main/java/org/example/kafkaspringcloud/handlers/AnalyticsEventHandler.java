package org.example.kafkaspringcloud.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafkaspringcloud.entities.AnalyticsRecord;
import org.example.kafkaspringcloud.events.BillEvent;
import org.example.kafkaspringcloud.events.SupplierEvent;
import org.example.kafkaspringcloud.repositories.AnalyticsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventHandler {

    private final AnalyticsRepository analyticsRepository;

    @Bean
    public Consumer<BillEvent> billingEventConsumer() {
        return event -> {
            log.info(">>> Analytics received billing event: Bill ID = {}", event.billId());

            AnalyticsRecord record = new AnalyticsRecord();
            record.setEventType("BILLING");
            record.setEventAction("CREATED");
            record.setEntityId(event.billId());
            record.setEntityName("Bill #" + event.billId());
            record.setAmount(event.totalAmount());
            record.setEventDate(event.billingDate());
            record.setCreatedAt(new Date());

            analyticsRepository.save(record);
            log.info(">>> Analytics stored billing record: {}", record);
        };
    }

    @Bean
    public Consumer<SupplierEvent> supplierEventConsumer() {
        return event -> {
            log.info(">>> Analytics received supplier event: {} - {}", event.getEventType(), event.getSupplierName());

            AnalyticsRecord record = new AnalyticsRecord();
            record.setEventType("SUPPLIER");
            record.setEventAction(event.getEventType());
            record.setEntityId(event.getSupplierId());
            record.setEntityName(event.getSupplierName());
            record.setAmount(0.0);
            record.setEventDate(event.getTimestamp());
            record.setCreatedAt(new Date());

            analyticsRepository.save(record);
            log.info(">>> Analytics stored supplier record: {}", record);
        };
    }
}
