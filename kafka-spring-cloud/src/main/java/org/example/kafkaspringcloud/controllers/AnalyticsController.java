package org.example.kafkaspringcloud.controllers;

import lombok.RequiredArgsConstructor;
import org.example.kafkaspringcloud.entities.AnalyticsRecord;
import org.example.kafkaspringcloud.repositories.AnalyticsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnalyticsController {

    private final AnalyticsRepository analyticsRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("totalBillingAmount", analyticsRepository.getTotalBillingAmount());
        dashboard.put("billingEventCount", analyticsRepository.getBillingEventCount());
        dashboard.put("supplierEventCount", analyticsRepository.getSupplierEventCount());
        dashboard.put("recentEvents", analyticsRepository.findTop10ByOrderByCreatedAtDesc());

        return dashboard;
    }

    @GetMapping("/events")
    public List<AnalyticsRecord> getAllEvents() {
        return analyticsRepository.findAll();
    }

    @GetMapping("/events/billing")
    public List<AnalyticsRecord> getBillingEvents() {
        return analyticsRepository.findByEventType("BILLING");
    }

    @GetMapping("/events/supplier")
    public List<AnalyticsRecord> getSupplierEvents() {
        return analyticsRepository.findByEventType("SUPPLIER");
    }

    @GetMapping("/recent")
    public List<AnalyticsRecord> getRecentEvents() {
        return analyticsRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
