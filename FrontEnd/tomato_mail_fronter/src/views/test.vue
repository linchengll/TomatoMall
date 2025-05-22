<template>
  <div style="position: relative; width: 300px;">
    <!-- 圆角输入框 -->
    <input
        v-model="search"
        @focus="showTopList = true"
        @blur="onInputBlur"
        placeholder="搜索..."
        :class="{ 'input-expanded': showTopList }"
        style="
        width: 100%;
        border: 1px solid #ddd;
        border-radius: 20px;
        padding: 8px 15px;
        outline: none;
        transition: all 0.2s ease;
        font-size: 14px;
        position: relative; /* 确保z-index生效 */
        z-index: 1001; /* 高于下拉菜单 */
      "
    />

    <!-- 无缝衔接的下拉菜单 -->
    <transition name="slide-fade">
      <div
          v-show="showTopList"
          style="
          position: absolute;
          top: 100%;
          left: 0;
          width: 100%;
          background: white;
          border: 1px solid #ddd;
          border-top: none;
          border-radius: 0 0 20px 20px;
          z-index: 1000;
          padding: 10px 0;
          box-sizing: border-box;
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
          margin-top: -1px; /* 关键：消除缝隙 */
        "
      >
        <div v-if="topList.length > 0">
          <div
              v-for="item in topList"
              :key="item"
              style="
              padding: 8px 15px;
              cursor: pointer;
              transition: background 0.2s;
            "
              @mousedown.prevent="selectItem(item)"
              class="dropdown-item"
          >
            {{ item }}
          </div>
        </div>
        <div v-else style="color: #999; padding: 8px 15px;">暂无数据</div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const search = ref('');
const showTopList = ref(false);
const topList = ref([]);
let keepOpen = false;

function selectItem(item) {
  search.value = item;
  showTopList.value = false;
}

function onInputBlur() {
  if (!keepOpen) {
    setTimeout(() => {
      showTopList.value = false;
    }, 200);
  }
}
</script>

<style scoped>
/* 输入框展开状态 */
.input-expanded {
  background: #c7c7c7;
  border-radius: 20px 20px 0 0 !important;
  border-bottom: 1px solid transparent !important; /* 防止双边框 */
}

/* 下拉菜单项悬停效果 */
.dropdown-item:hover {
  background-color: #f5f5f5;
}

/* 平滑动画 */
.slide-fade-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 1, 1);
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-5px);
  opacity: 0;
}
</style>