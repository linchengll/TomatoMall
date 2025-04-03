<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { userInfo, userInfoUpdate } from '../api/user.ts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getListInfo } from '../api/Book/products.ts'

// === 侧边栏和搜索栏 ===============================
const search = ref("");
const categories = ref(["惊悚", "穿越", "科幻", "经典", "爱情", "励志"]);
// =================================================

// === 广告 ========================================
import banner1 from '../assets/banners1.jpg';
import banner2 from '../assets/banners2.jpg';
const banners = ref([banner1, banner2]);
const products = ref([
  { name: "三体", price: "149", image: null },
  { name: "红楼梦", price: "299", image: null }
]);
// =================================================

// === 商品列表 =====================================
interface Product {
  title: string;
  price: number;
  cover: string;
}
const productList = ref<Product[]>([]);

async function getProductList() {
  try {
    const res = await getListInfo();
    if (res.data.code === '200') {
      // 使用 map 只提取需要的字段
      productList.value = (res.data.result || []).map((item: any) => ({
        title: item.title,
        price: item.price,
        cover: item.cover?.trim() ? item.cover : "../assets/DefaultCover.png",
      }));
    } else {
      ElMessage.error(res.data.msg || "获取失败");
    }
  } catch (err) {
    console.error("加载商品列表失败", err);
    ElMessage.error("加载商品列表失败");
  }
}
// =================================================

// === 用户信息 ======================================
const username = ref(sessionStorage.getItem("username") || '')
const telephone = ref('')
const location = ref('')
const name = ref('')
const password = ref('')
const email = ref('')
const avatar = ref('')

function getUserInfo() {
  if (!username.value) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }
  userInfo(username.value).then(res => {
    username.value = res.data.data.username
    telephone.value = res.data.data.telephone || ''
    location.value = res.data.data.location || ''
    email.value = res.data.data.email || ''
    avatar.value = res.data.data.avatar || ''
  })
}
getUserInfo()
// =================================================


</script>

<template>
  <div class="homepage">
    <!-- 顶部搜索栏 -->
    <el-header class="header">
      <div class="logo">番茄书驿</div>
      <el-input v-model="search" placeholder="搜索商品..." class="search-box" />
      <el-button type="primary" icon="el-icon-search">搜索</el-button>
    </el-header>

    <el-container>
      <!-- 侧边分类导航 -->
      <el-aside width="200px" class="aside">
        <el-menu>
          <el-menu-item v-for="(item, index) in categories" :key="index">{{ item }}</el-menu-item>
        </el-menu>
      </el-aside>

      <el-main>
        <!-- 轮播图 -->
        <el-carousel height="300px">
          <el-carousel-item v-for="(image, index) in banners" :key="index">
            <img :src="image" class="banner-img" />
          </el-carousel-item>
        </el-carousel>

        <!-- 商品展示 -->
        <el-card v-for="(product, index) in productList"
                 :key="index"
                 class="product-card"
                 @click="$router.push(`/productDetail/${product.id}`)">
          <img :src="product.cover" class="product-img" />
          <div class="product-info">
            <p>{{ product.title }}</p>
            <p class="price">{{ product.price }}¥</p>
          </div>
        </el-card>
      </el-main>

      <!-- 用户信息面板 -->
      <el-aside width="250px" class="user-panel">
        <el-card class="user-card">
          <el-link @click="$router.push('/dashboard')" class="user-avatar-link">
            <el-avatar :src="avatar" class="user-avatar" />
          </el-link>
          <p class="welcome-text">欢迎：{{ username }}</p>
        </el-card>
      </el-aside>
    </el-container>
  </div>
</template>

<style>
.header {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #ff4400;
  color: white;
}
.logo {
  font-size: 24px;
  font-weight: bold;
  margin-right: 20px;
}
.search-box {
  flex: 1;
  margin-right: 10px;
}
.aside {
  background: #f5f5f5;
  padding: 10px;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
  margin-top: 20px;
}
.product-card {
  text-align: center;
}
.product-img {
  width: 100%;
  height: 180px;
  object-fit: cover
}
.user-panel {
  padding: 10px;
}
.user-card {
  text-align: center;
  padding: 20px;
  height: 270px;
}
.user-avatar-link {
  display: flex;
  justify-content: center;
  text-decoration: none;
}
.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  cursor: pointer;
}
.welcome-text {
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
}
</style>