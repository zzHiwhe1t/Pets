<template>
  <div class="auth-page">
    <router-link to="/" class="brand auth-brand"><span class="brand-mark">爪</span><span>爪爪家园</span></router-link>
    <div class="auth-card">
      <div class="auth-illustration"><img src="/api/files/jinmao.jpg" alt="萌宠头像"><h2>每个生命，都值得被认真选择</h2><p>加入爪爪家园，让相遇发生在充分了解之后。</p><div class="trust-list"><span><i class="el-icon-circle-check" /> 本地数据存储</span><span><i class="el-icon-circle-check" /> 沟通记录可追溯</span><span><i class="el-icon-circle-check" /> 领养流程透明</span></div></div>
      <div class="auth-form-wrap">
        <div class="auth-tabs"><button :class="{ active: mode==='login' }" @click="mode='login'">登录</button><button :class="{ active: mode==='register' }" @click="mode='register'">注册</button></div>
        <div v-if="mode==='login'"><h1>欢迎回来</h1><p class="subtext">登录后继续你的暖心旅程</p><el-form ref="loginForm" :model="login" :rules="loginRules" label-position="top" @keyup.enter.native="submitLogin"><el-form-item label="用户名" prop="username"><el-input v-model.trim="login.username" prefix-icon="el-icon-user" placeholder="请输入用户名" /></el-form-item><el-form-item label="密码" prop="password"><el-input v-model="login.password" prefix-icon="el-icon-lock" type="password" show-password placeholder="请输入密码" /></el-form-item><el-button class="submit-btn" type="primary" :loading="loading" @click="submitLogin">登录</el-button></el-form></div>
        <div v-else><h1>创建账号</h1><p class="subtext">成为负责任的领养人或发布人</p><el-form ref="registerForm" :model="register" :rules="registerRules" label-position="top"><el-form-item label="用户名" prop="username"><el-input v-model.trim="register.username" prefix-icon="el-icon-user" placeholder="3-20位用户名" /></el-form-item><el-form-item label="昵称" prop="nickname"><el-input v-model.trim="register.nickname" prefix-icon="el-icon-magic-stick" placeholder="大家怎么称呼你" /></el-form-item><el-form-item label="手机号" prop="phone"><el-input v-model.trim="register.phone" prefix-icon="el-icon-mobile-phone" placeholder="选填，11位手机号" /></el-form-item><el-form-item label="密码" prop="password"><el-input v-model="register.password" prefix-icon="el-icon-lock" type="password" show-password placeholder="至少6位" /></el-form-item><el-button class="submit-btn" type="primary" :loading="loading" @click="submitRegister">注册并登录</el-button></el-form></div>
      </div>
    </div>
  </div>
</template>
<script>
import { authApi } from '../api'
export default {
  data () { const phone = (r,v,cb) => !v || /^1[3-9]\d{9}$/.test(v) ? cb() : cb(new Error('请输入正确的手机号')); return { mode: this.$route.query.mode === 'register' ? 'register' : 'login', loading:false, login:{username:'alice',password:'123456'}, register:{username:'',nickname:'',phone:'',password:''}, loginRules:{username:[{required:true,message:'请输入用户名',trigger:'blur'}],password:[{required:true,message:'请输入密码',trigger:'blur'}]}, registerRules:{username:[{required:true,message:'请输入用户名',trigger:'blur'},{min:3,max:20,message:'长度为3-20位',trigger:'blur'}],nickname:[{required:true,message:'请输入昵称',trigger:'blur'}],phone:[{validator:phone,trigger:'blur'}],password:[{required:true,message:'请输入密码',trigger:'blur'},{min:6,max:30,message:'长度为6-30位',trigger:'blur'}]} } },
  methods: {
    validateForm (refName) {
      const form = this.$refs[refName]
      if (!form) return Promise.resolve(false)
      return new Promise(resolve => form.validate(valid => resolve(valid)))
    },
    saveSession (data) {
      localStorage.setItem('pet_token', data.token)
      localStorage.setItem('pet_user', JSON.stringify(data.user))
    },
    async enterSite (data, message, preferredTarget) {
      this.saveSession(data)
      this.$message.success(message)
      const target = preferredTarget || this.$route.query.redirect || '/'
      if (this.$route.fullPath !== target) await this.$router.replace(target).catch(() => {})
    },
    async submitLogin () {
      if (!await this.validateForm('loginForm')) return
      this.loading = true
      try {
        const data = await authApi.login(this.login)
        await this.enterSite(data, `欢迎回来，${data.user.nickname}`)
      } finally {
        this.loading = false
      }
    },
    async submitRegister () {
      if (!await this.validateForm('registerForm')) return
      this.loading = true
      try {
        const credentials = { username: this.register.username, password: this.register.password }
        await authApi.register(this.register)
        // 注册页没有 loginForm，直接请求登录接口，避免访问未渲染的表单引用。
        const data = await authApi.login(credentials)
        await this.enterSite(data, `注册成功，欢迎你，${data.user.nickname}`, '/welcome')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
