<script setup lang="ts">
import { ref, computed } from 'vue';
import {ElMessage} from "element-plus";
import {imageInfoUpdate} from "../../api/tools.ts"
import {Specification, addInfo, AddInfo} from "../../api/Book/products.ts";
import {router} from "../../router";

const title = ref('');
const price = ref(0);
const rate = ref(0);
const description = ref('');
const cover = ref('');
const detail = ref('');
const specificationsTableData = ref([]);

const hasTelInput = computed(() => title.value != '')
const hasPriceInput = computed(() => price.value != '')
const hasRateInput = computed(() => rate.value != '')
const isPriceLegal = computed(() => price.value >= 0)
const isRateLegal = computed(() => rate.value >= 0 && rate.value <= 10)

const fileList = ref<UploadUserFile[]>([])

const createDisabled = computed(() => {
  return !(hasTelInput.value && hasPriceInput.value && hasRateInput.value && isPriceLegal.value && isRateLegal.value);
})

const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile);
  fileList.value = uploadFiles;
};

const handlePreview: UploadProps['onPreview'] = (file) => {
  console.log(file)
}

const handleFileUpload: UploadProps['onChange'] = async (uploadFile) => {
  if (uploadFile.raw) {
    try {
      const response = await imageInfoUpdate(uploadFile.raw);
      if (response.data.code === '200') {
        cover.value = response.data.data;// 保存返回的图片 URL
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
//上传更新的商品信息（不是库存）
//
const handleUpdate = async () => {
  try {
    // 1. 构造 UpdateInfo 对象
    const specificationsSet = new Set<Specification>();
    specificationsTableData.value.forEach(item => {
      specificationsSet.add({
        item: item.item,
        value: item.value,
        id: 1
    });
    });

    const addData: AddInfo = {
      title: title.value,
      price: price.value,
      rate: rate.value,
      description: description.value,
      cover: cover.value,
      detail: detail.value,
      specifications: Array.from(specificationsSet),
    };

    // 2. 调用 updateInfo 函数
    const response = await addInfo(addData);

    // 3. 处理响应
    if (response.data.code === '200') { // 替换为你的实际 code
      ElMessage.success('添加成功');
    } else if(response.data.code === '400') {
      ElMessage.error(response.data.msg); // 替换为你的实际 msg
    }
  } catch (error) {
    console.error('Error updating product info:', error);
    ElMessage.error('更新失败，请检查网络');
  }
};

</script>

<template>
  <div class="top">
    <div class="Name">
      <p class="title">TOMATO MALL</p>
    </div>
  </div>

  <div class = "main-container">
    <div>
      <h2>商品信息</h2>
      <el-form label-width="120px">
        <el-form-item label="商品名称" required>
          <el-input v-model="title" />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input v-model.number="price" type="number" />
        </el-form-item>
        <el-form-item label="评分" required>
          <el-input v-model.number="rate" type="number" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="description" />
        </el-form-item>
        <el-form-item label="详细信息">
          <el-input type="textarea" v-model="detail" />
        </el-form-item>

        <el-form-item>
          <label for="cover">商品图片</label>
          <el-upload
              v-model:file-list="fileList"
              class="upload-demo"
              :on-remove="handleRemove"
              :on-preview="handlePreview"
              :on-change="handleFileUpload"
              :limit="1"
              list-type="picture"
              :auto-upload="false"
          >
            <el-button type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>

      <!-- Specifications Table -->
      <div>
        <el-button type="primary" @click="handleAdd">添加规格</el-button>
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

      <el-button type="primary" class="add-product-button" @click="handleUpdate" :disabled="createDisabled">添加商品</el-button>
    </div>
  </div>
</template>

<style scoped>
.main-container{
  margin-bottom: 30px;
  margin-left: 300px;
  width: 60%;
  padding: 20px;
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
.top {
  background-color: #000000;
  height: 26vh;
  display: flex;
}
.Name{
  lign-items: center;
  margin-left: 600px;
  margin-top: 60px;
}
.title{
  font-size: 65px; /* 更大的文字 */
  color: #dddddd; /* 蓝色，使用十六进制表示 */
  font-weight: 20000; /* 加粗 */
  font-family: 'Arial', sans-serif; /* 使用 Arial 字体 */
  margin-top: 15px;
}


</style>