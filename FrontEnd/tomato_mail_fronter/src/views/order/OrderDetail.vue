<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import {getOrderDetial, getOrderProducts, cancelOrder, getOrderItems, initiatePayment} from "../../api/order.ts";
import { ElMessage } from "element-plus";

interface OrderDetail {
  orderId: string;
  userId: string;
  totalAmount: number;
  payMethod: string;
  status: string;
  createTime: string;
}

interface ProductItem {
  productId: string;
  productName: string;
  productImage: string;
  productQuantity: number;
  productPrice: number;
}

const router = useRouter();
const route = useRoute();
const orderId = route.params.orderId as string;

const orderDetail = ref<OrderDetail | null>(null);
const productList = ref<ProductItem[]>([]);
// const orderDetail = ref<OrderDetail | null>({
//   orderId: "1",
//   userId: "1",
//   totalAmount: 32,
//   payMethod: "ALIPAY",
//   status: "PENDING" ,
//   createTime: "2024-5-6"
// });
//
//
// const productList = ref<ProductItem[]>([
//   {
//     productId: "1",
//     productName: "软件工程计算",
//     productImage: "1.png",
//     productQuantity: 10,
//     productPrice: 10.8,
//   },
//   {
//     productId: "2",
//     productName: "软件工程计算2",
//     productImage: "2.png",
//     productQuantity: 20,
//     productPrice: 12.8,
//   }
// ]);


// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    const res = await getOrderDetial(orderId);
    if (res.data.code === '200') {
      orderDetail.value = res.data.data;
    } else {
      ElMessage.error(res.data.msg || "获取订单详情失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取订单详情失败");
  }
};

// 获取订单商品列表
const fetchProductList = async () => {
  try {
    const response = await getOrderProducts(orderId);
    if (response.data.code === '200') {
      productList.value = await Promise.all(
          response.data.data.map(async (item: any) => {
            return {
              productId: item.productId,
              productName: item.productName,
              productImage: item.productImage,
              productQuantity: item.productQuantity,
              productPrice: item.productPrice,
            };
          })
      );
    } else {
      ElMessage.error(response.data.msg || '获取商品列表失败啦');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取商品列表失败');
  }
};

// 取消订单
const handleCancelOrder = async () => {
  try {
    const res = await cancelOrder(orderId);
    if (res.data.code === '200') {
      ElMessage.success(res.data.data || "订单已取消");
      orderDetail.value!.status = "CANCELLED"; // 更新订单状态
    } else {
      ElMessage.error(res.data.msg || "取消订单失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("取消订单失败");
  }
};

// 发起支付
async function handlePayment() {
  try {
    const res = await initiatePayment(orderInfo.value.orderId); // 调用支付接口
    if (res.data.code === '200') {
      const paymentForm = res.data.data.paymentForm; // 获取支付表单HTML
      const paymentWindow = window.open('', '_blank'); // 打开新窗口
      paymentWindow?.document.write(paymentForm); // 渲染支付表单
    } else {
      ElMessage.error(res.data.msg || '支付失败');
    }
  } catch {
    ElMessage.error('支付失败');
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchOrderDetail();
  fetchProductList();
});

// 返回订单列表页面
const goBack = () => {
  router.push("/orderList");
};
</script>

<template>
  <div>
    <!-- Header -->
    <header style="background-color: darkorange; color: white; padding: 10px; display: flex; justify-content: space-between; align-items: center;">
      <span style="font-size: 24px; font-weight: bold; color: white;">TOMATO</span>
      <button @click="goBack" style="background: none; border: none; color: white; font-size: 16px; cursor: pointer;">返回</button>
    </header>

    <!-- 订单详情 -->
    <div style="margin: 20px auto; width: 84%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); padding: 20px; background: white;">
      <p>订单号: {{ orderDetail?.orderId }}</p>
      <p>支付方式: {{ orderDetail?.payMethod }}</p>
      <p>状态: {{ orderDetail?.status }}</p>
      <p>创建时间: {{ orderDetail?.createTime }}</p>
      <p>总金额: {{ orderDetail?.totalAmount }} 元</p>
    </div>

    <!-- 商品列表 -->
    <div style="margin: 20px auto; width: 84%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); padding: 20px; background: white;">
      <h3>商品列表</h3>
      <div v-for="product in productList" :key="product.productId" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding: 10px; border-bottom: 1px solid #ddd;">
        <img :src="product.productImage" alt="商品图片" style="width: 50px; height: 50px; object-fit: cover;" />
        <p>{{ product.productName }}</p>
        <p>数量: {{ product.productQuantity }}</p>
        <p>单价: {{ product.productPrice }} 元</p>
      </div>
    </div>

    <!-- 按钮区域 -->
    <div style="text-align: center; margin-top: 20px; display: flex; justify-content: center; gap: 20px;">
      <button
          @click="handleCancelOrder"
          :disabled="orderDetail?.status !== 'PENDING'"
          :style="{
          backgroundColor: orderDetail?.status === 'PENDING' ? 'darkorange' : '#c0c4cc',
          cursor: orderDetail?.status === 'PENDING' ? 'pointer' : 'not-allowed',
          color: 'white',
        }"
          style="border: none; padding: 10px 20px; font-size: 16px; border-radius: 5px;"
      >
        取消订单
      </button>
      <button
          @click="handlePayment"
          :disabled="orderDetail?.status !== 'PENDING'"
          :style="{
          backgroundColor: orderDetail?.status === 'PENDING' ? 'darkorange' : '#c0c4cc',
          cursor: orderDetail?.status === 'PENDING' ? 'pointer' : 'not-allowed',
          color: 'white',
        }"
          style="border: none; padding: 10px 20px; font-size: 16px; border-radius: 5px;"
      >
        发起支付
      </button>
    </div>
  </div>
</template>

<style scoped>

</style>
