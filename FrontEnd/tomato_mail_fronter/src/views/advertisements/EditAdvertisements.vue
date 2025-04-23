<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import Header from '../ManagerHead.vue'
import { ElMessage } from 'element-plus'
import { addADVInfo, updateADVInfo, deleteADVInfo, getADVListInfo} from '../../api/Adv/advertisements'
import { getListInfo } from '../../api/Book/products.ts'
import { imageInfoUpdate } from '../../api/tools.ts'

// 状态
const showAddDialog = ref(false)
const isEdit = ref(false)

// ==== 商品 ====================================
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
// ================================================

// ==== 广告 ====================================
const banners = ref<any[]>([])
const newBanner = ref({
  id: '',
  title: '',
  content: '',
  imgUrl: '',
  productId: ''
})

// 加载广告列表
async function getBannersList() {
  try {
    const res = await getADVListInfo();
    if (res.data.code === '200') {
      // 使用 map 只提取需要的字段
      banners.value = res.data.data || [];
    } else {
      ElMessage.error(res.data.msg || "获取广告失败");
    }
  } catch (err) {
    ElMessage.error("加载广告列表失败");
  }
}
getBannersList();

// 打开添加
const openAddDialog = () => {
  isEdit.value = false
  newBanner.value = {
    id: '',
    title: '',
    content: '',
    imgUrl: '',
    productId: ''
  }
  showAddDialog.value = true
}

// 打开编辑
const openEditDialog = (banner: any) => {
  isEdit.value = true
  newBanner.value = { ...banner }
  showAddDialog.value = true
}

// 确认添加或修改
const confirmBanner = async () => {
  try {
    if (isEdit.value) {
      await updateADVInfo(newBanner.value)
      ElMessage.success('广告更新成功')
    } else {
      console.log(newBanner.value)
      await addADVInfo(newBanner.value)
      ElMessage.success('广告添加成功')
    }
    showAddDialog.value = false
    await getADVListInfo()
  } catch (err) {
    ElMessage.error('操作失败，请重试')
  }
}

// 删除广告
const deleteBanner = async (id: string) => {
  await deleteADVInfo(id)
  ElMessage.success('删除成功')
  await getADVListInfo()
}
// ========================================================

// 图片上传
const handleAvatarUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  const file = input.files[0]
  try {
    const response = await imageInfoUpdate(file)
    if (response.data.code === '200') {
      newBanner.value.imgUrl = response.data.data
      await nextTick()
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    ElMessage.error('图片上传失败，请重试')
    console.error('上传失败:', error)
  }
}

// 加载广告
const fetchBanners = async () => {
  // 这里应接接口，例如：const res = await getADVList();
  // banners.value = res.data;
  banners.value = [ // mock 数据
    {
      id: '1',
      title: '广告标题1',
      content: '广告描述内容1',
      imgUrl: 'https://via.placeholder.com/400x100',
      productId: '1'
    }
  ]
}

onMounted(() => {
  fetchBanners()
})
</script>

<template>
  <Header />

  <div class="toolbar">
    <el-button type="primary" @click="openAddDialog">添加广告</el-button>
  </div>

  <el-row class="banner-list" gutter="20">
    <el-col :span="24" v-for="banner in banners" :key="banner.id" class="banner-item">
      <div class="banner-card">
        <div class="banner-image-container">
          <img :src="banner.imgUrl" class="banner-image" />
          <div class="banner-overlay">
            <p class="banner-description">{{ banner.content }}</p>
          </div>
        </div>
        <div class="banner-meta">
          <h3>{{ banner.title }}</h3>
          <div class="banner-actions">
            <el-button type="primary" size="small" @click="openEditDialog(banner)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteBanner(banner.id)">删除</el-button>
          </div>
        </div>
      </div>
    </el-col>
  </el-row>

  <!-- 添加/编辑广告弹窗 -->
  <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑广告' : '添加广告'" width="500px">
    <el-form :model="newBanner" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="newBanner.title" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" v-model="newBanner.content" />
      </el-form-item>
      <el-form-item label="广告图片">
        <img :src="newBanner.imgUrl" class="image" />
        <input type="file" accept="image/*" @change="handleAvatarUpload" />
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
      <el-button @click="showAddDialog = false">取消</el-button>
      <el-button type="primary" @click="confirmBanner">{{ isEdit ? '确认修改' : '确认添加' }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.banner-list {
  margin: 16px;
}

.banner-item {
  margin-bottom: 20px;
}

.banner-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.banner-image-container {
  position: relative;
  width: 400px;
  height: 100px;
  flex-shrink: 0;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4); /* 半透明遮罩 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-description {
  color: #fff;
  font-size: 14px;
  padding: 0 12px;
  text-align: center;
}

.banner-meta {
  flex: 1;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.banner-actions {
  display: flex;
  gap: 10px;
}
</style>