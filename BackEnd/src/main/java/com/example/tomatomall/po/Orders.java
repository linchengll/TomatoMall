package com.example.tomatomall.po;

import com.example.tomatomall.enums.StatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Orders {
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Id
     @Column(name = "order_id")
     private Integer orderId;

     @Column(name = "user_id")
     private Integer userId;

     @Column(name="total_amount")
     private Float totalAmount;

     @Column(name="pay_method")
     private String payMethod;

     @Column(name="status")
     private StatusEnum status=StatusEnum.PENDING;

     @Column(name="create_time")
     private Time createTime;


}
