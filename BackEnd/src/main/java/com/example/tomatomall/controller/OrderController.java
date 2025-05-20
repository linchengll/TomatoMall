package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.PaymentVO;
import com.example.tomatomall.vo.ProductOfOrderVO;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.util.AlipayProperties;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Resource
    OrderService orderService;
    @Resource
    AlipayProperties alipayProperties;

    @PostMapping("/{orderId}/pay")
    public Response<PaymentVO> handlePayment(@PathVariable String orderId){
         return Response.buildSuccess(orderService.handlePayment(orderId));
    }

    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), "UTF-8", "RSA2");
        if (!signVerified) {
            response.getWriter().print("fail");
            return;
        }
        String tradeStatus = params.get("trade_status");
        String orderId = params.get("out_trade_no"); // 您的订单号
        String alipayTradeNo = params.get("trade_no"); // 支付宝交易号
        String amount = params.get("total_amount"); // 支付金额
        switch (tradeStatus) {
            case "TRADE_SUCCESS":
                // 支付成功
                orderService.handleSuccess(orderId);
                response.getWriter().print("success");
                break;

            case "TRADE_CLOSED":
                // 交易关闭（支付超时）
                // 检查是否是支付超时（而不是退款导致的关闭）
                if ("PAYMENT_TIMEOUT".equals(params.get("sub_code"))) {
                    orderService.handleTimeout(orderId);
                }
                response.getWriter().print("timeout");
                break;

            case "WAIT_BUYER_PAY":
                // 交易创建，等待买家付款
                // 通常不需要处理，但可以记录日志
                response.getWriter().print("wait_pay");
                break;

            default:
                // 其他未知状态或支付失败
                orderService.handleFailure(orderId);
                response.getWriter().print("fail");
                break;
        }

    }
    //根据用户Id获取所有订单
    @GetMapping("/orderList/{userId}")
    public Response<List<OrderVO>> getOrders(@PathVariable String userId){
        return Response.buildSuccess(orderService.getOrderByUserId(userId));
    }
    //根据订单Id获取订单详情
    @GetMapping("/{orderId}")
    public Response<OrderVO> getOrder(@PathVariable String orderId){
        return Response.buildSuccess(orderService.getOrderById(orderId));
    }
    //根据订单Id获取订单商品列表
    @GetMapping("/productOfOrder/{orderId}")
    public Response<List<ProductOfOrderVO>> getProductOfOrder(@PathVariable String orderId){
        return Response.buildSuccess(orderService.getProductsByOrderId(orderId));
    }
}