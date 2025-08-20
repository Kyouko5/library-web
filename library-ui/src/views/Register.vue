<template>
  <div class="register-container">
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
      </div>
    </div>
    
    <div class="register-content">
      <div class="register-card">
        <div class="card-header">
          <div class="logo-section">
            <img src="../assets/icon/stack-of-books.png" class="logo-icon" alt="图书馆" />
            <h2 class="system-title">图书馆管理系统</h2>
            <p class="system-subtitle">Library Management System</p>
          </div>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
          <div class="form-title">
            <h3>用户注册</h3>
            <p>创建您的账户，开始使用图书馆管理系统</p>
          </div>
          
          <div class="form-row">
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
            
            <el-form-item prop="nickName" class="form-item">
              <el-input 
                v-model="form.nickName" 
                placeholder="请输入昵称" 
                clearable
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Avatar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <div class="form-row">
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
            
            <el-form-item prop="confirmPassword" class="form-item">
              <el-input 
                v-model="form.confirmPassword" 
                placeholder="请确认密码"
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
          </div>
          
          <div class="form-row">
            <el-form-item prop="email" class="form-item">
              <el-input 
                v-model="form.email" 
                placeholder="请输入邮箱地址" 
                clearable
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="phone" class="form-item">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号码" 
                clearable
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <el-form-item prop="address" class="form-item">
            <el-input 
              v-model="form.address" 
              placeholder="请输入地址" 
              clearable
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Location /></el-icon>
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
            <div class="agreement-section">
              <el-checkbox v-model="form.agreement" class="agreement-checkbox">
                我已阅读并同意
              </el-checkbox>
              <el-button type="text" class="agreement-link">
                《用户协议》
              </el-button>
              <span>和</span>
              <el-button type="text" class="agreement-link">
                《隐私政策》
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item">
            <el-button 
              type="primary" 
              size="large"
              class="register-button" 
              @click="register"
              :loading="registerLoading"
            >
              <span v-if="!registerLoading">立即注册</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>
          
          <el-form-item class="form-item">
            <div class="login-link">
              <span>已有账号？</span>
              <el-button type="text" @click="$router.push('/login')" class="link-button">
                立即登录 →
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
import { User, Lock, Message, Phone, Location, Avatar } from '@element-plus/icons-vue';
import ValidCode from '../components/ValidCode.vue';
import { registerAPI } from '@/api/auth.js';

const form = ref({
  username: '',
  nickName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  address: '',
  validCode: '',
  agreement: false
});

const validCode = ref('');
const registerLoading = ref(false);

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'));
  } else {
    callback();
  }
};

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'));
  } else if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (value === '') {
    callback(new Error('请输入邮箱地址'));
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (value === '') {
    callback(new Error('请输入手机号码'));
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号码'));
  } else {
    callback();
  }
};

const rules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 10, message: '昵称长度在2到10个字符', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
});

const router = useRouter();
const formRef = ref(null);

const createValidCode = (data) => {
  validCode.value = data;
};

const register = async () => {
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
      if (!form.value.agreement) {
        ElMessage.error("请先同意用户协议和隐私政策");
        return;
      }
      
      registerLoading.value = true;
      const res = await registerAPI(form.value);
      
      if (res.data.code === 200) {
        ElMessage.success("注册成功，请登录");
        await router.push("/login");
      } else {
        ElMessage.error(res.data.message);
      }
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("注册失败，请稍后再试");
  } finally {
    registerLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  position: fixed;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-y: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.background-decoration {
  position: fixed;
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
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 60px;
  height: 60px;
  top: 5%;
  left: 5%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  top: 15%;
  right: 15%;
  animation-delay: 1s;
}

.shape-3 {
  width: 40px;
  height: 40px;
  top: 60%;
  left: 10%;
  animation-delay: 2s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 10%;
  right: 10%;
  animation-delay: 3s;
}

.shape-5 {
  width: 50px;
  height: 50px;
  top: 40%;
  right: 5%;
  animation-delay: 4s;
}

.shape-6 {
  width: 70px;
  height: 70px;
  bottom: 40%;
  left: 15%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
    opacity: 1;
  }
}

.register-content {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 520px;
  padding: 16px;
}

.register-card {
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
  padding: 20px 20px;
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
  animation: shimmer 4s infinite;
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
  margin-bottom: 10px;
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

.register-form {
  padding: 24px 24px 20px;
}

.form-title {
  text-align: center;
  margin-bottom: 20px;
}

.form-title h3 {
  color: #667eea;
  font-size: 22px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.form-item {
  margin-bottom: 0;
}

.form-item:not(.form-row .form-item) {
  margin-bottom: 16px;
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

.agreement-section {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 10px;
  border: 2px solid rgba(102, 126, 234, 0.1);
  font-size: 13px;
  color: #667eea;
}

:deep(.agreement-checkbox .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

.agreement-link {
  color: #667eea;
  font-weight: 600;
  padding: 0;
  font-size: 13px;
  text-decoration: underline;
}

.agreement-link:hover {
  color: #764ba2;
}

.register-button {
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

.register-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.register-button:hover::before {
  left: 100%;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.login-link {
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
  .register-content {
    max-width: 420px;
    padding: 12px;
  }
  
  .register-form {
    padding: 20px 20px 16px;
  }
  
  .card-header {
    padding: 16px 16px;
  }
  
  .system-title {
    font-size: 18px;
  }
  
  .form-title h3 {
    font-size: 20px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .form-title {
    margin-bottom: 16px;
  }
}

@media (max-width: 480px) {
  .register-content {
    max-width: 340px;
  }
  
  .captcha-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .captcha-code {
    align-self: center;
  }
  
  .agreement-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    text-align: left;
  }
}
</style>
