package com.devstack.POS.service;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CustomerOrderService {
    void createOrder(CustomerOrderRequestDTO dto);
    CustomerOrderResponseDTO getOrderById(UUID orderId);
    List<CustomerOrderResponseDTO> getAllOrders();
    List<CustomerOrderResponseDTO> getOrdersByCustomer(UUID customerId);
    List<CustomerOrderResponseDTO> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);
    void deleteOrder(UUID orderId);
    CustomerOrderResponseDTO updateOrder(UUID orderId, CustomerOrderRequestDTO dto);
}
