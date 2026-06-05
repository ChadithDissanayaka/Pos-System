package com.devstack.POS.api;

import com.devstack.POS.dto.request.CustomerRequestDTO;
import com.devstack.POS.dto.response.CustomerResponseDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;
import com.devstack.POS.service.CustomerService;
import com.devstack.POS.config.OpenApiConfig;
import com.devstack.POS.util.StandardResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management endpoints")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new customer", description = "Requires ADMIN or MANAGER role")
    public ResponseEntity<StandardResponseDTO> createCustomer(@RequestBody CustomerRequestDTO dto) {
        customerService.createCustomer(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDTO.builder()
                        .code(201)
                        .message("Customer created successfully")
                        .data(null)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Update a customer", description = "Requires ADMIN or MANAGER role")
    public ResponseEntity<StandardResponseDTO> updateCustomer(
            @RequestBody CustomerRequestDTO dto,
            @PathVariable UUID id) {
        customerService.updateCustomer(dto, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Customer updated successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Delete a customer", description = "Requires ADMIN role")
    public ResponseEntity<StandardResponseDTO> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Customer deleted successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Get customer by ID", description = "Requires ADMIN or MANAGER role")
    public ResponseEntity<StandardResponseDTO> findCustomerById(@PathVariable UUID id) {
        CustomerResponseDTO customer = customerService.findCustomerById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Customer retrieved successfully")
                        .data(customer)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "Search customers with pagination")
    public ResponseEntity<StandardResponseDTO> searchCustomers(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<CustomerResponseDTO> result = customerService.searchCustomers(searchText, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Customers retrieved successfully")
                        .data(result)
                        .build());
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "Get all customers without pagination")
    public ResponseEntity<StandardResponseDTO> searchCustomers() {
        List<CustomerResponseDTO> result = customerService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StandardResponseDTO.builder()
                        .code(200)
                        .message("Customers retrieved successfully")
                        .data(result)
                        .build());
    }
}