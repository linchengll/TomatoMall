<script setup lang="ts">
import { ref, computed} from 'vue'
import { userInfo } from '../api/user.ts'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { addTypeInfo, deleteTypeInfo, getTypeListInfo, getTopList, searchList} from '../api/Book/products.ts'
import { useRoute, useRouter } from "vue-router";
import 'element-plus/dist/index.css'


const route = useRoute()
const router = useRouter()

// === 搜索的选择状态集合 ======================
const selectedCategory = ref("")
const currentKeyword = route.params.keyword as string;
// =========================================

// === 侧边栏 ===============================
// 状态定义
const isExpanded = ref(false)
let deleteType = ref(false)

const showAddCategoryDialog = ref(false)
const newCategoryName = ref("")
const categoryList = ref<{ typeId: number; typeName: string }[]>([])
const categories = ref<string[]>([])

// 用来排序分类（按字母）
const sortedCategories = computed(() => {
  return [...categories.value].sort((a, b) => a.localeCompare(b))
})

// 控制分类显示的数量（默认显示12个或全部）
const visibleCategories = computed(() => {
  return isExpanded.value ? sortedCategories.value : sortedCategories.value.slice(0, 12)
})

// 切换“展开/收起”
function toggleExpand() {
  isExpanded.value = !isExpanded.value
}

function getTypeIdByName(typeName: string): number | string {
  const match = categoryList.value.find(item => item.typeName === typeName);
  return match ? match.typeId : "";
}
// 分类选择逻辑（可用于筛选商品）
function selectCategory(category: string) {
  if (!deleteType.value) {
    if (selectedCategory.value === category) {
      selectedCategory.value = "" // 点击已选中项时取消选择
      getProductList(currentKeyword, 0)
    } else {
      selectedCategory.value = category
      const typeId = getTypeIdByName(category)
      getProductList(currentKeyword, typeId)
    }
  } else {
    const typeId = getTypeIdByName(category)
    console.log(typeId)
    ElMessageBox.confirm(
        `确定要删除分类 “${category}” 吗？此操作不可恢复！`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    ).then(() => {
      deleteTypeInfo(typeId).then(res => {
        if (res.data.code === '200') {
          ElMessage.success('分类删除成功！');
          // 可以刷新分类列表或重置选中项等逻辑
          selectedCategory.value = ""
          deleteType = false
          fetchCategories()
        } else {
          ElMessage.error(res.data.msg || '删除失败');
        }
      }).catch(error => {
        ElMessage.error('删除失败: ' + error.message);
        console.error('删除失败:', error);
      })
    }).catch(() => {
      ElMessage.info('已取消删除')
    })
  }
}

// 获取分类列表（页面加载时调用）
async function fetchCategories() {
  try {
    const res = await getTypeListInfo()
    categoryList.value = res.data.data.map((item: any) => ({
      typeId: item.typeId,
      typeName: item.typeName
    }))
    categories.value = res.data.data.map((item: any) => item.typeName)
  } catch (err) {
    ElMessage.error("获取分类失败")
  }
}
fetchCategories();

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
// =================================================

// === 搜索 ========================================
const search = ref('');
const showTopList = ref(false);
const topList = ref<string[]>([]);
const isFocused = ref(false);

// 点击搜索按钮
function onSearchClick() {
  const keyword = search.value.trim();
  const typeId = selectedCategory.value
      ? getTypeIdByName(selectedCategory.value)
      : 0;
  router.push(`/searchMain/${keyword}`)
}

// 聚焦时显示 top5 热榜 + 展开动画
function onSearchFocus() {
  isFocused.value = true;
  getTopList().then((res) => {
    topList.value = Array.isArray(res.data) ? res.data.slice(0, 5) : [];
  }).catch(() => {
    topList.value = []; // 请求失败时设为空数组
  });
  console.log("topList", topList);
  showTopList.value = true;
}

// 失焦时隐藏热门列表（延迟 200ms 避免点击被关闭）
function onSearchBlur() {
  setTimeout(() => {
    isFocused.value = false;
    showTopList.value = false;
  }, 200);
}

// 选中热词
function onHotItemClick(item: string) {
  search.value = item;
  onSearchClick();
  showTopList.value = false;
}
// ================================================

// === 商品列表 =====================================
interface Product {
  title: string;
  price: number;
  cover: string;
  id: string;
}
const productList = ref<Product[]>([]);

