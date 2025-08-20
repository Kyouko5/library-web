<template>
  <div>
    <el-card class="card">
      <h2 class="card-title">个人信息</h2>
      <el-form :model="form" ref="formRef" label-width="80px">
        <el-form-item label="用户名">
          <el-input class="input" v-model="form.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input class="input" v-model="form.nickName"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <span v-if="form.role === '1'" class="role">管理员</span>
          <span v-if="form.role === '2'" class="role">读者</span>
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input class="input" v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input class="input" v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input type="textarea" class="textarea" v-model="form.address"></el-input>
        </el-form-item>
      </el-form>
      <div class="button-container">
        <el-button type="primary" @click="update">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/userStore.js';
import { updateUserAPI } from '@/api/user.js';

const form = ref({});

const userStore = useUserStore();
form.value = userStore.userInfo;

const update = async () => {
  try {
    const res = await updateUserAPI(form.value);
    if (res.data.code === 200) {
      ElMessage.success('更新用户信息成功');
      // 刷新用户信息
      await userStore.getUserInfo();
    } else {
      ElMessage.error(res.data.message);
    }
  } catch (error) {
    ElMessage.error(error.response.data.message);
  }
};
</script>

<style scoped>
.card {
  width: 150%;
  margin-left: 120px;
  margin-top: 40px;
}

.card-title {
  padding: 30px;
}

.input {
  width: 80%;
}

.textarea {
  width: 80%;
}

.button-container {
  text-align: center;
}

</style>
