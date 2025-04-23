import { axios } from '../utils/request';
import { API_MODULE } from './_prefix';

const CART_MODULE = `${API_MODULE}/cart`;

type ShippingAddress = {
    name: string;
    phone: string;
    address: string;
};

type CreateOrderInfo = {
    cartItemIds: string[];
    shipping_address: ShippingAddress;
    payment_method: string;
};

type AddToCartInfo = {
    productId: string;
    quantity: number;
};

// 获取购物车商品
export const getCartItems = async () => {
    return axios.get(`${CART_MODULE}`).then(res => {
        return res;
    });
};

// 更新购物车商品数量
export const updateCartItem = async (cartItemId: string, quantity: number) => {
    return axios.patch(`${CART_MODULE}/${cartItemId}`,  { quantity: quantity }, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
};

// 删除购物车商品
export const deleteCartItem = async (cartItemId: string) => {
    return axios.delete(`${CART_MODULE}/${cartItemId}`).then(res => {
        return res;
    });
};

// 提交购物车订单
export const createOrder = async (cartItemIds: string[], shippingAddress: ShippingAddress, paymentMethod: string) => {
    const orderInfo: CreateOrderInfo = {
        cartItemIds,
        shippingAddress: shippingAddress,
        paymentMethod: paymentMethod,
    };
    return axios.post(`${CART_MODULE}/checkout`, orderInfo).then(res => {
        return res;
    });
};

// 加入购物车
export const addToCart = async (productId: string, quantity: number) => {
    const addToCartInfo: AddToCartInfo = {
        productId,
        quantity,
    };
    return axios.post(`${CART_MODULE}`, addToCartInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
};