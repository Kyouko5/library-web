<template>
  <div class="aside-container">
    <el-menu
      class="sidebar-menu"
      :default-active="path"
      router
      background-color="transparent"
      text-color="#ffffff"
      active-text-color="#ffffff"
    >
      <el-menu-item index="/dashboard" v-if="user.role === '1'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icondashboard"></use>
            </svg>
          </div>
          <span class="menu-text">数据统计</span>
        </div>
      </el-menu-item>
      
      <el-menu-item index="/book" v-if="user.role === '1'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconbook"></use>
            </svg>
          </div>
          <span class="menu-text">书籍管理</span>
        </div>
      </el-menu-item>
      
      <el-menu-item index="/book" v-if="user.role === '2'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconbook"></use>
            </svg>
          </div>
          <span class="menu-text">图书查询</span>
        </div>
      </el-menu-item>
      
      <el-menu-item index="/user" v-if="user.role === '1'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconreader"></use>
            </svg>
          </div>
          <span class="menu-text">用户管理</span>
        </div>
      </el-menu-item>
      
      <el-menu-item index="/lendrecord" v-if="user.role === '1'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#iconlend-record"></use>
            </svg>
          </div>
          <span class="menu-text">借阅管理</span>
        </div>
      </el-menu-item>
      
      <el-menu-item index="/bookwithuser" v-if="user.role === '2'" class="menu-item">
        <div class="menu-item-content">
          <div class="icon-wrapper">
            <el-icon>
              <grid/>
            </el-icon>
          </div>
          <span class="menu-text">借阅查询</span>
        </div>
      </el-menu-item>
      
      <el-sub-menu index="2" class="sub-menu">
        <template #title>
          <div class="menu-item-content">
            <div class="icon-wrapper">
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-mingpian"></use>
              </svg>
            </div>
            <span class="menu-text">个人信息</span>
          </div>
        </template>
        
        <el-menu-item index="/person" class="sub-menu-item">
          <div class="menu-item-content">
            <div class="icon-wrapper small">
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-a-bianji1"></use>
              </svg>
            </div>
            <span class="menu-text">修改个人信息</span>
          </div>
        </el-menu-item>
        
        <el-menu-item index="/password" class="sub-menu-item">
          <div class="menu-item-content">
            <div class="icon-wrapper small">
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-mima"></use>
              </svg>
            </div>
            <span class="menu-text">修改密码</span>
          </div>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/userStore.js';

const user = ref({});
const route = useRoute();
const path = ref(route.path);
const userStore = useUserStore();
user.value = userStore.userInfo;
</script>

<style scoped>
.aside-container {
  width: 220px;
  min-height: calc(100vh - 80px);
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  box-shadow: 4px 0 20px rgba(102, 126, 234, 0.2);
}

.aside-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.03)"/><circle cx="20" cy="60" r="0.5" fill="rgba(255,255,255,0.03)"/><circle cx="80" cy="40" r="0.5" fill="rgba(255,255,255,0.03)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

.sidebar-menu {
  border: none;
  padding: 20px 0;
  background: transparent;
  position: relative;
  z-index: 1;
}

.menu-item {
  margin: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.menu-item:hover::before {
  left: 100%;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(8px) scale(1.02);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.menu-item.is-active {
  background: rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.menu-item.is-active::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: linear-gradient(180deg, #ffffff 0%, rgba(255, 255, 255, 0.8) 100%);
  border-radius: 2px 0 0 2px;
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
  position: relative;
  z-index: 1;
}

.icon-wrapper {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.icon-wrapper.small {
  width: 28px;
  height: 28px;
}

.menu-item:hover .icon-wrapper {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(5deg) scale(1.1);
}

.icon {
  width: 20px;
  height: 20px;
  color: #ffffff;
  transition: all 0.3s ease;
}

.menu-text {
  font-weight: 500;
  font-size: 14px;
  color: #ffffff;
  transition: all 0.3s ease;
}

.menu-item:hover .menu-text {
  font-weight: 600;
}

/* 子菜单样式 */
.sub-menu {
  margin: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

:deep(.el-sub-menu__title) {
  padding: 12px 16px;
  background: transparent;
  border: none;
  color: #ffffff;
  transition: all 0.3s ease;
  border-radius: 12px;
}

:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

:deep(.el-sub-menu__icon-arrow) {
  color: #ffffff;
  transition: transform 0.3s ease;
}

:deep(.el-sub-menu.is-opened .el-sub-menu__icon-arrow) {
  transform: rotateZ(180deg);
}

.sub-menu-item {
  margin: 4px 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.sub-menu-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

.sub-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2);
}

/* 移除默认样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: auto;
  line-height: normal;
  padding: 12px 16px;
}

:deep(.el-menu-item.is-active) {
  background-color: transparent;
}

:deep(.el-menu-item:hover) {
  background-color: transparent;
}

:deep(.el-sub-menu .el-menu-item) {
  padding: 8px 12px;
  min-height: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .aside-container {
    width: 200px;
  }
  
  .menu-text {
    font-size: 13px;
  }
  
  .icon-wrapper {
    width: 32px;
    height: 32px;
  }
  
  .icon {
    width: 18px;
    height: 18px;
  }
}

/* 动画效果 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.menu-item {
  animation: slideIn 0.3s ease-out;
}

.menu-item:nth-child(1) { animation-delay: 0.1s; }
.menu-item:nth-child(2) { animation-delay: 0.2s; }
.menu-item:nth-child(3) { animation-delay: 0.3s; }
.menu-item:nth-child(4) { animation-delay: 0.4s; }
.menu-item:nth-child(5) { animation-delay: 0.5s; }
.sub-menu { animation-delay: 0.6s; }
</style>
