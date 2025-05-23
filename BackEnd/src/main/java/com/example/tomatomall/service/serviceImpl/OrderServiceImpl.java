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
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayProperties;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.ProductOfOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    ProductRepository productRepository;
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

    @Override
    public List<OrderVO> getOrderByUserId(String userId) {
        if(!accountRepository.findById(new Integer(userId)).isPresent()){
            throw TomatoMallException.usernameNotExists();
        }
        List<Orders> orderList=orderRepository.findByUserId(new Integer(userId));
        List<OrderVO> orderVOList=new ArrayList<>();
        for(Orders order:orderList){
            OrderVO orderVO=order.toVO();
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public OrderVO getOrderById(String orderId) { //直接返回orderVO类，不包含订单商品信息
        if(!orderRepository.findById(new Integer(orderId)).isPresent()){
            throw TomatoMallException.orderNotExist();
        }
        Orders order=orderRepository.findById(new Integer(orderId)).get();
        return order.toVO();
    }

    @Override
    public List<ProductOfOrderVO> getProductsByOrderId(String orderId) { //新增了一个VO类，返回购买商品的详细信息
        if(!orderRepository.findById(new Integer(orderId)).isPresent()){
            throw TomatoMallException.orderNotExist();
        }
        List<OrderArchive> orderArchiveList=orderArchiveRepository.findByOrderId(new Integer(orderId));
        List<ProductOfOrderVO> productOfOrderVOList=new ArrayList<>();
        for(OrderArchive archive:orderArchiveList){
            Integer productId=archive.getProductId();
            Product product=productRepository.findById(productId).get();
            ProductOfOrderVO productOfOrderVO=new ProductOfOrderVO();
            productOfOrderVO.setProductId(productId);
            productOfOrderVO.setProductName(product.getTitle());
            productOfOrderVO.setProductImage(product.getCover());
            productOfOrderVO.setProductPrice(product.getPrice());
            productOfOrderVO.setProductQuantity(archive.getQuantity());
            productOfOrderVOList.add(productOfOrderVO);
        }
        return productOfOrderVOList;
    }

    @Override
    public String cancelOrder(String orderId) {
        if(!orderRepository.findById(new Integer(orderId)).isPresent())
            throw TomatoMallException.orderNotExist();
        Orders order=orderRepository.findById(new Integer(orderId)).get();
        if(order.getStatus().equals(StatusEnum.PENDING)){
            orderRepository.delete(order);
            List<OrderArchive> orderArchiveList=orderArchiveRepository.findByOrderId(new Integer(orderId));
            orderArchiveRepository.deleteAll(orderArchiveList);
            return "订单已取消";
        }else
            return "订单已支付，无法取消";
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
                if(!reduce) {
                    ps.setAmount(ps.getAmount() + archive.getQuantity());
                    cartService.updateOrderFlag(String.valueOf(archive.getCartItemId()),false);
                }
                else
                    cartService.deleteCartItem(String.valueOf(archive.getCartItemId()));
                productStockpileRepository.save(ps);
            }
        }
    }
}