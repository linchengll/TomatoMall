<template>
  <el-header class="header">
    <div class="logo" @click="$router.push('/')">番茄书驿</div>
    <div class="nav-buttons">
      <el-button type="primary" icon="el-icon-house" @click="$router.push('/main')">首页</el-button>
      <el-button type="success" icon="el-icon-shopping-cart-full" @click="$router.push('/cart')">购物车</el-button>
    </div>
  </el-header>
</template>

<script setup lang="ts">
// 无需 props 和 emits，已移除搜索栏
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background: #ff4400;
  color: white;
}
.logo {
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}
.nav-buttons {
  display: flex;
  gap: 10px;
}
</style>