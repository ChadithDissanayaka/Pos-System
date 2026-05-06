package com.devstack.POS.api;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;
import com.devstack.POS.service.CustomerOrderService;
import com.devstack.POS.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createOrder(@Valid @RequestBody CustomerOrderRequestDTO dto) {
        customerOrderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Order created successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<StandardResponseDTO> getOrderById(@PathVariable UUID orderId) {
        CustomerOrderResponseDTO order = customerOrderService.getOrderById(orderId);
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Order retrieved successfully")
                .data(order)
                .build());
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllOrders() {
        List<CustomerOrderResponseDTO> orders = customerOrderService.getAllOrders();
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Orders retrieved successfully")
                .data(orders)
                .build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<StandardResponseDTO> getOrdersByCustomer(@PathVariable UUID customerId) {
        List<CustomerOrderResponseDTO> orders = customerOrderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Customer orders retrieved successfully")
                .data(orders)
                .build());
    }

    @GetMapping("/date-range")
    public ResponseEntity<StandardResponseDTO> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CustomerOrderResponseDTO> orders = customerOrderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Orders retrieved successfully")
                .data(orders)
                .build());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<StandardResponseDTO> deleteOrder(@PathVariable UUID orderId) {
        customerOrderService.deleteOrder(orderId);
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Order deleted successfully")
                .data(null)
                .build());
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<StandardResponseDTO> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO updatedOrder = customerOrderService.updateOrder(orderId, dto);
        return ResponseEntity.ok(StandardResponseDTO.builder()
                .code(200)
                .message("Order updated successfully")
                .data(updatedOrder)
                .build());
    }
}
