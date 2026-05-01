package com.devstack.POS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "customer_order")
@Builder
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID orderId;

    //Fetch Lazy meanning, when we fetch data from super class we also got subclass detils
    // also, so making lazy we can get data only we need
    //JoinColum got to hold forgin key got from customer table, Normally many side hold forgin key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name="total_cost")
    private double totalCost;

    private LocalDate date;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private List<OrderDetails> detailsList;

}

