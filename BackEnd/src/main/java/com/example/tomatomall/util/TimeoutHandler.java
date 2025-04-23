package com.example.tomatomall.util;

import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.OrderArchive;
import com.example.tomatomall.po.Orders;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.OrderArchiveRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductStockpileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TimeoutHandler {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;

    private static final long TIMEOUT_DURATION = 300*1000; //300s,调试的时候改短点
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean orderTimeoutActive = false;

    public TimeoutHandler() {
        scheduler.scheduleAtFixedRate(() -> {
            if(orderTimeoutActive) {
                List<Orders> orders = orderRepository.findByStatus(StatusEnum.PENDING);
                if(orders==null)
                    orderTimeoutActive=false;
                else{
                    for(Orders item:orders){
                        if(System.currentTimeMillis()-item.getCreateTime().getTime()>=TIMEOUT_DURATION) {
                            handleTimeout(String.valueOf(item.getOrderId()));
                            System.out.println("#####订单号 "+item.getOrderId()+" 已超时!");
                            System.out.println("#####创建时间:"+item.getCreateTime()+";过期时间:"+new Time(System.currentTimeMillis()));
                        }
                    }
                }
            }
        }, 1, 60, TimeUnit.SECONDS);
    }

    public void enable() {
        orderTimeoutActive=true;
    }

    private void handleTimeout(String orderId) {//解耦
        Orders order;
        if(orderRepository.findById(new Integer(orderId)).isPresent())
            order=orderRepository.findById(new Integer(orderId)).get();
        else
            throw TomatoMallException.orderNotExist();
        if(order.getStatus().equals(StatusEnum.TIMEOUT))
            throw TomatoMallException.duplicateOrderUpdate(orderId);
        order.setStatus(StatusEnum.TIMEOUT);
        orderRepository.save(order);
        List<OrderArchive> orderArchiveList=orderArchiveRepository.findByOrderId(new Integer(orderId));
        for(OrderArchive archive:orderArchiveList){
            ProductStockpile ps=productStockpileRepository.findByProductId(archive.getProductId());
            if(ps==null)
                throw TomatoMallException.orderCartProductInvalid();//处理订单中被删除的商品，这样不安全
            else if (ps.getFrozen() < archive.getQuantity()) {
                System.err.println("##### order amount invalid???");//我搞不出这个
                throw TomatoMallException.duplicateOrderUpdate(orderId);
            }else {
                ps.setFrozen(ps.getFrozen() - archive.getQuantity());
                ps.setAmount(ps.getAmount() + archive.getQuantity());
                productStockpileRepository.save(ps);
            }
        }
    }
}
