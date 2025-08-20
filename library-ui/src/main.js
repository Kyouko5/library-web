/**
 * Vue 3 应用程序主入口文件
 * 
 * 负责初始化和配置整个前端应用程序。
 * 
 * 主要功能：
 * - 创建Vue应用实例
 * - 配置状态管理（Pinia）
 * - 配置路由系统（Vue Router）
 * - 集成UI组件库（Element Plus）
 * - 配置全局样式和图标
 * - 启用数据持久化
 * 
 * 技术栈：
 * - Vue 3: 渐进式JavaScript框架
 * - Pinia: Vue状态管理库
 * - Vue Router: 官方路由管理器
 * - Element Plus: Vue 3 UI组件库
 * - Element Plus Icons: 图标库
 * 
 * @author kyouko
 * @version 1.0
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// ==================== UI组件库配置 ====================

// 引入Element Plus UI组件库
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';  // Element Plus的CSS样式
import locale from 'element-plus/es/locale/lang/zh-cn'; // 引入中文语言包，用于组件的国际化

// ==================== 全局样式配置 ====================

// 引入全局CSS样式文件
import '@/assets/global.css'  // 全局通用样式
import '@/assets/style.css'   // 项目特定样式

// ==================== 图标配置 ====================

// 引入自定义iconfont图标库
import '@/assets/icon/iconfont.js'   // iconfont JavaScript文件
import '@/assets/icon/iconfont.css'  // iconfont CSS样式

// 引入Element Plus的官方图标库
import * as ElIconModules from '@element-plus/icons'

// ==================== 状态管理配置 ====================

// 引入Pinia数据持久化插件
// 用于将store中的数据持久化到localStorage或sessionStorage
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// ==================== 应用程序初始化 ====================

/**
 * 创建Vue应用实例
 * 基于根组件App.vue创建应用程序实例
 */
const app = createApp(App)

/**
 * 创建Pinia状态管理实例
 * Pinia是Vue 3推荐的状态管理库，替代Vuex
 */
const pinia = createPinia()

// ==================== 插件配置和注册 ====================

/**
 * 配置Pinia数据持久化
 * 使用插件自动将store数据保存到浏览器存储中
 * 页面刷新后可以恢复之前的状态
 */
pinia.use(piniaPluginPersistedstate)

/**
 * 注册状态管理
 * 使应用程序能够使用Pinia进行状态管理
 */
app.use(pinia)

/**
 * 注册路由系统
 * 使应用程序支持单页应用(SPA)的路由功能
 */
app.use(router)

/**
 * 注册Element Plus UI组件库
 * 配置中文语言包，使组件显示中文文本
 * 注册后可以在所有组件中使用Element Plus的组件
 */
app.use(ElementPlus, {locale});

/**
 * 注册Element Plus图标组件
 * 遍历所有图标模块，将每个图标注册为全局组件
 * 注册后可以在模板中直接使用图标组件，如：<Edit />、<Delete />
 */
for(let iconName in ElIconModules){
  app.component(iconName, ElIconModules[iconName])
}

/**
 * 挂载应用程序到DOM
 * 将Vue应用实例挂载到HTML页面中id为"app"的元素上
 * 这是应用程序开始运行的起点
 */
app.mount('#app')
