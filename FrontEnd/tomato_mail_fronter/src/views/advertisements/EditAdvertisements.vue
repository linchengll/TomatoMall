<script setup lang="ts">
import { ref } from 'vue'
import Header from '../ManagerHead.vue'
import { ElMessage } from 'element-plus'
import { addADVInfo, updateADVInfo, deleteADVInfo, getADVListInfo} from '../../api/Adv/advertisements'
import { searchList } from '../../api/Book/products.ts'
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
    const res = await searchList({
      searchString: "",
      type: 0
    });
    console.log("res", res);
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
getProductList();
// ================================================

// ==== 广告 ====================================
interface banner{
  id: string;
  title: string;
  content: string;
  imageUrl: string;
  productId: string;
}
const banners = ref<banner[]>([])
const newBanner = ref({
  id: '',
  title: '',
  content: '',
  imageUrl: '',
  productId: ''
})

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

// 打开添加
const openAddDialog = () => {
  isEdit.value = false
  newBanner.value = {
    id: '',
    title: '',
    content: '',
    imageUrl: '',
    productId: '',
    discount: '',
    limitNum: '',
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
const fileList = ref<UploadUserFile[]>([])
const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile);
  fileList.value = uploadFiles;
};

const handlePreview: UploadProps['onPreview'] = (file) => {
  console.log(file)
}

const handleAvatarUpload: UploadProps['onChange'] = async (uploadFile) => {
  if (uploadFile.raw) {
    try {
      const response = await imageInfoUpdate(uploadFile.raw);
      if (response.data.code === '200') {
        newBanner.value.imageUrl = response.data.data
        console.log(newBanner.value.imageUrl)
        ElMessage.success('图片上传成功！');
      } else {
        ElMessage({
          message: '图片上传失败！',
          type: 'error',
          center: true,
        });
      }
    } catch (error) {
      ElMessage.error('图片上传失败，请重试！');
      console.error('上传失败:', error);
    }
  }
};

</script>

<template>
  <Header />

  <div class="toolbar">
    <el-button type="primary" @click="openAddDialog">添加广告</el-button>
  </div>
  <div class="toolbar">
    <el-button type="primary" @click="$router.push('/main')">返回</el-button>
  </div>
  <el-row class="banner-list" gutter="20">
    <el-col :span="24" v-for="banner in banners" :key="banner.id" class="banner-item">
      <div class="banner-card">
        <div class="banner-image-container">
          <img :src="banner.imageUrl" class="banner-image" />
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
      <el-form-item label="折扣力度">
        <el-input-number
            v-model="newBanner.discount"
            :min="0"
            :max="0.99"
            :step="0.01"
            :precision="2"
            controls-position="right"
        />
      </el-form-item>

      <el-form-item label="限购个数">
        <el-input-number
            v-model="newBanner.limitNum"
            :min="1"
            :step="1"
            controls-position="right"
        />
      </el-form-item>
      <el-form-item label="广告图片">
        <el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            :on-remove="handleRemove"
            :on-preview="handlePreview"
            :on-change="handleAvatarUpload"
            :limit="1"
            list-type="picture"
            :auto-upload="false"
        >
          <el-button type="primary">点击上传</el-button>
        </el-upload>
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