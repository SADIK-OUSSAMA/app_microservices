package ma.emsi.sadik.billingservice.repository;

import ma.emsi.sadik.billingservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
