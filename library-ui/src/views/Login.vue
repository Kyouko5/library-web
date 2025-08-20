<template>
  <div class="login-container">
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
    
    <div class="login-content">
      <div class="login-card">
        <div class="card-header">
          <div class="logo-section">
            <img src="../assets/icon/stack-of-books.png" class="logo-icon" alt="图书馆" />
            <h2 class="system-title">图书馆管理系统</h2>
            <p class="system-subtitle">Library Management System</p>
          </div>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <div class="form-title">
            <h3>系统登录</h3>
            <p>欢迎回来，请输入您的登录信息</p>
          </div>
          
          <el-form-item prop="username" class="form-item">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名" 
              clearable
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password" class="form-item">
            <el-input 
              v-model="form.password" 
              placeholder="请输入密码"
              clearable 
              show-password
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item class="form-item">
            <div class="captcha-container">
              <el-input 
                v-model="form.validCode" 
                placeholder="请输入验证码"
                size="large"
                class="captcha-input"
              />
              <ValidCode @input="createValidCode" class="captcha-code" />
            </div>
          </el-form-item>
          
          <el-form-item class="form-item">
            <div class="role-selection">
              <span class="role-label">选择身份：</span>
              <el-radio-group v-model="form.rule" class="role-group">
                <el-radio value="1" class="role-radio">
                  <span class="role-text">管理员</span>
                </el-radio>
                <el-radio value="2" class="role-radio">
                  <span class="role-text">读者</span>
                </el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item">
            <el-button 
              type="primary" 
              size="large"
              class="login-button" 
              @click="login"
              :loading="loginLoading"
            >
              <span v-if="!loginLoading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
          
          <el-form-item class="form-item">
            <div class="register-link">
              <span>还没有账号？</span>
              <el-button type="text" @click="$router.push('/register')" class="link-button">
                立即注册 →
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/userStore.js';
import ValidCode from '../components/ValidCode.vue';
import { loginAPI } from '@/api/auth.js';

const form = ref({
  username: '',
  password: '',
  validCode: '',
  rule: '2'
});

const validCode = ref('');
const loginLoading = ref(false);

const rules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
});

const router = useRouter();
const formRef = ref(null);

const createValidCode = (data) => {
  validCode.value = data;
};

const login = async () => {
  try {
    const valid = await formRef.value.validate();
    if (valid) {
      if (!form.value.validCode) {
        ElMessage.error("请填写验证码");
        return;
      }
      if (form.value.validCode.toLowerCase() !== validCode.value.toLowerCase()) {
        ElMessage.error("验证码错误");
        return;
      }
      
      loginLoading.value = true;
      const res = await loginAPI(form.value);
      
      if (res.data.code === 200) {
        ElMessage.success("登录成功");
        localStorage.setItem('token', res.data.data.token);
        await useUserStore().getUserInfo();
        await router.push("/book");
      } else {
        ElMessage.error(res.data.message);
      }
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("登录失败，请稍后再试");
  } finally {
    loginLoading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  position: fixed;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 20%;
  right: 10%;
  animation-delay: 1s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 30%;
  left: 20%;
  animation-delay: 2s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  right: 20%;
  animation-delay: 3s;
}

.shape-5 {
  width: 40px;
  height: 40px;
  top: 50%;
  left: 5%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

.login-content {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 420px;
  padding: 16px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  overflow: hidden;
  animation: slideInUp 0.8s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.card-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.logo-section {
  position: relative;
  z-index: 1;
}

.logo-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  margin-bottom: 12px;
  transition: transform 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.1) rotate(5deg);
}

.system-title {
  color: white;
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 6px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.system-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  margin: 0;
  font-weight: 300;
  letter-spacing: 1px;
}

.login-form {
  padding: 28px 24px 24px;
}

.form-title {
  text-align: center;
  margin-bottom: 24px;
}

.form-title h3 {
  color: #667eea;
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 6px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-title p {
  color: #8892b0;
  font-size: 13px;
  margin: 0;
}

.form-item {
  margin-bottom: 18px;
}

:deep(.custom-input .el-input__wrapper) {
  border-radius: 10px;
  padding: 12px 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  border: 2px solid rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.25);
}

.input-icon {
  color: #667eea;
  font-size: 18px;
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-code {
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.role-selection {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 10px;
  border: 2px solid rgba(102, 126, 234, 0.1);
}

.role-label {
  color: #667eea;
  font-weight: 600;
  font-size: 13px;
}

.role-group {
  display: flex;
  gap: 20px;
}

:deep(.role-radio .el-radio__input.is-checked .el-radio__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

:deep(.role-radio .el-radio__input.is-checked + .el-radio__label) {
  color: #667eea;
}

.role-text {
  font-weight: 500;
  color: #667eea;
}

.login-button {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.register-link {
  text-align: center;
  color: #8892b0;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.link-button {
  color: #667eea;
  font-weight: 600;
  padding: 0;
  transition: all 0.3s ease;
}

.link-button:hover {
  color: #764ba2;
  transform: translateX(4px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-content {
    max-width: 360px;
    padding: 12px;
  }
  
  .login-form {
    padding: 24px 20px 20px;
  }
  
  .card-header {
    padding: 20px 16px;
  }
  
  .system-title {
    font-size: 18px;
  }
  
  .form-title h3 {
    font-size: 20px;
  }
  
  .form-item {
    margin-bottom: 16px;
  }
}

@media (max-width: 480px) {
  .login-content {
    max-width: 320px;
  }
  
  .captcha-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .captcha-code {
    align-self: center;
  }
  
  .role-selection {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
