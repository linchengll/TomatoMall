package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProductTypes {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "type_id")
    private Integer typeId;

    @Basic
    @Column(name = "type_name")
    private String typeName;

    @Basic
    @Column(name = "product_id")
    private String productId;
}
