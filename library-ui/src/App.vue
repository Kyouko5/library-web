<!--
  根组件 App.vue
  
  这是Vue应用程序的根组件，所有其他组件都是它的子组件。
  
  主要职责：
  - 作为应用程序的入口点和容器
  - 提供路由视图的挂载点
  - 可以包含全局的布局元素（如果需要）
  
  当前实现：
  - 使用router-view组件显示当前路由对应的组件
  - 保持简洁，所有具体的布局和功能都在各个页面组件中实现
  
  路由系统说明：
  - router-view会根据当前URL路径动态渲染相应的组件
  - 支持嵌套路由，可以在任何组件中使用router-view
  - 路由配置在src/router/index.js中定义
  
  @author kyouko
  @version 1.0
-->
<template>
  <div>
    <!-- 
      路由视图组件
      根据当前路由动态渲染对应的页面组件
      
      可能渲染的组件包括：
      - 登录页面 (Login.vue)
      - 注册页面 (Register.vue)  
      - 主布局页面 (Layout.vue) 及其子路由
    -->
    <router-view/>
  </div>
</template>
