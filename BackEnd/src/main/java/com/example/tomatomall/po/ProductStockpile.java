package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_stockpile")
public class ProductStockpile {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "amount")
    private Integer amount;

    @Basic
    @Column(name = "frozen")
    private Integer frozen;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false,referencedColumnName = "id")
    private Product product;
}
