<script setup lang="ts">
import { ref, reactive, onMounted} from 'vue'
import { getInfo } from '../../api/Book/products.ts'
import { useRoute } from "vue-router";

const route = useRoute();
const productId = route.params.id;

const title = ref("");
const price = ref(0);
const rate = ref(0);
const description = ref("");
const cover = ref("../src/assets/img.png");
const detail = ref("");
const specifications = ref<Set<Specification>>(new Set());

const fetchProductInfo = async (id: string) => {
  try {
    const res = await getInfo(id);
    console.log(res);
    if (res.data) {
      title.value = res.data.data.title;
      price.value = res.data.data.price;
      rate.value = res.data.data.rate;
      description.value = res.data.data.description;
      cover.value = res.data.data.cover?.trim() ? res.data.cover : "../src/assets/img.png";
      detail.value = res.data.data.detail?.trim() ? res.data.detail : "总而言之是本好书";
      specifications.value = res.data.data.specifications || [];
    }
  } catch (error) {
    console.error("获取商品信息失败", error);
  }
};
fetchProductInfo(productId);

// 购买数量
const quantity = ref(1);

// 加入购物车
const addToCart = () => {
  console.log(`加入购物车：${quantity.value} 件 ${product.title}`);
};

// 立即购买
const buyNow = () => {
  console.log(`立即购买：${quantity.value} 件 ${product.title}`);
};

</script>

<template>
  <div class="product-detail">
    <!-- 左侧：商品封面 -->
    <div class="product-image">
      <el-image :src = "cover"> </el-image>
    </div>

    <!-- 右侧：商品信息 -->
    <div class="product-info">
      <h2>{{ title }}</h2>
      <p class="price" v-if="price !== undefined && price !== null">￥{{ price.toFixed(2) }}</p>
      <p class="rate">评分: ⭐ {{ rate }}</p>
      <p class="desc">{{ description }}</p>

      <!-- 规格参数 -->
      <el-divider>规格参数</el-divider>
      <el-descriptions-item
          v-for="(spec, index) in specifications"
          :key="index"
          :label="spec.item"
      >
        {{ spec.value }}
      </el-descriptions-item>

      <!-- 购买操作 -->
      <div class="action-buttons">
        <el-input-number v-model="quantity" :min="1" :max="10" />
        <el-button type="danger" @click="buyNow">立即购买</el-button>
        <el-button type="primary" @click="addToCart">加入购物车</el-button>
        <router-link to="/changeInfo/${productId}">
          <el-button>去注册</el-button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  display: flex;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

product-image {
  width: 200px;
  height: 200px;
  left: 80px;
  top: 80px;
}

.product-image {
  flex: 1;
  max-width: 350px;
}

.product-info {
  flex: 2;
  padding-left: 20px;
}

.price {
  font-size: 24px;
  color: #e63946;
  font-weight: bold;
}

.rate {
  font-size: 16px;
  color: #ff9800;
}

.desc {
  margin: 10px 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
</style>

