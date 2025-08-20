// 管理用户数据相关

import { defineStore } from 'pinia';
import { ref } from 'vue';
import { userInfoAPI } from '@/api/user.js';

export const useUserStore = defineStore('user', () => {
  // 1. 定义管理用户数据的state
  const userInfo = ref({});
  // 2. 定义获取接口数据的action函数
  const getUserInfo = async () => {
    const token = localStorage.getItem("token")
    const res = await userInfoAPI(token);
    userInfo.value = res.data.data;
  };

  // 退出时清除用户信息
  const clearUserInfo = () => {
    localStorage.removeItem('token');
    userInfo.value = {};
  };
  // 3. 以对象的格式把state和action return
  return {
    userInfo, getUserInfo, clearUserInfo,
  };
}, {
  persist: true,
});
