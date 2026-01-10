package ma.emsi.sadik.billingservice;

import ma.emsi.sadik.billingservice.entities.Bill;
import ma.emsi.sadik.billingservice.entities.ProductItem;
import ma.emsi.sadik.billingservice.events.BillEventProducer;
import ma.emsi.sadik.billingservice.feign.CustomerRestClient;
import ma.emsi.sadik.billingservice.feign.ProductRestClient;
import ma.emsi.sadik.billingservice.model.Customer;
import ma.emsi.sadik.billingservice.model.Product;
import ma.emsi.sadik.billingservice.repository.BillRepository;
import ma.emsi.sadik.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BillRepository billRepository,
            ProductItemRepository productItemRepository,
            CustomerRestClient customerRestClient,
            ProductRestClient productRestClient,
            BillEventProducer billEventProducer) {

        return args -> {
            Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
            Collection<Product> products = productRestClient.getAllProducts().getContent();

            customers.forEach(customer -> {
                Bill bill = Bill.builder()
                        .billingDate(new Date())
                        .customerId(customer.getId())
                        .build();
                billRepository.save(bill);
                products.forEach(product -> {
                    ProductItem productItem = ProductItem.builder()
                            .bill(bill)
                            .productId(product.getId())
                            .quantity(1 + new Random().nextInt(10))
                            .unitPrice(product.getPrice())
                            .build();
                    productItemRepository.save(productItem);
                });
                // Refresh bill to get product items and publish event
                Bill savedBill = billRepository.findById(bill.getId()).orElse(bill);
                billEventProducer.publishBillCreatedEvent(savedBill);
            });
        };
    }

}