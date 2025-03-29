<script setup lang="ts">
import { ref, computed } from 'vue'
import { userInfo, userInfoUpdate } from '../../api/user.ts'
import { ElMessage, ElMessageBox } from 'element-plus'
import userImage from '../../assets/login.jpg';

const role = sessionStorage.getItem("role")

// 用户信息的响应式变量
const username = ref('')
const telephone = ref('')
const location = ref('')
const name = ref('')
const newName = ref('') // 新用户名
const displayInfoCard = ref(false)
const password = ref('') // 新密码
const confirmPassword = ref('')// 确认密码
const email = ref('')
const avatar = ref('')
const image = ref(userImage)

function getUserInfo() {
  if (!username.value) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }
  userInfo(username.value).then(res => {
    username.value = res.data.data.username // 确保使用 API 返回的 username
    telephone.value = res.data.data.telephone || ''
    location.value = res.data.data.location || ''
    email.value = res.data.data.email || ''
    avatar.value = res.data.data.avatar || '' // 头像
  })
}

getUserInfo()

// 处理头像上传
const handleAvatarUpload = (file: File) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    avatar.value = e.target.result // 预览新头像
  }
  reader.readAsDataURL(file)
}

// 更新用户信息
function updateInfo() {
  if (!username.value.trim()) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }

  // 仅提交非空字段
  const updateData = {
    username: username.value.trim(), // 必须包含用户名
    password: password.value.trim() || undefined,
    telephone: telephone.value.trim() || undefined,
    location: location.value.trim() || undefined,
    email: email.value.trim() || undefined,
    avatar: avatar.value, // 头像
  }

  userInfoUpdate(updateData).then(res => {
    console.log(res)
    if (res.data.code === '200') {
      ElMessage({ type: 'success', message: '更新成功！' })
      password.value = '' // 清空密码字段
      getUserInfo() // 重新获取信息
      displayInfoCard.value = false // 关闭对话框
    } else {
      ElMessage({ type: 'error', message: res.data.msg })
    }
  })
}
</script>

<template>
  <el-main class="main-frame bgimage">
    <div class="user-info" >
      <!-- 个人信息卡片 -->
      <div class="user-card">
        <div class="user_image">
          <img :src="image" class="image" />
        </div>
        <div class="user-name">
<!--          {{ name }}-->
          一只番茄
        </div>

        <div class="user-details">
          <p><strong>电话：</strong> {{ telephone || '这个人很懒，还没填这个东西' }}</p>
          <p><strong>邮箱：</strong> {{ email || '这个人很懒，还没填这个东西' }}</p>
          <p><strong>地址：</strong> {{ location || '这个人很懒，还没填这个东西' }}</p>
        </div>
        <el-button type="primary" @click="displayInfoCard = true" class="edit-button">编辑信息</el-button>

        <el-dialog title="编辑用户信息" v-model="displayInfoCard" width="400px">
          <el-form label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="username" placeholder="输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="name" placeholder="输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="telephone" placeholder="输入电话"></el-input>
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="location" placeholder="输入地址"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="email" placeholder="输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="password" type="password" placeholder="输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="头像">
              <img :src="avatar" class="image" />
              <input type="file" accept="image/*" @change="handleAvatarUpload($event.target.files[0])" />
            </el-form-item>
            <el-button type="primary" @click="updateInfo">保存修改</el-button>
          </el-form>
        </el-dialog>
      </div>
    </div>
  </el-main>
</template>

<style scoped>
.user-info {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.user-card {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 30px;
  height: 600px;
  width: 500px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  border: 2px solid #f0f0f0;
}

.user_image {
  margin-bottom: 20px;
}

.image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-family: "幼圆", "宋体", "微软雅黑", sans-serif;
  font-weight: bold;
  font-size: 25px;
}

.user-details {
  text-align: left;
  margin: 30px 0; /* 整体增加外边距，使其在上下居中 */
  display: flex;
  flex-direction: column;
  align-items: center; /* 保持文本居中 */
  gap: 20px; /* 使每个字段之间有均匀的间隔 */
}

.user-details p {
  font-size: 16px;
  margin: 0;
  padding: 10px 0; /* 增加内部间距 */
  color: #666;
  width: 100%;
  text-align: center;
  border-bottom: 1px solid #eee; /* 增加下边框，使信息更清晰 */
}

.user-details p:last-child {
  border-bottom: none; /* 最后一项去掉下边框 */
}

.edit-container {
  display: flex;
  justify-content: center; /* 让按钮居中 */
  margin-top: 20px; /* 增加上边距，使其与上方信息分隔 */
  padding-top: 15px;
  border-top: 1px solid #ddd; /* 添加分割线，视觉更清晰 */
}

.edit-button {
  width: 80%; /* 让按钮宽度更大，更易点击 */
  font-size: 16px;
  padding: 10px;
  border-radius: 8px; /* 圆角设计，视觉更友好 */
  background-color: #de6b6b;
  color: white;
  transition: all 0.3s ease;
}

.bgimage {
  background-image: url("../../assets/login.jpg");
}

.el-button {
  margin-top: 10px;
}
</style>