package org.example.kafkaspringcloud.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType; // BILLING, SUPPLIER
    private String eventAction; // CREATED, UPDATED, DELETED
    private Long entityId;
    private String entityName;
    private Double amount;
    private Date eventDate;

    @Column(name = "created_at")
    private Date createdAt = new Date();
}
