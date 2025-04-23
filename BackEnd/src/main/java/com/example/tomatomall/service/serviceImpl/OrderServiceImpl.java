package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.OrderArchive;
import com.example.tomatomall.po.Orders;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayProperties;
import com.example.tomatomall.vo.PaymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    AlipayProperties alipayProperties;
    @Autowired
    CartService cartService;



    @Override
    public PaymentVO handlePayment(String orderId) {
        if(!orderRepository.findById(new Integer(orderId)).isPresent())
            throw TomatoMallException.orderNotExist();
        Orders PO=orderRepository.findById(new Integer(orderId)).get();
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayProperties.getServerUrl(),
                alipayProperties.getAppId(),
                alipayProperties.getPrivateKey(),
                "json",
                "UTF-8",
                alipayProperties.getAlipayPublicKey(),
                "RSA2");

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(PO.getOrderId().toString());
        model.setTotalAmount(String.format("%.2f", PO.getTotalAmount())); // 必须保留两位小数
        model.setSubject("TomatoMall订单"); // 订单标题
        model.setProductCode("FAST_INSTANT_TRADE_PAY"); // 固定值

        request.setBizModel(model);
        try {
            // 调用SDK生成表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            String paymentForm = response.getBody();

            PaymentVO paymentVO = new PaymentVO();
            paymentVO.setPaymentForm(paymentForm);
            paymentVO.setOrderId(PO.getOrderId());
            paymentVO.setTotalAmount(PO.getTotalAmount());
            paymentVO.setPaymentMethod(PO.getPayMethod());
            System.out.println(paymentForm);
            return paymentVO;
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝接口调用失败", e);
        }

    }

    @Override
    public void handleSuccess(String orderId) {
        updateOrderStatus(orderId,StatusEnum.SUCCESS);
        updateStock(orderId,true);
    }

    @Override
    public void handleTimeout(String orderId) {
        updateOrderStatus(orderId,StatusEnum.TIMEOUT);
        updateStock(orderId,false);
    }

    @Override
    public void handleFailure(String orderId) {
        updateOrderStatus(orderId,StatusEnum.FAILED);
        updateStock(orderId,false);
    }

    private void updateOrderStatus(String orderId, StatusEnum status) {
        Orders order;
        if(orderRepository.findById(new Integer(orderId)).isPresent())
            order=orderRepository.findById(new Integer(orderId)).get();
        else
            throw TomatoMallException.orderNotExist();
        if(order.getStatus().equals(status))
            throw TomatoMallException.duplicateOrderUpdate(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    private void updateStock(String orderId,boolean reduce) {
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
                if(!reduce)
                    ps.setAmount(ps.getAmount() + archive.getQuantity());
                else
                    cartService.deleteCartItem(String.valueOf(archive.getCartItemId()));
                productStockpileRepository.save(ps);
            }
        }
    }
}