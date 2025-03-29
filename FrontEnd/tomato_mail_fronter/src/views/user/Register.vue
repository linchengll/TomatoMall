<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import {router} from '../../router'
import {userRegister} from "../../api/user.ts"
import {getAllStoreInfo, Store} from "../../api/store.ts";

// 输入框值（需要在前端拦截不合法输入：是否为空+额外规则）
const username = ref('')
const password = ref('')
const name = ref('')
const avatar = ref('')
const role = ref('')
const telephone = ref('')
const email = ref('')
const location = ref('')
const confirmPassword = ref('')
// const identity = ref('')
// const tel = ref('')
// const address = ref('')
// const password = ref('')
// const confirmPassword = ref('')

// //对于商家用户，还需要在注册时选择所属商店，从而传入storeId。但由于Lab2才会开发商店模块，所以这里暂且设置唯一一个Id为1的商店1，待Lab2完善
// const storeId = ref()

// 用户名是否为空
const hasUserNameInput = computed(() => username.value != '')
// 密码是否为空
const hasPasswordInput = computed(() => password.value != '')
// 姓名是否为空
const hasNameInput = computed(() => name.value != '')
// 头像url是否为空
const hasAvatarInput = computed(() => avatar.value != '')
// 用户身份是否为空
const hasRoleInput = computed(() => role.value != '')
// 手机号是否为空
const hasTelephoneInput = computed(() => telephone.value != '')
// 邮箱是否为空
const hasEmailInput = computed(() => email.value != '')
// 位置是否为空
const hasLocationInput = computed(() => location.value != '')
// 重复密码是否为空
const hasConfirmPasswordInput = computed(() => confirmPassword.value != '')

// // 地址是否为空
// const hasAddressInput = computed(() => address.value != '')
// // 身份是否为空
// const hasIdentityChosen = computed(() => identity.value != '')

// // 对于商家用户，商店Id是否为空
// const hasStoreName = computed(() => storeId.value != undefined)

// 电话号码的规则
const chinaMobileRegex = /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/
const telLegal = computed(() => chinaMobileRegex.test(telephone.value))
// 重复密码的规则
const isPasswordIdentical = computed(() => password.value == confirmPassword.value)
//邮箱格式限制
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(?:\.[a-zA-Z]{2,})?$/
const emailLegal = computed(() => emailRegex.test(email.value))
// 注册按钮可用性
const registerDisabled = computed(() => {
  // if (!hasIdentityChosen.value) {
  //   return true
  // } else if (identity.value == 'CUSTOMER') {
  //   return !(hasTelInput.value && hasPasswordInput.value && hasConfirmPasswordInput && hasAddressInput.value &&
  //       telLegal.value && isPasswordIdentical.value)
  // } else if (identity.value == 'STAFF') {
  //   return !(hasTelInput.value && hasPasswordInput.value && hasConfirmPasswordInput && hasAddressInput.value &&
  //       hasStoreName.value && telLegal.value && isPasswordIdentical.value)
  // }
  return !(hasUserNameInput.value && hasPasswordInput.value && hasNameInput.value && hasRoleInput.value && telLegal.value && isPasswordIdentical.value && emailLegal.value)
})

// 注册按钮触发
async function handleRegister() {
  let is_succ = true;

  if(avatar.value != ''){
    const imageRes = await imageInfoUpdate(avatar.value); // 使用 await 等待 imageInfoUpdate 完成
    if (imageRes.data.code === '400'){
      ElMessage({
        message: imageRes.data.msg,
        type: 'error',
        center: true,
      });
      is_succ = false;
    }else if(imageRes.data.code === '000'){
      avatar.value = imageRes.data.result;
    }
  }

  if (is_succ){
    const infoRes = await userRegister({
      username: username.value,
      password: password.value,
      name: name.value,
      avatar: avatar.value,
      role: role.value,
      telephone: telephone.value,
      email: email.value,
      location: location.value,
    });

    if (infoRes.data.code === '200') {  //类型守卫，它检查 res.data 对象中是否存在名为 code 的属性
      ElMessage({
        message: "注册成功！请登录账号",
        type: 'success',
        center: true,
      })
      router.push({path: "/login"})
    } else if (infoRes.data.code === '400') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
    }
  }



}

  // userRegister({
  //   username: username.value,
  //   password: password.value,
  //   name: name.value,
  //   avatar: avatar.value,
  //   role: role.value,
  //   telephone: telephone.value,
  //   email: email.value,
  //   location: location.value,
  // }).then(res => {
  //   if (res.data.code === '200') {  //类型守卫，它检查 res.data 对象中是否存在名为 code 的属性
  //     ElMessage({
  //       message: "注册成功！请登录账号",
  //       type: 'success',
  //       center: true,
  //     })
  //     router.push({path: "/login"})
  //   } else if (res.data.code === '400') {
  //     ElMessage({
  //       message: res.data.msg,
  //       type: 'error',
  //       center: true,
  //     })
  //   }
  // })
