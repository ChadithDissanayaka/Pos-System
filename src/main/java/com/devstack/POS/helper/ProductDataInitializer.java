package com.devstack.POS.helper;

import com.devstack.POS.dto.request.ProductRequestDTO;
import com.devstack.POS.repo.ProductRepo;
import com.devstack.POS.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductService productService;
    private final ProductRepo productRepo;  // injected to check existing data

    @Override
    public void run(String... args) throws Exception {
        if (productRepo.count() > 0) {
            log.info("Products already exist, skipping initialization.");
            return;
        }

        log.info("No products found. Initializing 20 products...");
        getInitialProducts().forEach(productService::createProduct);
        log.info("Successfully initialized 20 products.");
    }

    private List<ProductRequestDTO> getInitialProducts() {
        return List.of(
                new ProductRequestDTO("Laptop Dell Inspiron", 1200.00, 50),
                new ProductRequestDTO("Wireless Mouse Logitech", 25.99, 200),
                new ProductRequestDTO("Mechanical Keyboard", 89.99, 100),
                new ProductRequestDTO("27-inch Monitor Samsung", 349.99, 30),
                new ProductRequestDTO("USB Flash Drive 32GB", 12.50, 150),
                new ProductRequestDTO("Bluetooth Headphones Sony", 79.99, 80),
                new ProductRequestDTO("External Hard Drive 1TB", 59.99, 60),
                new ProductRequestDTO("Webcam Logitech HD", 49.99, 120),
                new ProductRequestDTO("Printer Ink Cartridge", 29.99, 90),
                new ProductRequestDTO("Smartphone Case iPhone", 19.99, 300),
                new ProductRequestDTO("Gaming Mouse RGB", 39.99, 110),
                new ProductRequestDTO("SSD 500GB", 89.99, 70),
                new ProductRequestDTO("Router WiFi 6", 129.99, 40),
                new ProductRequestDTO("Power Bank 10000mAh", 24.99, 180),
                new ProductRequestDTO("Graphics Card NVIDIA", 499.99, 20),
                new ProductRequestDTO("Microphone USB", 69.99, 85),
                new ProductRequestDTO("Tablet Stand Adjustable", 14.99, 250),
                new ProductRequestDTO("Cable HDMI 2m", 9.99, 400),
                new ProductRequestDTO("Speakers Bluetooth", 34.99, 95),
                new ProductRequestDTO("VR Headset Oculus", 299.99, 15)
        );
    }
}