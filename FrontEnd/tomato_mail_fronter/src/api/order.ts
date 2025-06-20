import { axios } from '../utils/request';
import { API_MODULE } from './_prefix';

const ORDER_MODULE = `${API_MODULE}/orders`;

type PaymentInfo = {
    orderId: string;
};

// 发起支付
export const initiatePayment = async (orderId: string) => {
    const paymentInfo: PaymentInfo = { orderId };
    return axios.post(`${ORDER_MODULE}/${paymentInfo.orderId}/pay`).then(res => {
        return res;
    });
};

// 获取订单列表
export const getOrderItems = async (userId:string) => {
    return axios.get(`/api/orders/orderList/${userId}`).then(res => {
        return res;
    });
};

// 获取订单详情
export const getOrderDetial = (orderId:string) => {
    return axios.get(`/api/orders/${orderId}`)
        .then(res => {
            return res
        })
}

//获取订单商品列表
export const getOrderProducts = async (orderId:string) => {
    return axios.get(`/api/orders/productOfOrder/${orderId}`).then(res => {
        return res;
    });
};

//取消订单
export const cancelOrder = async (orderId:string) => {
    return axios.delete(`/api/orders/${orderId}`).then(res => {
        return res;
    });
};