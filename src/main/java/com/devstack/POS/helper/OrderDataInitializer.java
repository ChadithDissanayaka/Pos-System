package com.devstack.POS.helper;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.request.OrderDetailsRequestDTO;
import com.devstack.POS.entity.Customer;
import com.devstack.POS.entity.Product;
import com.devstack.POS.repo.CustomerRepo;
import com.devstack.POS.repo.OrderRepo;
import com.devstack.POS.repo.ProductRepo;
import com.devstack.POS.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(3) // Run after CustomerDataInitializer (1) and ProductDataInitializer (2)
public class OrderDataInitializer implements CommandLineRunner {

    private final CustomerOrderService customerOrderService;
    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (orderRepo.count() > 0) {
            log.info("Orders already exist, skipping initialization.");
            return;
        }

        // Ensure we have customers and products before creating orders
        if (customerRepo.count() == 0 || productRepo.count() == 0) {
            log.warn("No customers or products found. Skipping order initialization.");
            return;
        }

        log.info("No orders found. Initializing sample orders...");
        createSampleOrders();
        log.info("Successfully initialized sample orders.");
    }

    private void createSampleOrders() {
        List<Customer> customers = customerRepo.findAll();
        List<Product> products = productRepo.findAll();

        // Create 15 sample orders with random data
        for (int i = 0; i < 15; i++) {
            Customer randomCustomer = customers.get(random.nextInt(customers.size()));

            // Create order with 1-5 random products
            int numProducts = random.nextInt(5) + 1;
            List<OrderDetailsRequestDTO> orderDetails = new ArrayList<>();

            for (int j = 0; j < numProducts; j++) {
                Product randomProduct = products.get(random.nextInt(products.size()));

                // Ensure we don't exceed available stock
                int qty = Math.min(random.nextInt(5) + 1, randomProduct.getQtyOnHand());
                if (qty > 0) {
                    orderDetails.add(new OrderDetailsRequestDTO(
                            randomProduct.getId(),
                            qty,
                            randomProduct.getUnitPrice()
                    ));
                }
            }

            if (!orderDetails.isEmpty()) {
                // Create order for a random date within the last 30 days
                LocalDate orderDate = LocalDate.now().minusDays(random.nextInt(30));

                CustomerOrderRequestDTO orderRequest = new CustomerOrderRequestDTO(
                        orderDate,
                        randomCustomer.getId(),
                        orderDetails
                );

                try {
                    customerOrderService.createOrder(orderRequest);
                } catch (Exception e) {
                    log.warn("Failed to create order {}: {}", i + 1, e.getMessage());
                }
            }
        }
    }
}
