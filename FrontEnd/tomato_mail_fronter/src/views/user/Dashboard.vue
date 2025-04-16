<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { userInfo, userInfoUpdate } from '../../api/user.ts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { imageInfoUpdate } from "../../api/tools.ts"
import userImage from '../../assets/login.jpg';
import { useRouter } from 'vue-router';

const router = useRouter();
const role = sessionStorage.getItem("role")

// 原始用户信息
const username = ref(sessionStorage.getItem("username"))
const telephone = ref('')
const location = ref('')
const password = ref('')
const email = ref('')
const avatar = ref('')
const name = ref('')
// 新的编辑数据
const new_telephone = ref('')
const new_location = ref('')
const new_name = ref('')
const new_password = ref('')
const new_email = ref('')
const new_avatar = ref('')

const displayInfoCard = ref(false)

// 获取用户信息
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
    name.value = res.data.data.name || ''
    password.value = res.data.data.password || ''
  })
}

getUserInfo()

// 处理头像上传
const handleAvatarUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;

  const file = input.files[0];
  const reader = new FileReader();

  reader.onload = async (e: any) => {
    new_avatar.value = e.target.result; // 本地预览
    try {
      const response = await imageInfoUpdate(file);
      if (response.data.code === '200') {
        new_avatar.value = response.data.data;
        await nextTick();
        ElMessage.success('头像上传成功！');
      } else {
        ElMessage.error('头像上传失败！');
      }
    } catch (error) {
      ElMessage.error('头像上传失败，请重试！');
      console.error('上传失败:', error);
    }
  };

  reader.readAsDataURL(file);
};

// 更新用户信息
function updateInfo() {
  if (!username.value.trim()) {
    ElMessage({ type: 'error', message: '用户名不能为空！' })
    return
  }

  // 检查是否修改了密码
  const isPasswordChanged = new_password.value !== '' && new_password.value !== password.value;

  if (new_name.value === '') new_name.value = name.value
  if (new_email.value === '') new_email.value = email.value
  if (new_avatar.value === '') new_avatar.value = avatar.value
  if (new_telephone.value === '') new_telephone.value = telephone.value
  if (new_location.value === '') new_location.value = location.value
  if (new_password.value === '') new_password.value = password.value

  const updateData = {
    name: new_name.value.trim(),
    username: username.value.trim(),
    password: new_password.value.trim(),
    telephone: new_telephone.value.trim(),
    location: new_location.value.trim(),
    email: new_email.value.trim(),
    avatar: new_avatar.value.trim(),
  }

  userInfoUpdate(updateData).then(res => {
    if (res.data.code === '200') {
      ElMessage({
        type: 'success',
        message: isPasswordChanged ? '密码修改成功，请重新登录！' : '更新成功！',
        duration: isPasswordChanged ? 2000 : 3000
      })

      // 更新原始数据
      name.value = new_name.value
      telephone.value = new_telephone.value
      location.value = new_location.value
      email.value = new_email.value
      avatar.value = new_avatar.value
      password.value = new_password.value

      displayInfoCard.value = false

      // 如果修改了密码，跳转到登录页
      if (isPasswordChanged) {
        setTimeout(() => {
          // 清除sessionStorage中的用户信息
          sessionStorage.removeItem("username");
          sessionStorage.removeItem("role");
          sessionStorage.removeItem("token");
          router.push('/login');
        }, 2000);
      }
    } else {
      ElMessage({ type: 'error', message: res.data.msg })
    }
  }).catch(error => {
    ElMessage({ type: 'error', message: '更新失败: ' + error.message })
    console.error('更新失败:', error)
  })

  // 清空表单数据
  new_telephone.value = ''
  new_email.value = ''
  new_avatar.value = ''
  new_telephone.value = ''
  new_location.value = ''
  new_name.value = ''
  new_password.value = ''
}
</script>

<template>
  <el-main class="main-frame bgimage">
    <div class="user-info">
      <!-- 个人信息卡片 -->
      <div class="user-card">
        <div class="user_image">
          <img :src="avatar" class="image" />
        </div>
        <div class="user-name">
          {{ username }}
        </div>

        <div class="user-details">
          <p><strong>电话：</strong> {{ telephone || '这个人很懒，还没填这个东西' }}</p>
          <p><strong>邮箱：</strong> {{ email || '这个人很懒，还没填这个东西' }}</p>
          <p><strong>地址：</strong> {{ location || '这个人很懒，还没填这个东西' }}</p>
        </div>
        <el-button type="primary" @click="displayInfoCard = true" class="edit-button">编辑信息</el-button>
        <div>
          <el-dialog title="编辑用户信息" v-model="displayInfoCard" width="400px">
            <el-form label-width="100px">
              <el-form-item label="姓名">
                <el-input v-model="new_name" placeholder="输入姓名"></el-input>
              </el-form-item>
              <el-form-item label="电话">
                <el-input v-model="new_telephone" placeholder="输入电话"></el-input>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="new_location" placeholder="输入地址"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="new_email" placeholder="输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="new_password" type="password" placeholder="输入新密码"></el-input>
              </el-form-item>
              <el-form-item label="头像">
                <img :src="new_avatar" class="image" />
                <input type="file" accept="image/*" @change="handleAvatarUpload" />
              </el-form-item>
              <el-button type="primary" @click="updateInfo">保存修改</el-button>
            </el-form>
          </el-dialog>
        </div>
        <div>
          <el-button class="back-button" @click="$router.push('/main')" type="info" plain>返回</el-button>
        </div>
        <div>
          <el-button class="back-button" @click="$router.push('/login')" type="info" plain>退出登录</el-button>
        </div>
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
  margin: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.user-details p {
  font-size: 16px;
  margin: 0;
  padding: 10px 0;
  color: #666;
  width: 100%;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.user-details p:last-child {
  border-bottom: none;
}

.edit-button {
  width: 80%;
  font-size: 16px;
  padding: 10px;
  border-radius: 8px;
  background-color: #de6b6b;
  color: white;
  transition: all 0.3s ease;
}

.back-button {
  width: 80%;
  font-size: 16px;
  padding: 10px;
  border-radius: 8px;
}

.bgimage {
  background-image: url("../../assets/login.jpg");
  background-size: cover; /* 让背景图片覆盖整个容器 */
  background-position: center; /* 居中显示 */
  background-repeat: no-repeat; /* 防止图片重复 */
  width: 100vw; /* 适应整个视口宽度 */
  height: 100vh; /* 适应整个视口高度 */
}

.el-button {
  margin-top: 10px;
}
</style>