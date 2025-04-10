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

  // 检查是否修改了密码且新密码与旧密码不同
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
   