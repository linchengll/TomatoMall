package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name ="specifications")
public class ProductSpecification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "item")
    private String item;

    @Basic
    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
}
