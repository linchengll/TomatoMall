<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInfo, deleteInfo } from '../../api/Book/products.ts'
import { useRoute, useRouter } from "vue-router";
import { userInfo } from '../../api/user.ts'

const route = useRoute();
const router = useRouter();
const productId = route.params.id as string;

const title = ref("");
const price = ref(0);
const rate = ref(0);
const description = ref("");
const cover = ref("");
const detail = ref("");

const specificationsTableData = ref<Array<{ id: number, item: string, value: string, productId: number }>>([]);

const fetchProductInfo = async (id: string) => {
  try {
    const res = await getInfo(id);
    if (res.data && res.data.data) {
      const data = res.data.data;

      title.value = data.title ?? '';
      price.value = data.price ?? 0;
      rate.value = data.rate ?? 0;
      description.value = data.description ?? '';
      detail.value = data.detail ?? '';

      // 修复 cover 获取路径错误
      cover.value = data.cover?.trim() ? data.cover : "../src/assets/img.png";

      // 直接赋值数组即可，不需要转 Set
      if (Array.isArray(data.specifications)) {
        specificationsTableData.value = data.specifications;
      } else {
        ElMessage.error("后端返回的 specifications 格式不正确！");
      }
    }
  } catch (error) {
    console.error("获取商品信息失败", error);
  }
};

//*******获取用户职能************************************************
const username = ref(sessionStorage.getItem("username") || '')
const role = ref("")
function getUserInfo() {
  if (!username.value) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }
  userInfo(username.value).then(res => {
    role.value = res.data.data.role
  })
}
//*****************************************************************

onMounted(() => {
  fetchProductInfo(productId);
  getUserInfo()
});

// 购买数量
const quantity = ref(1);

// 加入购物车
const addToCart = () => {
  console.log(`加入购物车：${quantity.value} 件 ${title.value}`);
};

// 立即购买
const buyNow = () => {
  console.log(`立即购买：${quantity.value} 件 ${title.value}`);
};

// 返回主页
const goBackToMain = () => {
  router.push('/main');
};

// 删除商品
const handleDeleteProduct = () => {
  ElMessageBox.confirm(
      '确定要删除这个商品吗？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    deleteInfo(productId).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('商品删除成功！');
        router.push('/main');
      } else {
        ElMessage.error(res.data.msg || '删除失败');
      }
    }).catch(error => {
      ElMessage.error('删除失败: ' + error.message);
      console.error('删除失败:', error);
    });
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};
</script>

<template>
  <div class="product-detail">
    <!-- 操作按钮区域 -->
    <div class="action-buttons-top">
      <el-button type="info" plain @click="goBackToMain">返回主页</el-button>
      <el-button
          v-if="role === 'admin'"
          type="danger"
          plain
          @click="handleDeleteProduct"
      >
        删除商品
      </el-button>
    </div>

    <!-- 商品详情内容 -->
    <div class="product-content">
      <!-- 左侧：商品封面 -->
      <div class="product-image">
        <el-image :src="cover"></el-image>
      </div>

      <!-- 右侧：商品信息 -->
      <div class="product-info">
        <h2>{{ title }}</h2>
        <p class="price" v-if="price !== undefined && price !== null">￥{{ price.toFixed(2) }}</p>
        <p class="rate">评分: ⭐ {{ rate }}</p>
        <p class="desc">{{ description }}</p>

        <div class="spec-section">
          <div class="spec-title-bar">
            <h3 class="spec-title">📦 详情信息</h3>
            <el-button
                v-if="role === 'admin'"
                type="primary"
                size="small"
                @click="$router.push(`/changeInfo/${productId}`)"
            >
              修改信息
            </el-button>
          </div>
          <el-descriptions :column="2" border size="small" class="spec-table">
            <el-descriptions-item
                v-for="(spec, index) in specificationsTableData"
                :key="index"
                :label="spec.item"
            >
              {{ spec.value }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 购买操作 -->
        <div class="action-buttons">
          <el-input-number v-model="quantity" :min="1" :max="10" />
          <el-button type="danger" @click="buyNow">立即购买</el-button>
          <el-button type="primary" @click="addToCart">加入购物车</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.action-buttons-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.product-content {
  display: flex;
}

.product-image {
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

.spec-section {
  margin-top: 30px;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.spec-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  color: #333;
}

.spec-table {
  background-color: #fff;
  border-radius: 4px;
}

.spec-title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
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
  align-items: center;
}
</style>