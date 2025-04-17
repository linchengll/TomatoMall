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