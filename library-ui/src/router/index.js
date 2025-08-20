/**
 * Vue Router 路由配置文件
 * 
 * 定义应用程序的所有路由规则和导航逻辑。
 * 
 * 路由结构说明：
 * - 根路径("/")：主应用布局，包含侧边栏和头部
 * - 子路由：各个功能页面，嵌套在主布局中显示
 * - 独立路由：登录和注册页面，不使用主布局
 * 
 * 权限控制：
 * - 使用路由守卫检查用户登录状态
 * - 未登录用户自动重定向到登录页面
 * - 基于JWT令牌验证用户身份
 * 
 * @author kyouko
 * @version 1.0
 */

import { createRouter, createWebHistory } from 'vue-router'
import Layout from "@/layout/Layout.vue";

/**
 * 创建路由器实例
 * 使用HTML5 History模式，支持美观的URL（无#号）
 */
const router = createRouter({
  // 使用HTML5 History模式，需要服务器配置支持
  history: createWebHistory(import.meta.env.BASE_URL),
  
  /**
   * 路由配置数组
   * 定义了应用程序的所有路由规则
   */
  routes: [
    {
      path: '/',
      name: 'layout',
      component: Layout,  // 主布局组件，包含侧边栏、头部等通用UI
      /**
       * 子路由配置
       * 所有子路由都会在Layout组件的router-view中渲染
       * 这些路由需要用户登录后才能访问
       */
      children: [
        {
          path: 'book',
          name: 'book',
          component: () => import('@/views/Book.vue'),  // 图书管理页面：图书的增删改查
        },
        {
          path: 'user',
          name: 'user',
          component: () => import('@/views/User.vue'),  // 用户管理页面：用户信息管理
        },
        {
          path: 'person',
          name: 'Person',
          component: () => import('@/views/Person.vue'),  // 个人信息页面：当前用户信息维护
        },
        {
          path: 'password',
          name: 'Password',
          component: () => import('@/views/Password.vue'),  // 密码修改页面：用户密码修改
        },
        {
          path: 'lendrecord',
          name: 'LendRecord',
          component: () => import('@/views/LendRecord.vue'),  // 借阅记录页面：查看借阅历史
        },
        {
          path: 'bookwithuser',
          name: 'BookWithUser',
          component: () => import('@/views/BookWithUser.vue'),  // 图书借阅页面：用户借阅图书操作
        },
        {
          path:'dashboard',
          name:'Dashboard',
          component:() => import("@/views/DashBoard.vue")  // 数据仪表盘：统计数据展示
        }
      ]
    },
    /**
     * 独立路由配置
     * 这些路由不使用主布局，直接渲染对应组件
     */
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),  // 用户登录页面
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),  // 用户注册页面
    },
  ],
})

/**
 * 全局前置路由守卫
 * 
 * 在每次路由跳转之前执行，用于权限验证。
 * 
 * 验证逻辑：
 * 1. 检查目标路由是否为登录或注册页面
 * 2. 如果不是，检查用户是否已登录（通过localStorage中的token）
 * 3. 未登录用户强制跳转到登录页面
 * 4. 已登录用户或访问登录/注册页面的用户正常跳转
 * 
 * @param {Object} to - 即将进入的目标路由对象
 * @param {Object} from - 当前导航正要离开的路由对象  
 * @param {Function} next - 确认导航的回调函数
 */
router.beforeEach((to, from, next) => {
  // 从localStorage获取用户的JWT令牌
  // 令牌存在表示用户已登录，不存在表示用户未登录
  const token = localStorage.getItem('token');
  
  // 检查是否访问需要登录的页面
  // 登录页面和注册页面不需要验证，其他所有页面都需要登录
  if (to.name !== 'login' && to.name !== 'register' && !token) {
    // 未登录且访问受保护页面，重定向到登录页面
    next({ name: 'login' });
  } else {
    // 满足以下条件之一时允许访问：
    // 1. 访问登录或注册页面
    // 2. 已登录用户访问任何页面
    next();
  }
});

export default router
