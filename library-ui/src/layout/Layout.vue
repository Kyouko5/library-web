<template>
  <div class="layout-container">
    <Header class="layout-header"/>
    <!-- 主体 -->
    <div class="main-container">
      <!-- 侧边栏 -->
      <Aside class="layout-aside"/>
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <div class="content-container">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" class="page-content" />
            </transition>
          </router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from '../components/Header.vue';
import Aside from '../components/Aside.vue';
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

.layout-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(72, 219, 251, 0.05) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.layout-header {
  position: relative;
  z-index: 100;
  box-shadow: 0 2px 20px rgba(102, 126, 234, 0.2);
}

.main-container {
  display: flex;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 80px);
}

.layout-aside {
  position: relative;
  z-index: 10;
  flex-shrink: 0;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.content-container {
  flex: 1;
  padding: 24px;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
}

.page-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-height: calc(100vh - 160px);
  position: relative;
  overflow: hidden;
}

.page-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #48dbfb 100%);
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(30px) scale(0.95);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px) scale(0.95);
}

.fade-slide-enter-to,
.fade-slide-leave-from {
  opacity: 1;
  transform: translateX(0) scale(1);
}

/* 滚动条样式 */
.content-container::-webkit-scrollbar {
  width: 8px;
}

.content-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.content-container::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.content-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #5a6fd8 0%, #6a4190 100%);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-container {
    padding: 16px;
  }
  
  .page-content {
    min-height: calc(100vh - 140px);
  }
}

@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
  
  .layout-aside {
    order: 2;
    width: 100%;
    min-height: auto;
  }
  
  .content-wrapper {
    order: 1;
  }
  
  .content-container {
    padding: 12px;
  }
  
  .page-content {
    min-height: calc(100vh - 200px);
    border-radius: 12px;
  }
}

/* 加载动画 */
@keyframes contentLoad {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-content {
  animation: contentLoad 0.6s ease-out;
}

/* 悬停效果 */
.page-content:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  transition: all 0.3s ease;
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .layout-container {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  }
  
  .page-content {
    background: rgba(26, 26, 46, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
  }
}
</style>
