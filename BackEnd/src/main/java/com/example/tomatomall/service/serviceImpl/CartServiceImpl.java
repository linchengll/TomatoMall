package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.ProductSpecificationRepository;
import com.example.tomatomall.repository.ProductStockpileRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CartItemsVO;
import com.example.tomatomall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ProductSpecificationRepository productSpecificationRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    SecurityUtil securityUtil;

    @Override
    public CartVO addCart(String productId, Integer quantity) {
        return null;
    }

    @Override
    public String deleteCartItem(String cartItemId) {
        return "";
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
        if(!productStockpileRepository.findById(cartItem.getProductId()).isPresent()||quantity-cartItem.getQuantity()<productStockpileRepository.findById(cartItem.getProductId()).get().getAmount())
            throw TomatoMallException.cartProductQuantityNotEnough();
        cartItem.setQuantity(quantity);
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
                if(productRepository.findById(item.getProductId()).isPresent())
                    throw TomatoMallException.cartProductNotExists();
                Product product=productRepository.findById(item.getProductId()).get();
                VO.setCartItemId(item.getCartItemId());
                VO.setProductId(item.getProductId());
                VO.setQuantity(item.getQuantity());
                VO.setTitle(product.getTitle());
                VO.setPrice(product.getPrice());
                VO.setDescription(product.getDescription());
                VO.setCover(product.getCover());
                VO.setDetail(product.getDetail());
                sum+=VO.getPrice()*VO.getQuantity();
                total++;
            }
        cartList.setItems(items);
        cartList.setTotal(total);
        cartList.setTotalAmount(sum);
        return cartList;
    }
}
