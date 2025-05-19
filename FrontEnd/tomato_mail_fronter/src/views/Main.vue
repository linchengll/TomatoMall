<script setup lang="ts">
import { ref, computed} from 'vue'
import { userInfo } from '../api/user.ts'
import { ElMessage } from 'element-plus'
import { getListInfo, addTypeInfo, deleteTypeInfo, getTypeListInfo} from '../api/Book/products.ts'
import { getADVListInfo} from '../api/Adv/advertisements'
import NavBar from '../views/NavHead.vue'


// === 侧边栏和搜索栏 ===============================
const isExpanded = ref(false)
const selectedCategory = ref("")
const showAddCategoryDialog = ref(false)
const newCategoryName = ref("")
const categories = ref<string[]>([])

// 控制分类显示的数量（如默认显示6个）
const visibleCategories = computed(() => {
  return isExpanded.value ? categories.value : categories.value.slice(0, 6)
})

// 切换“展开/收起”
function toggleExpand() {
  isExpanded.value = !isExpanded.value
}

// 获取分类列表（页面加载时调用）
async function fetchCategories() {
  try {
    const res = await getTypeListInfo()
    categories.value = res.data.data.map((item: any) => item.typeName) // 假设后端字段为 typeName
  } catch (err) {
    ElMessage.error("获取分类失败")
  }
}

// 添加新分类（调用接口）
const confirmAddCategory = async () => {
  const name = newCategoryName.value.trim()
  if (!name) {
    ElMessage.error("分类名称不能为空")
    return
  }

  try {
    const payload = JSON.stringify({ typeName: name })
    const response = await addTypeInfo(payload)

    if (response.data.code === '200') {
      ElMessage.success("添加成功")
      showAddCategoryDialog.value = false
      newCategoryName.value = ""
      fetchCategories() // 重新加载分类列表
    } else if (response.data.code === '400') {
      ElMessage.error(response.data.msg || "添加失败")
    }
  } catch (error) {
    console.error("Error adding category:", error)
    ElMessage.error("添加失败，请检查网络")
  }
}

// 分类选择逻辑（可用于筛选商品）
function selectCategory(category: string) {
  selectedCategory.value = category
  // 发起筛选逻辑，比如 emit("filter", category) 或调用 API
}
// =================================================


// === 广告 ========================================
interface banner{
  id: string;
  title: string;
  content: string;
  imageUrl: string;
  productId: string;
}
const banners = ref<banner[]>([])

// 加载广告列表
async function getBannersList() {
  try {
    const res = await getADVListInfo();
    if (res.data.code === '200') {
      // 使用 map 只提取需要的字段
      banners.value =( res.data.data || [] ).map((item: any) => ({
        id: item.id,
        title: item.title,
        content: item.content,
        imageUrl: item.imageUrl? item.imageUrl : "../assets/DefaultBanner.png",
        productId: item.productId
      }));
    } else {
      ElMessage.error(res.data.msg || "获取广告失败");
    }
  } catch (err) {
    ElMessage.error("加载广告列表失败");
  }
}
getBannersList();
// =================================================

// === 商品列表 =====================================
interface Product {
  title: string;
  price: number;
  cover: string;
  id: string;
}
const productList = ref<Product[]>([]);

async function getProductList() {
  try {
    const res = await getListInfo();
    if (res.data.code === '200') {
      // 使用 map 只提取需要的字段
      productList.value = (res.data.data || []).map((item: any) => ({
        title: item.title,
        price: item.price,
        id: item.id,
        cover: item.cover? item.cover : "../assets/DefaultCover.png",
      }));
    } else {
      ElMessage.error(res.data.msg || "获取失败");
    }
  } catch (err) {
    ElMessage.error("加载商品列表失败");
  }
}
getProductList();
// =================================================

// === 用户信息 ======================================
const username = ref(sessionStorage.getItem("username") || '')
const telephone = ref('')
const location = ref('')
const name = ref('')
const password = ref('')
const email = ref('')
const avatar = ref('')
const role = ref('')

