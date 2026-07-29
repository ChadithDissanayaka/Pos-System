package com.devstack.POS.service;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CustomerOrderService {
    void createOrder(CustomerOrderRequestDTO dto);

    CustomerOrderResponseDTO updateOrder(UUID orderId, CustomerOrderRequestDTO dto);

    CustomerOrderResponseDTO getOrderById(UUID orderId);

    void deleteOrder(UUID orderId);

    PagedResponseDTO<CustomerOrderResponseDTO> searchOrders(String searchText, int page, int size);

    PagedResponseDTO<CustomerOrderResponseDTO> getAllOrders(int page, int size);

    List<CustomerOrderResponseDTO> getOrdersByCustomer(UUID customerId);

    PagedResponseDTO<CustomerOrderResponseDTO> getOrdersByDateRange(LocalDate startDate,
            LocalDate endDate, int page, int size);

}
