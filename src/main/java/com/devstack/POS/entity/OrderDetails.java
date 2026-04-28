package com.devstack.POS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "order_details")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="order_details_id")
    private UUID id;

    //In many-to-many relationship all many side comes in shared entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private CustomerOrder customerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    //why unit price in both side of product and orderDetails both?
    //because when after buy order our product price change then we got miss balance matched to prevent that
    //we calculate invoice using this price
    @Column(name="unit_price")
    private Double unitPrice;

    @Column(name="qty")
    private Integer qty;

}
