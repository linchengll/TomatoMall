package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.tomatomall.enums.PaymentEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Orders;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.ProductStockpileRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayProperties;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    ProductStockpileRepository productStockpileRepository;
    SecurityUtil securityUtil;
    AlipayProperties alipayProperties;

    @Override
    public OrderVO submitOrder(List<String> cartItemIds, Object shipping_address, String payment_method) {
        Orders raw=new Orders();
        //OrderEx archive=new OrderEx;
        float totalAmount= (float) 0;
        try{PaymentEnum.valueOf(payment_method);}
        catch (IllegalArgumentException e) {
            throw TomatoMallException.orderPaymentMethodInvalid();
        }
        for(String id : cartItemIds) {
            //后半部分理应恒为true
            if (!cartRepository.findById(new Integer(id)).isPresent() || !productRepository.findById(cartRepository.findById(new Integer(id)).get().getProductId()).isPresent())
                throw TomatoMallException.orderCartProductInvalid();
        }
        for(String id : cartItemIds){
            Cart item=cartRepository.findById(new Integer(id)).get();
            ProductStockpile ps=productStockpileRepository.findByProductId(item.getProductId());
            totalAmount += item.getQuantity()*productRepository.findById(item.getProductId()).get().getPrice();
                //1.删除购物车商品/标记无效
                //2.限制用户只能有一个订单
                //数据库可能要保存订单信息?
            //if(ps.hasTag)
            ProductStockpile productStockpile = productStockpileRepository.findByProductId(cartRepository.findById(new Integer(id)).get().getProductId());
            if(productStockpile==null){
                throw TomatoMallException.productNotExists();
            }
            productStockpile.setAmount(ps.getAmount()-item.getQuantity());
            productStockpile.setFrozen(ps.getFrozen()+item.getQuantity());
            //timeStamp?
            productStockpileRepository.save(productStockpile);

        }
            //
            //shipping_address->???
            //
        raw.setUserId(securityUtil.getCurrentUser().getId());//帮别人下单就不当作异常了吧:D
        raw.setTotalAmount(totalAmount);
        raw.setPayMethod(PaymentEnum.valueOf(payment_method));
        //status=PENDING
        raw.setCreateTime(new Time(System.currentTimeMillis()));
        Orders saved=orderRepository.save(raw);
        return saved.toVO();
    }

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

            // 构建返回对象
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
}