async function getProductList(searchString: string, type: number) {
  try {
    const res = await searchList({
      searchString: searchString,
      type: type
    });
    if (res.data.code === '200') {
      const list = res.data.data.productList || [];
      productList.value = list.map((item: any) => ({
        title: item.title,
        price: item.price,
        id: item.id,
        cover: item.cover || "../assets/DefaultCover.png",
      }));
    } else {
      ElMessage.error(res.data.msg || "获取失败");
    }
  } catch (err) {
    ElMessage.error("加载商品列表失败");
  }
}
getProductList("",0);
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
    <el-header class="header">
      <div class="logo">番茄书驿</div>

      <div class="search-container" style="position: relative">
        <div class="search-box-wrapper" style="display: flex; width: 100%">
          <!-- el-input -->
          <div style="position: relative; flex-grow: 1">
            <el-input
                v-model="search"
                placeholder="搜索商品..."
                :class="{ 'expanded-search': showTopList }"
                @focus="onSearchFocus"
                @blur="onSearchBlur"
                style="
              width: 100%;
              transition: all 0.2s ease;
            "
            >
              <template #prefix>
                <el-icon><search /></el-icon>
              </template>
            </el-input>

            <!-- 无缝下拉菜单 -->
            <transition name="dropdown-fade">
              <div
                  v-show="showTopList"
                  class="top-list"
                  style="
                position: absolute;
                top: 100%;
                left: 0;
                width: 100%;
                margin-top: -1px;
                border-radius: 0 0 8px 8px;
              "
              >
                <ul v-if="topList.length > 0">
                  <li
                      v-for="(item, index) in topList"
                      :key="index"
                      @mousedown="onHotItemClick(item)"
                  >
                    {{ item }}
                  </li>
                </ul>
                <div v-else class="empty-placeholder">
                  暂无数据
                </div>
              </div>
            </transition>
          </div>

          <el-button
              type="primary"
              icon="el-icon-search"
              @click="onSearchClick"
              style="margin-left: 5px"
          >
            搜索
          </el-button>
          <el-button type="primary" icon="el-icon-house" @click="$router.push('/main')">首页</el-button>
        </div>
      </div>
    </el-header>

    <el-container>
      <el-aside width="200px" class="aside">
        <div v-if="role === 'admin'" class="add-category">
          <el-button type="primary" icon="el-icon-plus" @click="showAddCategoryDialog = true">添加分类</el-button>
          <el-button type="danger" icon="el-icon-plus" v-if="deleteType === false" @click="deleteType = true">删除分类</el-button>
          <el-button type="danger" icon="el-icon-plus" v-if="deleteType === true" @click="deleteType = false">取消删除</el-button>
        </div>

        <!-- 分类列表容器，带滚动 -->
        <div class="scrollable-category">
          <el-menu class="category-menu">
            <el-menu-item
                v-if="selectedCategory"
                key="selected"
                class="active-category"
                @click="selectCategory(selectedCategory)"
            >
              {{ selectedCategory }}
              <el-icon
                  v-if="deleteType"
                  class="delete-icon"
                  @click.stop="deleteCategory(selectedCategory)"
              >
                <CircleCloseFilled />
              </el-icon>
            </el-menu-item>

            <el-menu-item
                v-for="item in visibleCategories"
                :key="item"
                :class="{ 'active-category': item === selectedCategory }"
                @click="selectCategory(item)"
            >
              {{ item }}
              <el-icon
                  v-if="deleteType"
                  class="delete-icon"
                  @click.stop="deleteCategory(item)"
              >
                <CircleCloseFilled />
              </el-icon>
            </el-menu-item>

            <el-menu-item @click="toggleExpand">
              <span>{{ isExpanded ? '收起 «' : '展开更多 »' }}</span>
            </el-menu-item>
          </el-menu>
        </div>

      </el-aside>

      <el-main>
        <!-- 商品展示 -->
        <div class="product-list">
          <el-card v-for="product in productList"
                   :key="product.id"
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
    </el-container>
  </div>

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

.aside {
  position: relative;
  background: #f5f7fa;
  height: 100vh;
  overflow: hidden;
}

.scrollable-category {
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  padding-right: 4px;
}

.category-menu {
  border-right: none;
}

.active-category {
  background-color: #ecf5ff !important;
  color: #409eff;
  font-weight: 600;
}

.delete-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  cursor: pointer;
  font-size: 16px;
}

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
.search-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow: visible !important; /* 禁止裁剪 */
}

.search-box-wrapper {
  display: flex;
  align-items: center;
  width: 600px;
}

.search-box {
  flex: 1;
}

.top-list {
  display: block; /* 强制显示 */
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: white;
  border: 1px solid #dcdfe6;
  z-index: 1000; /* 确保在最上层 */
  opacity: 1; /* 强制不透明 */
}
/* 修改el-input的圆角 */
:deep(.el-input__wrapper) {
  border-radius: 20px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

/* 展开状态下的输入框 */
.expanded-search :deep(.el-input__wrapper) {
  border-radius: 20px 20px 0 0 !important;
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
}

/* 下拉菜单样式 */
.top-list {
  background: white;
  border: 1px solid #dcdfe6;
  border-top: none;
  height: 100px;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.top-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.top-list li {
  padding: 10px 15px;
  cursor: pointer;
}

.top-list li:hover {
  background-color: #f5f5f5;
}

.empty-placeholder {
  padding: 10px;
  color: #999;
  text-align: center;
}

/* 动画效果 */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.3s ease;
}
.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>