// }

const handleFileUpload = (event: Event) => {  // Added: Function to handle file selection
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    avatar.value = target.files[0];
  } else {
    avatar.value = null;
  }
};


// const tableData = ref<Store[]>([]);
// const fetchData = async () => {
//   tableData.value = await getAllStoreInfo();
// };
// onMounted(fetchData);

</script>


<template>
  <el-main class="main-frame bgimage">
    <el-card class="login-card">
      <div>
        <h1>创建一个新的账户</h1>

        <el-form>
          <el-row>
            <el-col :span="8">
              <el-form-item>
                <label for="username">用户名</label>
                <el-input id="username"
                          v-model="username"
                          placeholder="请输入用户名（作为登陆使用）"/>
              </el-form-item>
            </el-col>

            <el-col :span="1"></el-col>

            <el-col :span="7">
              <el-form-item>
                <label for="role">身份</label>
                <el-select id="role"
                           v-model="role"
                           placeholder="请选择"
                           style="width: 100%;"
                >
                  <el-option value="customer" label="顾客"/>
                  <el-option value="admin" label="管理员"/>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="1"></el-col>

            <el-col :span="7">
              <el-form-item>
                <label for="name">用户名</label>
                <el-input id="name"
                          v-model="name"
                          placeholder="请输入真实姓名"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="7">
              <el-form-item>

                <label v-if="!hasTelephoneInput" for="telephone">
                  注册手机号
                </label>
                <label v-else-if="!telLegal" for="telephone" class="error-warn">
                  手机号不合法
                </label>
                <label v-else for="telephone">
                  注册手机号
                </label>

                <el-input id="telephone"
                          v-model="telephone" :class="{'error-warn-input' :(hasTelephoneInput && !telLegal)}"
                          placeholder="请输入手机号"/>
              </el-form-item>
            </el-col>

            <el-col :span="1"></el-col>

            <el-col :span="7">
              <el-form-item>
                <label for="location">
                  地址
                </label>
                <el-input id="location"
                          v-model="location"
                          placeholder="请输入地址"/>
              </el-form-item>
            </el-col>

<!--            <el-col :span="7" v-if="identity==='STAFF'">-->
<!--              <el-form-item>-->
<!--                <label for="address">-->
<!--                  地址-->
<!--                </label>-->
<!--                <el-input id="address"-->
<!--                          v-model="address"-->
<!--                          placeholder="请输入地址"/>-->
<!--              </el-form-item>-->
<!--            </el-col>-->

<!--            <el-col :span="1" v-if="identity==='STAFF'"></el-col>-->
            <el-col :span="1"></el-col>

            <el-col :span="7">
              <el-form-item>
                <label v-if="!hasEmailInput" for="email">
                  注册邮箱
                </label>
                <label v-else-if="!emailLegal" for="email" class="error-warn">
                  邮箱不合法
                </label>
                <label v-else for="email">
                  注册邮箱
                </label>

                <el-input id="email"
                          v-model="email" :class="{'error-warn-input' :(hasEmailInput && !emailLegal)}"
                          placeholder="请输入邮箱"/>
              </el-form-item>
            </el-col>

          </el-row>

          <el-form-item>
            <label for="password">密码</label>
            <el-input type="password" id="password"
                      v-model="password"
                      placeholder="••••••••"/>
          </el-form-item>

          <el-form-item>
            <label v-if="!hasConfirmPasswordInput">确认密码</label>
            <label v-else-if="!isPasswordIdentical" class="error-warn">密码不一致</label>
            <label v-else>确认密码</label>

            <el-input type="password" id="confirm-password"
                      v-model="confirmPassword"
                      :class="{'error-warn-input' :(hasConfirmPasswordInput && !isPasswordIdentical)}"
                      placeholder="••••••••"/>
          </el-form-item>

          <el-form-item>
            <label for="avatar">个人头像</label>
            <input type="file" id="avatar" accept="image/*" @change="handleFileUpload" />
            <div v-if="avatar">
              已选择文件: {{ avatar.name }}
            </div>
          </el-form-item>

          <span class="button-group">
            <el-button @click.prevent="handleRegister"
                       :disabled="registerDisabled"
                       type="primary">
              创建账户
            </el-button>

            <router-link to="/login" v-slot="{navigate}">
              <el-button @click="navigate">
                去登录
              </el-button>
            </router-link>
          </span>

        </el-form>
      </div>

    </el-card>
  </el-main>

</template>


<style scoped>
.main-frame {
  width: 100%;
  height: 100%;

  display: flex;
  align-items: center;
  justify-content: center;
}

.bgimage {
  background-image: url("../../assets/login.jpg");
}

.login-card {
  width: 60%;
  padding: 10px;
}

.error-warn {
  color: red;
}

.error-warn-input {
  --el-input-focus-border-color: red;
}

.button-group {
  padding-top: 10px;
  display: flex;
  flex-direction: row;
  gap: 30px;
  align-items: center;
  justify-content: right;
}
</style>