package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.Response;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api/orders")
@ConfigurationProperties("alipay")
public class OrderController {
    private static String alipayPublicKey;

    @Resource
    OrderService orderService;

    @PostMapping("/checkout")
    public Response<OrderVO> submitOrder(@RequestBody Map<Object,Object> Body){
        return Response.buildSuccess(orderService.submitOrder((List<String>) Body.get("cartItemIds"),Body.get("shipping_address"), (String) Body.get("payment_method")));
    }

    @PostMapping("/{orderId}/pay")
    public Response<PaymentVO> handlePayment(@PathVariable String orderId){
         return Response.buildSuccess(orderService.handlePayment(orderId));
    }

    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        if (!signVerified) {
            response.getWriter().print("fail");
            return;
        }
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            String orderId = params.get("out_trade_no"); // 您的订单号
            String alipayTradeNo = params.get("trade_no"); // 支付宝交易号
            String amount = params.get("total_amount"); // 支付金额

            // 更新订单状态（注意幂等性，防止重复处理）
            //orderService.updateOrderStatus(orderId, alipayTradeNo, amount);

            // 扣减库存（建议加锁或乐观锁）
            //inventoryService.reduceStock(orderId);
        }
        response.getWriter().print("success");
    }
}
