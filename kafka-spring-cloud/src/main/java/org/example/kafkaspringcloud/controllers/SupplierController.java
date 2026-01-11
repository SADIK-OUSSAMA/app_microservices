package org.example.kafkaspringcloud.controllers;

import lombok.RequiredArgsConstructor;
import org.example.kafkaspringcloud.entities.Supplier;
import org.example.kafkaspringcloud.events.SupplierEvent;
import org.example.kafkaspringcloud.repositories.SupplierRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final StreamBridge streamBridge;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Supplier> searchSuppliers(@RequestParam(required = false) String name,
            @RequestParam(required = false) String company) {
        if (name != null) {
            return supplierRepository.findByNameContainingIgnoreCase(name);
        } else if (company != null) {
            return supplierRepository.findByCompanyContainingIgnoreCase(company);
        }
        return supplierRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        Supplier saved = supplierRepository.save(supplier);

        // Publish event to Kafka
        SupplierEvent event = new SupplierEvent(saved.getId(), saved.getName(),
                saved.getCompany(), "CREATED", new Date());
        streamBridge.send("SUPPLIER_EVENTS", event);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierRepository.findById(id)
                .map(existing -> {
                    supplier.setId(id);
                    Supplier updated = supplierRepository.save(supplier);

                    // Publish event
                    SupplierEvent event = new SupplierEvent(updated.getId(), updated.getName(),
                            updated.getCompany(), "UPDATED", new Date());
                    streamBridge.send("SUPPLIER_EVENTS", event);

                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        return supplierRepository.findById(id)
                .map(supplier -> {
                    supplierRepository.delete(supplier);

                    // Publish event
                    SupplierEvent event = new SupplierEvent(supplier.getId(), supplier.getName(),
                            supplier.getCompany(), "DELETED", new Date());
                    streamBridge.send("SUPPLIER_EVENTS", event);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
