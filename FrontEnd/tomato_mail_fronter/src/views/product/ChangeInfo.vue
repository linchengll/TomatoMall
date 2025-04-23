<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getInfo, updateInfo, UpdateInfo, Specification } from "../../api/Book/products.ts";
import { useRoute } from 'vue-router';
import { getStockpileInfo, adjustStockpile } from "../../api/Book/stockpiles.ts";
import {router} from "../../router";
import Header from '../ManagerHead.vue';


const route = useRoute();
const productId = Number(route.params.id);

const id = ref('');
const specificationsTableData = ref([]);
const title = ref('');
const price = ref(0);
const rate = ref(0);
const description = ref('');
const detail = ref('');
const cover = ref('');

const amount = ref(0);
const frozen = ref(0);

// const id = ref('12781');
// const specificationsTableData = ref([
//   { item: '作者', value: 'Robert C. Martin' },
//   { item: '副标题', value: '程序员的职业素养' }
// ]);
// const title = ref('哈哈哈');
// const price = ref(0);
// const rate = ref(0);
// const description = ref('呵呵呵呵');
// const detail = ref('阿萨德很快就');
// const cover = ref('hsjhdjsad');
// const amount = ref(0);
// const frozen = ref(0);

//
//这段为处理规格操作的相关函数
//使用到常量specificationsTableData构建的一个table
//
const dialogVisible = ref(false);
const form = ref({
  item: '',
  value: ''
});
const editIndex = ref(null); // 用于存储正在编辑的行的索引

const handleAdd = () => {
  form.value = { item: '', value: '' }; // 清空表单数据
  editIndex.value = null; // 重置编辑索引
  dialogVisible.value = true;
};

const handleSave = () => {
  if (!form.value.item || !form.value.value) {
    ElMessage.warning('请填写完整信息');
    return;
  }

  if (editIndex.value !== null) {
    // 编辑模式

    specificationsTableData.value[editIndex.value].item = form.value.item;
    specificationsTableData.value[editIndex.value].value = form.value.value;
    ElMessage.success('更新成功');
  } else {
    // 添加模式
    const newItem = {
      id:specificationsTableData.value.length + 1,
      item: form.value.item,
      value: form.value.value
    };
    specificationsTableData.value.push(newItem);
    ElMessage.success('添加成功');
  }

  dialogVisible.value = false;
};

const handleEdit = (index, row) => {
  form.value = { ...row }; // 复制行数据到表单
  editIndex.value = index; // 记录编辑索引
  dialogVisible.value = true;
};

const handleDelete = (index) => {
  specificationsTableData.value.splice(index, 1);
  ElMessage.success('删除成功');
};



