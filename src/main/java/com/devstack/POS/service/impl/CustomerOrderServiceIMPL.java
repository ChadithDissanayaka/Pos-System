package com.devstack.POS.service.impl;

import com.devstack.POS.dto.request.CustomerOrderRequestDTO;
import com.devstack.POS.dto.request.OrderDetailsRequestDTO;
import com.devstack.POS.dto.response.CustomerOrderResponseDTO;
import com.devstack.POS.dto.response.PagedResponseDTO;
import com.devstack.POS.entity.Customer;
import com.devstack.POS.entity.CustomerOrder;
import com.devstack.POS.entity.OrderDetails;
import com.devstack.POS.entity.Product;
import com.devstack.POS.exception.EntryNotFoundException;
import com.devstack.POS.exception.ValidationException;
import com.devstack.POS.repo.CustomerRepo;
import com.devstack.POS.repo.OrderDetailsRepo;
import com.devstack.POS.repo.OrderRepo;
import com.devstack.POS.repo.ProductRepo;
import com.devstack.POS.service.CustomerOrderService;
import com.devstack.POS.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceIMPL implements CustomerOrderService {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final OrderMapper orderMapper;
    private final ProductRepo productRepo;
    private final OrderDetailsRepo orderDetailsRepo;

    @Override
    @Transactional
    public void createOrder(CustomerOrderRequestDTO dto) {
        Customer selectedCustomer = customerRepo.findById(dto.getCustomerId()).orElseThrow(
                () -> new EntryNotFoundException("Customer not found for provided id"));

        CustomerOrder savedData = orderRepo.save(orderMapper.toCustomerOrder(
                selectedCustomer, dto.getDetails(), dto.getDate()
        ));

        for (OrderDetailsRequestDTO temp : dto.getDetails()) {
            Product selectedProduct = productRepo.findById(temp.getProductId()).orElseThrow(
                    () -> new EntryNotFoundException(String.format("Product Not found %s", temp.getProductId())));

            if (temp.getQty() <= selectedProduct.getQtyOnHand()) {
                orderDetailsRepo.save(orderMapper.toOrderDetails(
                        savedData, selectedProduct, temp.getUnitPrice(), temp.getQty()
                ));

                selectedProduct.setQtyOnHand(selectedProduct.getQtyOnHand() - temp.getQty());
                productRepo.save(selectedProduct);

            } else {
                throw new ValidationException("Product qty is mismatch");
            }
        }

    }

    @Override
    public CustomerOrderResponseDTO getOrderById(UUID orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntryNotFoundException("Order not found with id: " + orderId));

        return orderMapper.toCustomerOrderResponseDTO(order);
    }

    @Override
    public PagedResponseDTO<CustomerOrderResponseDTO> getAllOrders(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pageResult = orderRepo.findAll(pageable);

        List<CustomerOrderResponseDTO> orders = pageResult.getContent().stream()
                .map(orderMapper::toCustomerOrderResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<CustomerOrderResponseDTO>builder()
                .dataList(orders)
                .dataCount(pageResult.getTotalElements())
                .build();
    }

    @Override
    public List<CustomerOrderResponseDTO> getOrdersByCustomer(UUID customerId) {
        // Verify customer exists
        customerRepo.findById(customerId)
                .orElseThrow(() -> new EntryNotFoundException("Customer not found with id: " + customerId));

        List<CustomerOrder> orders = orderRepo.findByCustomer_Id(customerId);
        return orders.stream()
                .map(orderMapper::toCustomerOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponseDTO<CustomerOrderResponseDTO> getOrdersByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        var pageable = PageRequest.of(page, size);
        var pageResult = orderRepo.findByDateBetween(startDate, endDate, pageable);

        List<CustomerOrderResponseDTO> orders = pageResult.getContent().stream()
                .map(orderMapper::toCustomerOrderResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<CustomerOrderResponseDTO>builder()
                .dataList(orders)
                .dataCount(pageResult.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntryNotFoundException("Order not found with id: " + orderId));

        // Restore product quantities before deleting
        for (OrderDetails detail : order.getDetailsList()) {
            Product product = detail.getProduct();
            product.setQtyOnHand(product.getQtyOnHand() + detail.getQty());
            productRepo.save(product);
        }

        // Delete order details first (due to foreign key constraints)
        orderDetailsRepo.deleteAll(order.getDetailsList());

        // Delete the order
        orderRepo.delete(order);
    }

    @Override
    @Transactional
    public CustomerOrderResponseDTO updateOrder(UUID orderId, CustomerOrderRequestDTO dto) {
        CustomerOrder existingOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntryNotFoundException("Order not found with id: " + orderId));

        Customer selectedCustomer = customerRepo.findById(dto.getCustomerId()).orElseThrow(
                () -> new EntryNotFoundException("Customer not found for provided id"));

        // Restore original product quantities
        for (OrderDetails detail : existingOrder.getDetailsList()) {
            Product product = detail.getProduct();
            product.setQtyOnHand(product.getQtyOnHand() + detail.getQty());
            productRepo.save(product);
        }

        // Delete existing order details
        orderDetailsRepo.deleteAll(existingOrder.getDetailsList());

        // Update order basic info
        existingOrder.setCustomer(selectedCustomer);
        existingOrder.setDate(dto.getDate());
        existingOrder.setTotalCost(0); // Will be recalculated

        CustomerOrder savedOrder = orderRepo.save(existingOrder);

        // Process new order details
        for (OrderDetailsRequestDTO temp : dto.getDetails()) {
            Product selectedProduct = productRepo.findById(temp.getProductId()).orElseThrow(
                    () -> new EntryNotFoundException(String.format("Product Not found %s", temp.getProductId())));

            if (temp.getQty() <= selectedProduct.getQtyOnHand()) {
                orderDetailsRepo.save(orderMapper.toOrderDetails(
                        savedOrder, selectedProduct, temp.getUnitPrice(), temp.getQty()
                ));

                selectedProduct.setQtyOnHand(selectedProduct.getQtyOnHand() - temp.getQty());
                productRepo.save(selectedProduct);

            } else {
                throw new ValidationException("Insufficient stock for product: " + selectedProduct.getDescription());
            }
        }

        // Recalculate and update total cost
        double newTotalCost = orderMapper.calculate(dto.getDetails());
        savedOrder.setTotalCost(newTotalCost);
        orderRepo.save(savedOrder);
        return orderMapper.toCustomerOrderResponseDTO(savedOrder);
    }

}
