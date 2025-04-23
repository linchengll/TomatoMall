<!-- src/components/Header.vue -->
<template>
  <div class="top">
    <div class="Name">
      <p class="title">TOMATOMALL</p>
    </div>
  </div>
</template>

<script setup lang="ts">
// 无需逻辑处理，这里是纯展示用的头部
</script>

<style scoped>
.top {
  background-color: rgba(182, 16, 16, 0.6);
  height: 26vh;
  display: flex;
}

.Name {
  align-items: center;
  margin-left: 600px;
  margin-top: 60px;
}

.title {
  font-size: 65px;
  color: #dddddd;
  font-weight: 20000;
  font-family: 'Arial', sans-serif;
  margin-top: 15px;
}
</style>