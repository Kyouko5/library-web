/**
 * 用户数据 Store（Pinia）。
 *
 * 职责：
 * - 持有并管理当前登录用户的基础信息（userInfo）。
 * - 提供获取用户信息的动作（从后端拉取并更新 state）。
 * - 提供登出清理逻辑（清除本地 token 与用户信息）。
 *
 * 相关约定：
 * - JWT token 保存在 localStorage 的 key 为 "token"；
 * - `userInfoAPI(token)` 需要传入 token；返回的响应结构应包含 `res.data.data` 为用户信息。
 * - 通过 Pinia 的 `persist: true` 将 state 持久化（默认使用 localStorage）。
 */

import { defineStore } from 'pinia';
import { ref } from 'vue';
import { userInfoAPI } from '@/api/user.js';

export const useUserStore = defineStore('user', () => {
  // 1) 用户信息 state：从后端接口获取后写入，结构由接口返回决定
  const userInfo = ref({});
  // 2) 获取用户信息的 action：
  // - 从 localStorage 取出后端登录时返回并保存的 JWT token；
  // - 调用用户信息接口，成功后将响应数据写入 userInfo。
  // 注意：若后续改为在 axios 拦截器自动注入 Authorization，可移除此处 token 传参。
  const getUserInfo = async () => {
    const token = localStorage.getItem("token")
    const res = await userInfoAPI(token);
    userInfo.value = res.data.data;
  };

  // 3) 退出登录：
  // - 清除本地 token，避免后续请求继续携带旧的身份；
  // - 将内存中的用户信息置空。
  const clearUserInfo = () => {
    localStorage.removeItem('token');
    userInfo.value = {};
  };
  // 4) 对外暴露 state 与 action
  return {
    userInfo, getUserInfo, clearUserInfo,
  };
}, {
  // 5) 启用持久化：刷新页面后仍能保留 userInfo（注意：token 由 localStorage 单独维护）
  persist: true,
});
