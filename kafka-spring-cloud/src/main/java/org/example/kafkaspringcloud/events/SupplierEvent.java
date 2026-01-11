package org.example.kafkaspringcloud.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierEvent {
    private Long supplierId;
    private String supplierName;
    private String company;
    private String eventType; // CREATED, UPDATED, DELETED
    private Date timestamp;
}
