<script setup>
import { ref, reactive, onMounted} from 'vue'
import { useRoute } from "vue-router";

const route = useRoute();
const productId = route.params.id;

// 商品数据
const product = reactive({
  id: "102",
  title: "三体",
  price: 59.0,
  rate: 9.2,
  description: "软件工程领域的经典著作",
  cover: "../src/assets/img.png",
  detail: "本书提出一种观念：代码质量与其整洁度成正比",
  specifications: [
    { id: "1008", item: "作者", value: "Robert C. Martin" },
    { id: "1009", item: "副标题", value: "程序员的职业素养" },
    { id: "1010", item: "ISBN", value: "9787121316633" },
    { id: "1011", item: "装帧", value: "精装" },
    { id: "1012", item: "页数", value: "388" },
    { id: "1013", item: "出版社", value: "人民邮电出版社" },
    { id: "1014", item: "出版日期", value: "2018-01-01" },
  ],
});

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
    if (res.data) {
      title.value = res.data.title;
      price.value = res.data.price;
      rate.value = res.data.rate;
      description.value = res.data.description;
      cover.value = res.data.cover?.trim() ? res.data.cover : "../src/assets/img.png";
      detail.value = res.data.detail?.trim() ? res.data.detail : "总而言之是本好书";
      specifications.value = new Set(res.data.specifications);
    }
  } catch (error) {
    console.error("获取商品信息失败", error);
  }
};

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

onMounted(() => {
  fetchProductInfo(product.id);
});
</script>

<template>
  <div class="product-detail">
    <!-- 左侧：商品封面 -->
    <div class="product-image">
      <el-image :src = "cover"> </el-image>
    </div>

    <!-- 右侧：商品信息 -->
    <div class="product-info">
      <h2>{{ product.title }}</h2>
      <p class="price">￥{{ product.price.toFixed(2) }}</p>
      <p class="rate">评分: ⭐ {{ product.rate }}</p>
      <p class="desc">{{ product.description }}</p>

      <!-- 规格参数 -->
      <el-divider>规格参数</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item
            v-for="spec in product.specifications"
            :key="spec.id"
            :label="spec.item"
        >
          {{ spec.value }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 购买操作 -->
      <div class="action-buttons">
        <el-input-number v-model="quantity" :min="1" :max="10" />
        <el-button type="danger" @click="buyNow">立即购买</el-button>
        <el-button type="primary" @click="addToCart">加入购物车</el-button>
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

