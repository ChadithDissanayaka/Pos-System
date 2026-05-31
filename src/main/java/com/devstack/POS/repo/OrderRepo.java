package com.devstack.POS.repo;

import com.devstack.POS.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@EnableJpaRepositories
public interface OrderRepo  extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findByCustomer_Id(UUID customerId);
    Page<CustomerOrder> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}
