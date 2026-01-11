package org.example.kafkaspringcloud.repositories;

import org.example.kafkaspringcloud.entities.AnalyticsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsRecord, Long> {

    List<AnalyticsRecord> findByEventType(String eventType);

    List<AnalyticsRecord> findByEventDateBetween(Date start, Date end);

    @Query("SELECT SUM(a.amount) FROM AnalyticsRecord a WHERE a.eventType = 'BILLING' AND a.eventAction = 'CREATED'")
    Double getTotalBillingAmount();

    @Query("SELECT COUNT(a) FROM AnalyticsRecord a WHERE a.eventType = 'BILLING'")
    Long getBillingEventCount();

    @Query("SELECT COUNT(a) FROM AnalyticsRecord a WHERE a.eventType = 'SUPPLIER'")
    Long getSupplierEventCount();

    List<AnalyticsRecord> findTop10ByOrderByCreatedAtDesc();
}
