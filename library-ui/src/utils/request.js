/**
 * HTTP请求工具模块
 * 
 * 基于axios封装的HTTP客户端，提供统一的请求配置和拦截处理。
 * 
 * 主要功能：
 * - 统一的请求基础配置（baseURL、超时、请求头等）
 * - 自动添加JWT认证令牌到请求头
 * - 统一的错误处理和响应拦截
 * - 自动处理认证失败的重定向逻辑
 * 
 * 技术特性：
 * - 使用axios作为HTTP客户端库
 * - 支持请求和响应拦截器
 * - 集成Vue Router进行页面跳转
 * - 从localStorage获取认证令牌
 * 
 * 使用方式：
 * import request from '@/utils/request'
 * request.get('/api/books').then(response => {...})
 * request.post('/api/auth/login', data).then(response => {...})
 * 
 * @author kyouko
 * @version 1.0
 */

import axios from 'axios'
import router from "../router";

/**
 * 创建axios实例
 * 
 * 配置了统一的请求基础设置，所有通过此实例发送的请求都会应用这些配置。
 * 
 * 配置说明：
 * - baseURL: 后端API服务器地址，所有相对路径请求都会以此为基础
 * - timeout: 请求超时时间，超过此时间未响应则取消请求
 * - headers: 默认请求头，设置内容类型为JSON格式
 * 
 * 注意事项：
 * - baseURL需要与后端服务器地址保持一致
 * - 生产环境中应该使用环境变量配置baseURL
 * - timeout设置需要考虑网络环境和接口响应时间
 */
const request = axios.create({
  baseURL: 'http://localhost:8080',  // 后端API服务器地址
  timeout: 5000,                     // 请求超时时间：5秒
  headers: {
    'Content-Type': 'application/json',  // 默认请求内容类型为JSON
  }
})

/**
 * 请求拦截器
 * 
 * 在每个HTTP请求发送之前执行，用于统一处理请求配置。
 * 主要功能是自动添加JWT认证令牌到请求头中。
 * 
 * 处理流程：
 * 1. 从localStorage获取存储的JWT令牌
 * 2. 如果令牌存在，添加到Authorization请求头
 * 3. 使用Bearer Token格式（标准的JWT传输格式）
 * 4. 返回修改后的请求配置
 * 
 * 认证机制：
 * - 令牌存储在浏览器的localStorage中
 * - 使用标准的"Bearer {token}"格式
 * - 后端通过解析Authorization头验证用户身份
 * 
 * 错误处理：
 * - 如果请求配置过程中出现错误，直接拒绝Promise
 * - 错误会传递给调用方进行处理
 */
request.interceptors.request.use(config => {
  // 从本地存储获取JWT令牌
  const token = localStorage.getItem('token');
  
  // 如果令牌存在，添加到请求头中
  if (token) {
    // 使用Bearer Token格式，这是JWT的标准传输方式
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  // 返回修改后的请求配置
  return config;
}, error => {
  // 处理请求配置错误
  // 这种情况比较少见，通常是axios内部错误
  console.error('请求拦截器错误:', error);
  return Promise.reject(error);
});

/**
 * 响应拦截器
 * 
 * 在每个HTTP响应返回之后、业务代码处理之前执行。
 * 用于统一处理响应数据和错误情况。
 * 
 * 成功响应处理：
 * - 直接返回响应对象，不做额外处理
 * - 业务代码可以通过response.data获取实际数据
 * 
 * 错误响应处理：
 * - 特别处理403 Forbidden错误（权限不足或令牌无效）
 * - 自动重定向到登录页面
 * - 其他错误直接抛出，由业务代码处理
 * 
 * 错误类型说明：
 * - 403: 禁止访问，通常是JWT令牌无效或权限不足
 * - 401: 未授权，通常是未提供令牌或令牌格式错误
 * - 404: 接口不存在
 * - 500: 服务器内部错误
 * 
 * 自动重定向逻辑：
 * - 当收到403错误时，说明用户认证失效
 * - 自动跳转到登录页面，提示用户重新登录
 * - 使用Vue Router进行页面跳转
 */
request.interceptors.response.use(
  function (response) {
    /**
     * 响应成功处理函数
     * 
     * 当HTTP状态码为2xx时执行此函数。
     * 目前直接返回响应对象，业务代码需要通过response.data获取数据。
     * 
     * 可扩展功能：
     * - 统一的数据格式处理
     * - 业务状态码检查
     * - 成功消息提示
     * - 数据预处理或格式化
     * 
     * @param {Object} response - axios响应对象
     * @returns {Object} 响应对象
     */
    return response;
  },
  function (error) {
    /**
     * 响应错误处理函数
     * 
     * 当HTTP状态码不是2xx时执行此函数。
     * 统一处理各种HTTP错误状态。
     * 
     * @param {Object} error - axios错误对象
     * @returns {Promise} 被拒绝的Promise
     */
    
    // 检查是否存在响应对象和状态码
    if (error.response && error.response.status === 403) {
      /**
       * 处理403 Forbidden错误
       * 
       * 403错误通常表示：
       * - JWT令牌已过期
       * - JWT令牌无效或被篡改
       * - 用户权限不足
       * - 服务器拒绝访问
       * 
       * 处理策略：
       * - 记录错误日志便于调试
       * - 自动跳转到登录页面
       * - 清除本地存储的无效令牌（可选）
       */
      console.error('403 Forbidden Error: Access denied');
      
      // 可选：清除无效的令牌
      // localStorage.removeItem('token');
      
      // 自动跳转到登录页面
      router.push('/login');
    }
    
    // 其他错误类型的处理可以在这里扩展
    // 例如：
    // if (error.response && error.response.status === 401) {
    //   // 处理401未授权错误
    // }
    // if (error.response && error.response.status === 500) {
    //   // 处理500服务器错误
    // }
    
    // 将错误继续抛出，让业务代码处理
    return Promise.reject(error);
  }
);

/**
 * 导出配置好的axios实例
 * 
 * 其他模块可以直接导入使用，享受统一的配置和拦截处理。
 * 
 * 使用示例：
 * 
 * // 在API模块中使用
 * import request from '@/utils/request'
 * 
 * // GET请求
 * export const getBooks = () => {
 *   return request.get('/api/books')
 * }
 * 
 * // POST请求
 * export const login = (data) => {
 *   return request.post('/api/auth/login', data)
 * }
 * 
 * // PUT请求
 * export const updateBook = (id, data) => {
 *   return request.put(`/api/books/${id}`, data)
 * }
 * 
 * // DELETE请求
 * export const deleteBook = (id) => {
 *   return request.delete(`/api/books/${id}`)
 * }
 */
export default request

