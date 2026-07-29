package com.devstack.POS.api;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;
import com.devstack.POS.service.CustomerOrderService;
import com.devstack.POS.config.OpenApiConfig;
import com.devstack.POS.util.StandardResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Customer order management endpoints")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class CustomerOrderController {

        private final CustomerOrderService customerOrderService;

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Create a new order", description = "Requires ADMIN or MANAGER role")
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
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Get order by ID", description = "Requires ADMIN or MANAGER role")
        public ResponseEntity<StandardResponseDTO> getOrderById(@PathVariable UUID orderId) {
                CustomerOrderResponseDTO order = customerOrderService.getOrderById(orderId);
                return ResponseEntity.ok(StandardResponseDTO.builder()
                                .code(200)
                                .message("Order retrieved successfully")
                                .data(order)
                                .build());
        }

        @GetMapping("/customer/{customerId}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Get orders by customer ID", description = "Requires ADMIN or MANAGER role")
        public ResponseEntity<StandardResponseDTO> getOrdersByCustomer(@PathVariable UUID customerId) {
                List<CustomerOrderResponseDTO> orders = customerOrderService.getOrdersByCustomer(customerId);
                return ResponseEntity.ok(StandardResponseDTO.builder()
                                .code(200)
                                .message("Customer orders retrieved successfully")
                                .data(orders)
                                .build());
        }

        @DeleteMapping("/{orderId}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Delete an order", description = "Requires ADMIN role")
        public ResponseEntity<StandardResponseDTO> deleteOrder(@PathVariable UUID orderId) {
                customerOrderService.deleteOrder(orderId);
                return ResponseEntity.ok(StandardResponseDTO.builder()
                                .code(200)
                                .message("Order deleted successfully")
                                .data(null)
                                .build());
        }

        @PutMapping("/{orderId}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Update an order", description = "Requires ADMIN or MANAGER role")
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

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
        @Operation(summary = "Get all orders with pagination and search filter")
        public ResponseEntity<StandardResponseDTO> getAllOrders(
                        @RequestParam(defaultValue = "") String searchText,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                PagedResponseDTO<CustomerOrderResponseDTO> orders = customerOrderService.searchOrders(searchText, page,
                                size);
                return ResponseEntity.ok(StandardResponseDTO.builder()
                                .code(200)
                                .message("Orders retrieved successfully")
                                .data(orders)
                                .build());
        }

        @GetMapping("/date-range")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
        @Operation(summary = "Get orders within a date range")
        public ResponseEntity<StandardResponseDTO> getOrdersByDateRange(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                PagedResponseDTO<CustomerOrderResponseDTO> orders = customerOrderService.getOrdersByDateRange(startDate,
                                endDate, page, size);
                return ResponseEntity.ok(StandardResponseDTO.builder()
                                .code(200)
                                .message("Orders retrieved successfully")
                                .data(orders)
                                .build());
        }
}
