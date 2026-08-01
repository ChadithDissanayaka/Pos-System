package com.devstack.POS.repo;

import com.devstack.POS.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@EnableJpaRepositories
public interface OrderRepo extends JpaRepository<CustomerOrder, UUID> {
        List<CustomerOrder> findByCustomer_Id(UUID customerId);

        Page<CustomerOrder> findByDateBetween(LocalDate startDate,
                        LocalDate endDate, Pageable pageable);

        @Query("SELECT o FROM CustomerOrder o JOIN o.customer c " +
                "WHERE LOWER(c.name) LIKE LOWER(:searchText) OR LOWER(CAST(o.orderId AS string)) LIKE LOWER(:searchText)")
        Page<CustomerOrder> findAllOrders(@Param("searchText") String searchText, Pageable pageable);

        @Query("SELECT COUNT(o) FROM CustomerOrder o JOIN o.customer c " +
                "WHERE LOWER(c.name) LIKE LOWER(:searchText) OR LOWER(CAST(o.orderId AS string)) LIKE LOWER(:searchText)")
        long countAllOrders(@Param("searchText") String searchText);

        @Query("SELECT SUM(o.totalCost) FROM CustomerOrder o")
        Double findTotalIncome();

        @Query("SELECT DISTINCT YEAR(o.date) FROM CustomerOrder o ORDER BY YEAR(o.date) DESC")
        List<Integer> findDistinctYears();

        List<CustomerOrder> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
