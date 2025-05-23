package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.PaymentEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TimeoutHandler;
import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockpileRepository productStockpileRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    TimeoutHandler timeoutHandler;
    @Autowired
    OrderArchiveRepository orderArchiveRepository;
    @Autowired
    OrderRepository orderRepository;

    @Override
    public CartVO addCart(String productId, Integer quantity,Integer discount) {
        Product product;
        Cart cart =new Cart();
        CartVO cartVO = new CartVO();
        if(productRepository.findById(new Integer(productId)).isPresent()) {
            product = productRepository.findById(new Integer(productId)).get();
            Integer userId = securityUtil.getCurrentUser().getId();
            Integer stockpile = productStockpileRepository.findByProductId(new Integer(productId)).getAmount();
            if(stockpile < quantity) {
                throw TomatoMallException.cartProductQuantityNotEnough();
            }
            cart.setProductId(new Integer(productId));
            cart.setUserId(userId);
            cart.setQuantity(quantity);
            if(discount!=null){
                cart.setDiscount(discount);
            }
            Cart savedCart= cartRepository.save(cart);
            cartVO.setCartItemId(savedCart.getCartItemId());
            cartVO.setProductId(product.getId());
            cartVO.setQuantity(quantity);
            cartVO.setTitle(product.getTitle());
            cartVO.setPrice(product.getPrice());
            cartVO.setDescription(product.getDescription());
            cartVO.setCover(product.getCover());
            cartVO.setDetail(product.getDetail());
            if(discount!=null){
                cartVO.setDiscount(discount);
            }
        }else
            throw TomatoMallException.productNotExists();


        return cartVO;
    }

    @Override
    public String deleteCartItem(String cartItemId) {
        Cart cart = cartRepository.findByCartItemId(new Integer(cartItemId));
        if(cart!= null){
            cartRepository.delete(cart);
            return "删除成功";
        }else{
            throw TomatoMallException.cartProductNotExists();
        }
    }

    @Override
    public String updateCartItem(String cartItemId, Integer quantity) {
        Cart cartItem;
        if(cartRepository.findById(new Integer(cartItemId)).isPresent())
            cartItem=cartRepository.findById(new Integer(cartItemId)).get();
        else
            throw TomatoMallException.cartProductNotExists();
        if(!Objects.equals(securityUtil.getCurrentUser().getId(), cartItem.getUserId()))
            throw TomatoMallException.userMismatch();
        if(productStockpileRepository.findByProductId(cartItem.getProductId())==null||quantity>productStockpileRepository.findByProductId(cartItem.getProductId()).getAmount())
            throw TomatoMallException.cartProductQuantityNotEnough();
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return "修改数量成功";
    }

    @Override
    public CartItemsVO getCartList() {
        CartItemsVO cartList=new CartItemsVO();
        List<CartVO> items=new ArrayList<>();
        Integer total = 0;
        float sum= (float) 0;
        List<Cart> raw=cartRepository.findByUserId(securityUtil.getCurrentUser().getId());
        if(!raw.isEmpty())
            for(Cart item:raw){
                CartVO VO=new CartVO();
                if(!productRepository.findById(item.getProductId()).isPresent())
                    throw TomatoMallException.cartProductNotExists();
                Product product=productRepository.findById(item.getProductId()).get();
                VO.setCartItemId(item.getCartItemId());
                VO.setProductId(item.getProductId());
                VO.setQuantity(item.getQuantity());
                VO.setOrdered(item.isOrdered());
                if(item.getDiscount()!=null){
                    VO.setDiscount(item.getDiscount());
                }else{
                    VO.setDiscount(null);
                }
                VO.setTitle(product.getTitle());
                VO.setPrice(product.getPrice());
                VO.setDescription(product.getDescription());
                VO.setCover(product.getCover());
                VO.setDetail(product.getDetail());
                items.add(VO);
                sum+=VO.getDiscount()!=null ? VO.getDiscount()*VO.getPrice()*VO.getQuantity()/10 : VO.getPrice()*VO.getQuantity();
                total++;
            }
        cartList.setItems(items);
        cartList.setTotal(total);
        cartList.setTotalAmount(sum);
        return cartList;
    }
    @Override
    public OrderVO submitOrder(List<String> cartItemIds, ShippingAddress shipping_address, String payment_method) {
        Orders raw=new Orders();
        float totalAmount= (float) 0;
        try{
            PaymentEnum.valueOf(payment_method);}
        catch (Exception e) {
            throw TomatoMallException.orderPaymentMethodInvalid(payment_method);
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
            if(item.getDiscount()!=null){
                totalAmount += item.getQuantity()*productRepository.findById(item.getProductId()).get().getPrice()*item.getDiscount()/10;
            }else{
            totalAmount += item.getQuantity()*productRepository.findById(item.getProductId()).get().getPrice();
            }
            //显然这里可以使同一购物车商品多次添加到不同订单，但这个版本选择不处理，到自由需求阶段修改po再处理
            //1.删除购物车商品/标记无效
            //2.限制用户只能有一个订单
            if(item.isOrdered())
                throw TomatoMallException.orderCartProductInvalid();
            ProductStockpile productStockpile = productStockpileRepository.findByProductId(cartRepository.findById(new Integer(id)).get().getProductId());
            if(productStockpile==null){
                throw TomatoMallException.productNotExists();
            }
            productStockpile.setAmount(ps.getAmount()-item.getQuantity());
            productStockpile.setFrozen(ps.getFrozen()+item.getQuantity());
            productStockpileRepository.save(productStockpile);
            updateOrderFlag(id,true);
        }
        raw.setUserId(securityUtil.getCurrentUser().getId());//帮别人下单不当作异常:D
        raw.setTotalAmount(totalAmount);
        raw.setPayMethod(PaymentEnum.valueOf(payment_method));
        //status=PENDING
        raw.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //添加shipping_address，虽然没有用到？？？
        raw.setName(shipping_address.getName());
        raw.setPhone(shipping_address.getPhone());
        raw.setAddress(shipping_address.getAddress());
        Orders saved=orderRepository.save(raw);
        timeoutHandler.enable();
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
    public void updateOrderFlag(String cartItemId,boolean ordered){
        Cart cart = cartRepository.findByCartItemId(new Integer(cartItemId));
        if(cart!= null){
            cart.setOrdered(ordered);
            cartRepository.save(cart);
        }else{
            throw TomatoMallException.cartProductNotExists();
        }
    }
}
