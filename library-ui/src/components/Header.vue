<template>
  <div class="header">

    <div class="logo">
      <div class="logo-icon">
        <img src="../assets/icon/stack-of-books.png" class="icon" alt="" />
      </div>

      <div class="logo-text">
        <h1 class="title gradient-text">图书馆管理系统</h1>
        <p class="subtitle">Library Management System</p>
      </div>
    </div>
    <div class="user-section">
      <div class="welcome-text">
        <span class="greeting">欢迎回来</span>
      </div>
      <el-dropdown class="user-dropdown" trigger="hover">
        <div class="user-info">
          <div class="avatar">
            <i class="el-icon-user-solid"></i>
          </div>
          <span class="username">{{ user.nickName }}</span>
          <el-icon class="dropdown-icon">
            <arrow-down />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="custom-dropdown">
            <el-dropdown-item class="dropdown-item" @click="exit">
              <i class="el-icon-switch-button"></i>
              <span>退出系统</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowDown } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/userStore.js';

const user = ref({});
const router = useRouter();
const userStore = useUserStore();
user.value = userStore.userInfo;

// 退出登录
const exit = () => {
  userStore.clearUserInfo();
  router.push('/login');
  ElMessage.success('退出系统成功');
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.logo {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  position: relative;
}

.icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.icon:hover {
  transform: rotate(5deg) scale(1.1);
}

.logo-text {
  color: white;
}

.title {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 12px;
  margin: 0;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 1px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  color: white;
  font-size: 14px;
  opacity: 0.9;
}

.greeting {
  font-weight: 500;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 25px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.username {
  color: white;
  font-weight: 500;
  font-size: 14px;
}

.dropdown-icon {
  color: white;
  transition: transform 0.3s ease;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

:deep(.custom-dropdown) {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 8px;
}

:deep(.dropdown-item) {
  border-radius: 8px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  font-weight: 500;
}

:deep(.dropdown-item:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .header {
    padding: 12px 16px;
  }

  .title {
    font-size: 18px;
  }

  .subtitle {
    display: none;
  }

  .welcome-text {
    display: none;
  }
}
</style>
