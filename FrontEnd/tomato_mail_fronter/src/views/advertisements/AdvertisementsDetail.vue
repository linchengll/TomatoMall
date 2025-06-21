<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from "vue-router"
import { getInfo } from '../../api/Book/products'
import { getADVDetailInfo } from '../../api/Adv/advertisements'
import { addToCart } from '../../api/cart'
import NavBar from '../NavHead.vue'

// 获取路由参数
const route = useRoute();
const router = useRouter();
const advertisementId = route.params.id as string;

// 广告信息
const banner = ref({
  id: '',
  title: '',
  content: '',
  imageUrl: '',
  productId: '',
  discount: 0,
  limitNum: '',
});

// 商品信息
const product = ref<any>(null);
const quantity = ref(1);

// 获取广告详情
const fetchAdvInfo = async () => {
  try {
    const res = await getADVDetailInfo(advertisementId);
    if (res.data && res.data.data) {
      banner.value = res.data.data;
      await fetchProductInfo(banner.value.productId); // 获取对应商品信息
    } else {
      ElMessage.error("广告信息加载失败");
    }
  } catch (error) {
    ElMessage.error("请求广告信息出错");
    console.error(error);
  }
};

// 获取商品详情
const fetchProductInfo = async (id: string) => {
  try {
    const res = await getInfo(id);
    if (res.data && res.data.data) {
      product.value = res.data.data;
    } else {
      ElMessage.error("商品信息加载失败");
    }
  } catch (error) {
    ElMessage.error("请求商品信息出错");
    console.error(error);
  }
};

// 操作函数
const handleAddToCart = async () => {
  if (quantity.value > banner.value.limitNum) {
    ElMessage.error('超出购买限制');
  }
  try {
    const res = await addToCart(product.value.id, quantity.value, banner.value.discount);
    if (res.data.code === '200') {
      ElMessage.success('成功加入购物车');
    } else {
      ElMessage.error(res.data.msg || '加入购物车失败');
    }
  } catch {
    ElMessage.error('加入购物车失败');
  }
};


const goBackToMain = () => {
  router.push("/main");
};

const goToCart = () => {
  router.push("/cart");
};

onMounted(fetchAdvInfo);
</script>

<template>
  <div>
    <NavBar />

    <!-- 广告展示 -->
    <div class="ad-banner-detail">
      <el-card shadow="hover">
        <div class="ad-header">
          <h2>{{ banner.title }}</h2>
          <p class="ad-content">{{ banner.content }}</p>
        </div>
        <el-image :src="banner.imageUrl" class="ad-image" fit="cover" />
      </el-card>
    </div>

    <!-- 商品详情展示 -->
    <div class="product-detail" v-if="product">
      <div class="product-content">
        <!-- 左侧封面 -->
        <div class="product-image">
          <el-image :src="product.cover" fit="cover" />
        </div>

        <!-- 右侧信息 -->
        <div class="product-info">
          <h2>{{ product.title }}</h2>
          <p class="price">￥{{ product.price?.toFixed(2) }}</p>
          <p class="discount">
            折扣: <strong>{{ banner.discount*10 }} 折</strong>
          </p>
          <p class="limit">
            限购: <strong>{{ banner.limitNum }}</strong> 件
          </p>
          <p class="rate">评分: ⭐ {{ product.rate }}</p>
          <p class="desc">{{ product.description || product.detail }}</p>

          <div class="spec-section" v-if="product.specifications?.length">
            <div class="spec-title-bar">
              <h3 class="spec-title">📦 详情信息</h3>
            </div>
            <el-descriptions :column="2" border size="small" class="spec-table">
              <el-descriptions-item
                  v-for="(spec, index) in product.specifications"
                  :key="index"
                  :label="spec.item"
              >
                {{ spec.value }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="action-buttons">
            <el-input-number
                v-model="quantity"
                :min="1"
                :max="computedMax"
                :disabled="computedMax < 1"
            />
            <el-button type="primary" @click="handleAddToCart">加入购物车</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  margin: 30px;
}
.product-content {
  display: flex;
  gap: 40px;
  margin-top: 20px;
}
.product-image {
  width: 300px;
}
.product-info {
  flex: 1;
}
.ad-banner-detail {
  margin: 20px;
}
.ad-header {
  margin-bottom: 10px;
}
.ad-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
}
.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>