<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { userInfo } from "../../api/user.ts"; // 假设user.ts路径
import { getOrderItems } from "../../api/order.ts";
import {ElMessage} from "element-plus"; // 假设order.ts路径

// 定义 OrderItem 接口
interface OrderItem {
  orderId: string;
  userId: string;
  totalAmount: number;
  payMethod: string;
  status: string;
  createTime: string;
}

const router = useRouter();
const username = ref(sessionStorage.getItem("username"));
const userId = ref("");
const orders = ref<OrderItem[]>([]);

// const orders = ref<OrderItem[]>([
//   {
//     orderId: "1",
//     userId: "1",
//     totalAmount: 100.00,
//     payMethod: "ALIPAY",
//     status: "SUCCESS" ,
//     createTime: "2024-5-6"
//   },
//   {
//     orderId: "2",
//     userId: "1",
//     totalAmount: 100.00,
//     payMethod: "ALIPAY",
//     status: "SUCCESS" ,
//     createTime: "2024-4-6"
//   }
// ]);

// 获取用户信息
const fetchUserId = async () => {
  if (username.value) {
    const res = await userInfo(username.value);
    if (res.data && res.data.userId) {
      userId.value = res.data.userId;
    }
  }
};

// 获取订单信息
const fetchOrders = async () => {
  try {
    if(!userId.value) {
      console.error('No userId provided');
      return;
    }
    const response = await getOrderItems(userId.value);
    if (response.data.code === '200') {
      orders.value = await Promise.all(
          response.data.data.map(async (item: any) => {
            return {
              orderId: item.orderId,
              userId: item.userId,
              totalAmount: item.totalAmount,
              payMethod: item.payMethod,
              status: item.status,
              createTime: item.createTime,
            };
          })
      );
    } else {
      ElMessage.error(response.data.msg || '获取订单信息失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取订单失败');
  }
};

// 页面加载时获取数据
onMounted(async () => {
  await fetchUserId();
  await fetchOrders();
});

// 跳转到订单详情页面
const goToOrderDetail = (orderId: string) => {
  router.push(`/orderDetail/${orderId}`);
};

// 返回主页面
const goBack = () => {
  router.push("/main");
};
</script>

<template>
  <div>
    <!-- Header -->
    <header style="background-color: darkorange; color: white; padding: 10px; display: flex; justify-content: space-between; align-items: center;">
      <span style="font-size: 24px; font-weight: bold; color: white;">TOMATO</span>
      <button @click="goBack" style="background: none; border: none; color: white; font-size: 16px; cursor: pointer;">返回</button>
    </header>

    <!-- 订单列表 -->
    <div style="margin: 20px auto; width: 84%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); padding: 20px; background: white;">
      <div v-for="order in orders" :key="order.orderId" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding: 10px; border-bottom: 1px solid #ddd;">
        <div>
          <p>订单号: {{ order.orderId }}</p>
          <p>支付方式: {{ order.payMethod }}</p>
          <p>状态: {{ order.status }}</p>
          <p>创建时间: {{ order.createTime }}</p>
          <p>总金额: {{ order.totalAmount }} 元</p>
        </div>
        <button @click="goToOrderDetail(order.orderId)" style="background-color: darkorange; color: white; border: none; padding: 5px 10px; cursor: pointer;">查看详情</button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>