function getUserInfo() {
  if (!username.value) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }
  userInfo(username.value).then(res => {
    username.value = res.data.data.username
    role.value = res.data.data.role
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
    <NavBar v-model="search" />

    <el-container>
      <!-- 侧边分类导航 -->
      <el-aside width="200px" class="aside">
        <!-- 管理员专属的添加分类按钮 -->
        <div v-if="role === 'admin'" style="margin-bottom: 10px; display: flex; justify-content: center;">
          <el-button type="primary" icon="el-icon-plus" @click="showAddCategoryDialog = true">添加分类</el-button>
        </div>

        <el-menu>
          <!-- 展示前若干个分类 -->
          <el-menu-item
              v-for="(item, index) in visibleCategories"
              :key="index"
              :class="{ 'active-category': item === selectedCategory }"
              @click="selectCategory(item)">
            {{ item }}
          </el-menu-item>

          <!-- 展开/收起按钮 -->
          <el-menu-item @click="toggleExpand" style="text-align: center;">
            <span>{{ isExpanded ? '收起' : '展开' }} »</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main>
        <!-- 轮播图 -->
        <el-carousel height="300px">
          <el-carousel-item v-for="(banner, index) in banners" :key="index">
            <img :src="banner.imageUrl" class="banner-img" />
            <div class="banner-caption">
              <h3>{{ banner.title }}</h3>
              <p>{{ banner.content }}</p>
            </div>
          </el-carousel-item>
        </el-carousel>

        <!-- 商品展示 -->
        <div class="product-list">
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
        </div>
      </el-main>

      <!-- 用户信息面板 -->
      <el-aside width="250px" class="user-panel">
        <el-card class="user-card">
          <el-link @click="$router.push('/dashboard')" class="user-avatar-link">
            <el-avatar :src="avatar" class="user-avatar" />
          </el-link>
          <p class="welcome-text">欢迎：{{ username }}</p>
          <el-space direction="vertical" size="large" alignment="center">
            <el-button type="primary" plain icon="el-icon-edit" @click="$router.push('/createProduct')" v-if="role === 'admin'">
              创建书籍
            </el-button>
            <el-button type="warning" plain icon="el-icon-picture" @click="$router.push('/editAdvertisements')">
              编辑广告
            </el-button>
            <el-button type="success" plain icon="el-icon-shopping-cart-full" @click="$router.push('/cart')">
              前往购物车
            </el-button>
          </el-space>
        </el-card>
      </el-aside>
    </el-container>
  </div>

  <!-- 添加广告的弹窗 -->
  <el-dialog v-model="showAddDialog" title="添加广告" width="500px">
    <el-form :model="newBanner" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="newBanner.title" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" v-model="newBanner.content" />
      </el-form-item>
      <el-form-item label="广告图片" prop="image">
        <el-form-item label="头像">
          <img :src="newBanner" class="image" />
          <input type="file" accept="image/*" @change="handleAvatarUpload" />
        </el-form-item>
      </el-form-item>
      <el-form-item label="商品ID">
        <el-select v-model="newBanner.productId" placeholder="选择关联商品">
          <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="confirmAddBanner">确认添加</el-button>
    </template>
  </el-dialog>

  <!-- 添加分类栏的弹窗 -->
  <el-dialog v-model="showAddCategoryDialog" title="创建新分类" width="400px">
    <el-form label-width="80px">
      <el-form-item label="分类名">
        <el-input v-model="newCategoryName" placeholder="请输入新分类名" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showAddCategoryDialog = false">取消</el-button>
      <el-button type="primary" @click="confirmAddCategory">确认</el-button>
    </template>
  </el-dialog>
</template>

<style>
.banner-caption {
  position: absolute;
  bottom: 20px;
  left: 30px;
  color: white;
  text-shadow: 0 0 5px black;
}

.el-button {
  width: 160px;
  justify-content: center !important;
  display: flex;
  align-items: center;
}

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
.product-list {
  display: grid;
  grid-template-columns: repeat(6, 1fr); /* 一行六列 */
  gap: 20px; /* 卡片间距 */
  padding: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.2s;
  text-align: center;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 6px;
}

.product-info {
  margin-top: 10px;
}

.price {
  color: #fa0056;
  font-weight: bold;
}
.user-panel {
  padding: 10px;
}
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  height: 300px;
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
  text-align: center;
}
</style>