//
//从后端获取指定商品信息的函数
//主要使用specificationsTableData和product
//product使用是精髓
//
const fetchProduct = async () => {
  try {
    const response = await getInfo(productId);
    if (response.data.code === '200') {
      response.data.data;

      id.value = response.data.data.id;
      title.value = response.data.data.title || '';
      price.value = response.data.data.price || 0;
      rate.value = response.data.data.rate || 0;
      description.value = response.data.data.description || '';
      detail.value = response.data.data.detail || '';
      cover.value = response.data.data.cover;

      // 1. 获取后端返回的 specifications 数据
      const specifications = response.data.data.specifications;

      // 2. 数据转换
      let specificationsArray = [];
      if (specifications instanceof Set) {
        // 如果后端返回的是 Set 类型
        specifications.forEach(spec => {
          // 只提取 item 和 value 属性
          const newItem = {
            id: spec.id,
            item: spec.item,
            value: spec.value,
            productId: spec.productId,
          };
          specificationsArray.push(newItem);
        });
      } else if (Array.isArray(specifications)) {
        // 如果后端返回的是数组类型
        specifications.forEach(spec => {
          // 只提取 item 和 value 属性
          const newItem = {
            id: spec.id,
            item: spec.item,
            value: spec.value,
            productId: spec.productId
          };
          specificationsArray.push(newItem);
        });
      } else{
        ElMessage.error("后端返回的specifications数据类型与前端处理不匹配");
      }

      // 3. 更新 specificationsTableData
      specificationsTableData.value = specificationsArray;
    } else if (response.data.code === '400') {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('Error fetching product:', error);
    ElMessage.error('获取商品信息失败');
  }
};


//
//上传更新的商品信息（不是库存）
//
const handleUpdate = async () => {
  try {
    // 1. 构造 UpdateInfo 对象
    const specificationsSet = new Set<Specification>();
    specificationsTableData.value.forEach(item => {
      specificationsSet.add({
        id: item.id,
        item: item.item,
        value: item.value,
      });
    });

    const updateData: UpdateInfo = {
      id: id.value, // 商品 ID
      title: title.value, // 商品名称
      price: price.value, // 价格
      rate: rate.value, // 评分
      description: description.value, // 描述
      cover: cover.value, // 封面链接
      detail: detail.value, // 详细信息
      specifications: Array.from(specificationsSet), // 规格信息
    };

    // 2. 调用 updateInfo 函数
    const response = await updateInfo(updateData);

    // 3. 处理响应
    if (response.data.code === '200') { // 替换为你的实际 code
      ElMessage.success('更新成功');
    } else if(response.data.code === '400') {
      ElMessage.error(response.data.msg); // 替换为你的实际 msg
    }
  } catch (error) {
    console.error('Error updating product info:', error);
    ElMessage.error('更新失败，请检查网络');
  }
};


//
//从后端获取库存信息，上传到前端的const变量里
//
const fetchStockpileInfo = async () => {
  try {
    const response = await getStockpileInfo(productId);

    if (response.data.code === '200') {
      const stockpileData = response.data.data;

      // 更新 ref 变量的值
      amount.value = stockpileData.amount || 0;
      frozen.value = stockpileData.frozen || 0;
    } else if (response.data.code === '400') {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('Error fetching stockpile info:', error);
    ElMessage.error('获取库存信息失败');
  }
};


//
//调整库存，将数据传到后端
//
const handleAdjustStockpile = async () => {
  try {
    // 1. 调用 adjustStockpile 函数
    const response = await adjustStockpile(productId, amount.value);

    // 2. 处理响应
    if (response.data.code === '200') { // 替换为你的实际 code
      ElMessage.success('调整库存成功');
    } else if(response.data.code === '400') {
      ElMessage.error(response.data.msg); // 替换为你的实际 msg
    }
  } catch (error) {
    console.error('Error adjusting stockpile:', error);
    ElMessage.error('调整库存失败，请检查网络');
  }
};


const return_to_main = () => {
  router.push({path: "/main"});
};


onMounted(() => {
  fetchProduct();
  fetchStockpileInfo()
});

</script>

<template>
  <Header />

  <div class="main-container">
    <div class="container">
      <div class="info_block">
        <h2>商品信息</h2>
        <el-form label-width="120px">
          <el-form-item label="商品名称">
            <el-text>{{ title }}</el-text>
          </el-form-item>
          <el-form-item label="价格">
            <el-input v-model.number="price" type="number" />
          </el-form-item>
          <el-form-item label="评分">
            <el-input v-model.number="rate" type="number" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="description" />
          </el-form-item>
          <el-form-item label="详细信息">
            <el-input type="textarea" v-model="detail" />
          </el-form-item>
        </el-form>
      </div>


      <div class="guige_block">
        <el-button type="primary" @click="handleAdd" class = "button1">添加规格</el-button>
        <el-table :data="specificationsTableData" border style="width: 100%">
          <el-table-column prop="item" label="项目" />
          <el-table-column prop="value" label="值" />
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog v-model="dialogVisible" title="添加/编辑规格" width="30%">
          <el-form :model="form" label-width="80px">
            <el-form-item label="项目">
              <el-input v-model="form.item" />
            </el-form-item>
            <el-form-item label="值">
              <el-input v-model="form.value" />
            </el-form-item>
          </el-form>
          <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确定</el-button>
        </span>
          </template>
        </el-dialog>
      </div>

      <div style="margin-top: 20px; text-align: center;">
        <el-button type="primary" @click="handleUpdate">更新商品</el-button>
      </div>
    </div>

    <div class="container">
      <div class="info_block">
        <h2>库存信息更新</h2>
        <el-form label-width="120px">
          <el-form-item label="库存数">
            <el-input v-model.number="amount" type="number" />
          </el-form-item>
          <el-form-item label="冻结数">
            <el-input v-model.number="frozen" type="number" disabled/>
          </el-form-item>
        </el-form>
      </div>

      <div style="margin-top: 20px; text-align: center;">
        <el-button type="primary" @click="handleAdjustStockpile">更新库存</el-button>
      </div>
    </div>
    <el-button type="primary" class="add-product-button" @click="router.push(`/productDetail/${productId}`)">返回</el-button>
  </div>
</template>

<style scoped>

.guige_block {

  border: 1px solid #ccc;
  padding: 40px;
  margin: 5px;
}

.info_block {
  border: 1px solid #ccc;
  padding: 40px;
  margin: 5px;


}
.add-product-button {
  margin-top: 20px; /* 增加按钮与上方内容的间距 */
  display: block; /* 使按钮独占一行，便于居中 */
  margin-left: auto; /* 水平居中 */
  margin-right: auto; /* 水平居中 */
  width: 200px; /* 设置按钮的宽度 */
  height: 40px;
  font-size: 16px; /* 增大字体大小 */
  padding: 12px 20px; /* 增加内边距，使按钮看起来更大 */
}
.container {
}

.button1{
  margin-bottom: 5px;
}

.main-container{
  margin-bottom: 30px;
  margin-left: 300px;
  width: 60%;
  padding: 20px;
}

</style>