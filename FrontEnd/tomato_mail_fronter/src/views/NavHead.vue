<!-- components/NavBar.vue -->
<template>
  <el-header class="header">
    <div class="logo">番茄书驿</div>
    <el-input v-model="search" placeholder="搜索商品..." class="search-box" />
    <el-button type="primary" icon="el-icon-search">搜索</el-button>
  </el-header>
</template>

<script setup lang="ts">
import { defineEmits, defineProps } from 'vue'
import { computed } from 'vue' // 添加这行

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits(['update:modelValue'])

const search = computed({
  get: () => props.modelValue,
  set: val => emit('update:modelValue', val),
})
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #ff4400;
  color: white;
}
.logo {
  font-size: 24px;
  font-weight: bold;
  margin-right: 20px;
}
.search-box {
  flex: 1;
  margin-right: 10px;
}
</style>