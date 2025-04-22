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
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayProperties;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    AlipayProperties alipayProperties;
    @Autowired
    CartService cartService;

    @Override
    public OrderVO submitOrder(List<String> cartItemIds, ShippingAddress shipping_address, String payment_method) {
        Orders raw=new Orders();
        float totalAmount= (float) 0;
        try{PaymentEnum.valueOf(payment_method);}
        catch (Exception e) {
            throw TomatoMallException.orderPaymentMethodInvalid();
        }
        //检验购物车商品有效性
        for(String id : cartItemIds) {
            //后半部分理应恒为true
            if (!cartRepository.findById(new Integer(id)).isPresent()) //|| !productRepository.findById(cartRepository.findById(new Integer(id)).get().getProductId()).isPresent())
                throw TomatoMallException.orderCartProductInvalid();
        }
        //计算金额，调整库存，.get()警告不用管
        for(String id : cartItemIds){
            Cart item=cartRepository.findById(new Integer(id)).get();
            ProductStockpile ps=productStockpileRepository.findByProductId(item.getProductId());
            totalAmount += item.getQuantity()*productRepository.findById(item.getProductId()).get().getPrice();
            //显然这里可以使同一购物车商品多次添加到不同订单，但这个版本选择不处理，到自由需求阶段修改po再处理
            //1.删除购物车商品/标记无效
            //2.限制用户只能有一个订单
            //if(item.hasTag)
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
        raw.setName(shipping_address.getName());
        raw.setPhone(shipping_address.getPhone());
        raw.setAddress(shipping_address.getAddress());
        Orders saved=orderRepository.save(raw);
        for(String id : cartItemIds){
            Cart cart=cartRepository.findById(new Integer(id)).get();
            OrderArchive orderArchive= new OrderArchive();
            orderArchive.setCartItemId(cart.getCartItemId());
            orderArchive.setUserId(cart.getUserId());
            orderArchive.setProductId(cart.getProductId());
            orderArchive.setQuantity(cart.getQuantity());
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
