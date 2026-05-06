package com.devstack.POS.service;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CustomerOrderService {
    void createOrder(CustomerOrderRequestDTO dto);
    CustomerOrderResponseDTO getOrderById(UUID orderId);
    PagedResponseDTO<CustomerOrderResponseDTO> getAllOrders(int page, int size);
    List<CustomerOrderResponseDTO> getOrdersByCustomer(UUID customerId);
    PagedResponseDTO<CustomerOrderResponseDTO> getOrdersByDateRange(LocalDate startDate, LocalDate endDate, int page, int size);
    void deleteOrder(UUID orderId);
    CustomerOrderResponseDTO updateOrder(UUID orderId, CustomerOrderRequestDTO dto);
}
