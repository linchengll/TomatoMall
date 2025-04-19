package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.tomatomall.enums.PaymentEnum;
import com.example.tomatomall.enums.StatusEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.OrderArchive;
import com.example.tomatomall.po.Orders;
import com.example.tomatomall.po.ProductStockpile;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayProperties;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Time;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    ProductStockpileRepository productStockpileRepository;
    OrderArchiveRepository orderArchiveRepository;
    SecurityUtil securityUtil;
    AlipayProperties alipayProperties;

    @Override
    public OrderVO submitOrder(List<String> cartItemIds, Map<Object,String> shipping_address, String payment_method) {
        Orders raw=new Orders();
        float totalAmount= (float) 0;
        try{PaymentEnum.valueOf(payment_method);}
        catch (IllegalArgumentException e) {
            throw TomatoMallException.orderPaymentMethodInvalid();
        }
        //检验购物车商品有效性
        for(String id : cartItemIds) {
            //后半部分理应恒为true
            if (!cartRepository.findById(new Integer(id)).isPresent() || !productRepository.findById(cartRepository.findById(new Integer(id)).get().getProductId()).isPresent())
                throw TomatoMallException.orderCartProductInvalid();
        }
        //计算金额，调整库存，.get()警告不用管
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
        raw.setUserId(securityUtil.getCurrentUser().getId());//帮别人下单就不当作异常了吧:D
        raw.setTotalAmount(totalAmount);
        raw.setPayMethod(PaymentEnum.valueOf(payment_method));
        //status=PENDING
        raw.setCreateTime(new Time(System.currentTimeMillis()));
        //添加shipping_address，虽然没有用到？？？
        raw.setName(shipping_address.get("name"));
        raw.setPhone(shipping_address.get("phone"));
        raw.setAddress(shipping_address.get("address"));
        Orders saved=orderRepository.save(raw);
        for(String id : cartItemIds){
            OrderArchive orderArchive= (OrderArchive) cartRepository.findById(new Integer(id)).get();
            orderArchive.setOrderId(saved.getOrderId());
            orderArchiveRepository.save(orderArchive);
        }
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

    @Override
    public void updateOrderStatus(String orderId, StatusEnum status) {
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

    @Override
    public void reduceStock(String orderId) {
        List<OrderArchive> orderArchiveList=orderArchiveRepository.findByOrderId(new Integer(orderId));
        for(OrderArchive archive:orderArchiveList){
            ProductStockpile ps=productStockpileRepository.findByProductId(archive.getProductId());
            if(ps==null)
                throw TomatoMallException.orderCartProductInvalid();//处理订单中被删除的商品，这样不安全
            else if (ps.getFrozen() < archive.getQuantity()) {
                System.err.println("#####Unexpected???");//这个用现有测试方法应该不会触发
                throw TomatoMallException.duplicateOrderUpdate(orderId);
            }else {
                ps.setFrozen(ps.getFrozen() - archive.getQuantity());
                productStockpileRepository.save(ps);
            }
        }
    }
}
