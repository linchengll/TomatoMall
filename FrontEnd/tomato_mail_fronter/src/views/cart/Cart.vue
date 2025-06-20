<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getCartItems, updateCartItem, deleteCartItem, purchaseCartItems, createOrder } from '../../api/cart.ts';
import { initiatePayment } from '../../api/order.ts';

interface CartItem {
  cartItemId: string;
  productId: string;
  title: string;
  price: number;
  description: string;
  cover: string;
  detail: string;
  quantity: number;
  discount: number;
}

const cartItems = ref<CartItem[]>([]);

const selectedItems = ref<string[]>([]);
const totalAmount = ref(0);
const showPurchaseDialog = ref(false);
const shippingName = ref('');
const shippingPhone = ref('');
const shippingAddress = ref('');
const paymentMethod = ref('');

const showOrderDialog = ref(false);
const orderInfo = ref({
  orderId: '',
  totalAmount: 0,
  paymentMethod: '',
  createTime: '',
  status: '',
});

// 计算是否有选中的商品
const hasSelectedItems = computed(() => selectedItems.value.length > 0);

// 获取购物车商品
async function fetchCartItems() {
  try {
    const res = await getCartItems();
    if (res.data.code === '200') {
      cartItems.value = res.data.data.items.map((item: any) => ({
        cartItemId: item.cartItemId,
        productId: item.productId,
        title: item.title,
        price: item.price,
        description: item.description,
        cover: item.cover,
        detail: item.detail,
        quantity: item.quantity,
        discount: item.discount,
      }));
      calculateTotal();
    } else {
      ElMessage.error(res.data.msg || '加载购物车失败');
    }
  } catch {
    ElMessage.error('加载购物车失败');
  }
}

// 更新商品数量
async function updateQuantity(item: CartItem, newQuantity: number) {
  if (newQuantity < 1) {
    await removeItem(item.cartItemId);
    return;
  }
  try {
    const res = await updateCartItem(item.cartItemId, newQuantity);
    if (res.data.code === '200') {
      item.quantity = newQuantity;
      calculateTotal();
    } else {
      ElMessage.error(res.data.msg || '更新失败');
    }
  } catch {
    ElMessage.error('更新失败');
  }
}

// 删除商品
async function removeItem(cartItemId: string) {
  try {
    const res = await deleteCartItem(cartItemId);
    if (res.data.code === '200') {
      cartItems.value = cartItems.value.filter(item => item.cartItemId !== cartItemId);
      selectedItems.value = selectedItems.value.filter(id => id !== cartItemId);
      calculateTotal();
    } else {
      ElMessage.error(res.data.msg || '删除失败');
    }
  } catch {
    ElMessage.error('删除失败');
  }
}

// 计算总金额
function calculateTotal() {
  totalAmount.value = cartItems.value
      .filter(item => selectedItems.value.includes(item.cartItemId))
      .reduce((sum, item) => sum + item.price * item.quantity*item.discount, 0);
}

// 提交订单
async function submitOrder() {
  if (!shippingName.value || !shippingPhone.value || !shippingAddress.value) {
    ElMessage.error('请填写完整的收货信息');
    return;
  }
  try {
    const shippingInfo = {
      name: shippingName.value,
      phone: shippingPhone.value,
      address: shippingAddress.value,
    };
    const res = await createOrder(selectedItems.value, shippingInfo, paymentMethod.value);
    if (res.data.code === '200') {
      ElMessage.success('订单提交成功');
      showPurchaseDialog.value = false;
      orderInfo.value = res.data.data; // 保存订单信息
      showOrderDialog.value = true; // 显示订单弹窗
    } else {
      ElMessage.error(res.data.msg || '提交失败');
    }
  } catch (error) {
    ElMessage.error('提交失败');
    console.error(error);
  }
}

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

fetchCartItems();
</script>

