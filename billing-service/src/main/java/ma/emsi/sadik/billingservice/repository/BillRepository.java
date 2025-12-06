package ma.emsi.sadik.billingservice.repository;

import ma.emsi.sadik.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository  extends JpaRepository<Bill, Long> {
}
