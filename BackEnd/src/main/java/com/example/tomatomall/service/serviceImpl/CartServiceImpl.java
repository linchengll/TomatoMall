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
            Cart savedCart= cartRepository.save(cart);
            cartVO.setCartItemId(savedCart.getCartItemId());
            cartVO.setProductId(product.getId());
            cartVO.setQuantity(quantity);
            cartVO.setTitle(product.getTitle());
            cartVO.setPrice(product.getPrice());
            cartVO.setDescription(product.getDescription());
            cartVO.setCover(product.getCover());
            cartVO.setDetail(product.getDetail());
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
        return "";
    }

    @Override
    public CartItemsVO getCartList() {
        return null;
    }
}