<template>
  <div class="cart-page">
    <!-- Header -->
    <div class="header">
      <div class="header-left">TOMATO</div>
      <div class="header-center">购物车</div>
      <el-button class="header-right" type="primary" @click="$router.push('/main')">返回</el-button>
    </div>

    <!-- 商品列表框 -->
    <div class="cart-container">
      <div class="cart-item" v-for="item in cartItems" :key="item.cartItemId">
        <div class="item-name">{{ item.title }}</div>
        <div class="item-price">{{ item.price*item.discount }}¥</div>
        <el-input-number
            class="item-quantity"
            v-model="item.quantity"
            :min="1"
            @change="updateQuantity(item, $event)"
        />
        <el-button class="item-delete" type="danger" icon="el-icon-delete" @click="removeItem(item.cartItemId)">删除</el-button>
        <el-checkbox class="item-select" v-model="selectedItems" :label="item.cartItemId" @change="calculateTotal"></el-checkbox>
      </div>
    </div>

    <!-- 底部购买按钮 -->
    <div class="cart-footer">
      <el-button
          type="primary"
          @click="showPurchaseDialog = true"
          :disabled="!hasSelectedItems"
      >
        购买
      </el-button>
    </div>

    <!-- 购买弹窗 -->
    <el-dialog v-model="showPurchaseDialog" title="确认购买" width="50%">
      <p class="dialog-total">总金额：{{ totalAmount }}¥</p>
      <el-input v-model="shippingName" placeholder="收货人姓名" class="dialog-input" />
      <el-input v-model="shippingPhone" placeholder="收货人电话" class="dialog-input" />
      <el-input v-model="shippingAddress" placeholder="收货地址" class="dialog-input" />
      <el-select v-model="paymentMethod" placeholder="选择支付方式" class="dialog-input">
        <el-option label="支付宝" value="ALIPAY" />
      </el-select>
      <div class="dialog-footer">
        <el-button @click="showPurchaseDialog = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </div>
    </el-dialog>

    <!-- 订单信息弹窗 -->
    <el-dialog v-model="showOrderDialog" title="订单信息" width="50%">
      <p>订单号：{{ orderInfo.orderId }}</p>
      <p>总金额：{{ orderInfo.totalAmount }}¥</p>
      <p>支付方式：{{ orderInfo.paymentMethod }}</p>
      <p>创建时间：{{ orderInfo.createTime }}</p>
      <p>订单状态：{{ orderInfo.status }}</p>
      <div class="dialog-footer">
        <el-button @click="showOrderDialog = false">关闭</el-button>
        <el-button type="primary" @click="handlePayment">支付</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.cart-page {
  background-color: #ffe5cc;
  min-height: 100vh;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ff8c00;
  padding: 10px 20px;
  color: white;
  font-size: 20px;
  font-weight: bold;
  border-radius: 8px;
}

.header-left {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.header-center {
  flex: 1;
  text-align: center;
}

.header-right {
  background-color: white;
  color: #ff8c00;
  border: none;
}

.cart-container {
  background-color: white;
  margin: 20px auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 84%;
}

.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
  gap: 20px; /* 增加子元素之间的间距 */
}

.item-name {
  flex: 2;
  font-size: 16px;
}

.item-price {
  flex: 1;
  text-align: center;
  color: #fa0056;
  font-weight: bold;
}

.item-quantity {
  flex: 1;
  text-align: center;
  margin-right: 10px; /* 添加右侧间距 */
}

.item-delete {
  flex: 1;
  text-align: center;
  margin-right: 10px; /* 添加右侧间距 */
}

.item-select {
  flex: 1;
  text-align: center;
}

.cart-footer {
  text-align: center;
  margin-top: 20px;
}

.cart-footer .el-button {
  width: 200px;
  height: 40px;
  font-size: 16px;
}

.cart-footer .el-button.is-disabled {
  background-color: #c0c4cc;
  border-color: #c0c4cc;
  color: #fff;
}

.el-dialog {
  border-radius: 8px;
}

.dialog-total {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
}

.dialog-input {
  margin-bottom: 20px;
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.dialog-footer .el-button {
  width: 120px;
}
</style>