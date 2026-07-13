<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="container nav-wrap">
        <router-link to="/" class="brand"><span class="brand-mark">爪</span><span>爪爪家园</span></router-link>
        <nav class="main-nav">
          <router-link to="/">首页</router-link><router-link to="/pets">寻找萌宠</router-link><router-link to="/publish">发布寄养</router-link>
        </nav>
        <div class="account-actions">
          <template v-if="user">
            <el-badge :value="unread" :hidden="!unread" class="message-badge"><el-button icon="el-icon-chat-dot-round" circle @click="navigate('/profile?tab=messages')" /></el-badge>
            <el-dropdown @command="handleCommand">
              <span class="user-trigger"><el-avatar :size="34" :src="user.avatar" @error="() => true">{{ user.nickname.slice(0,1) }}</el-avatar><span>{{ user.nickname }}</span><i class="el-icon-arrow-down" /></span>
              <el-dropdown-menu slot="dropdown"><el-dropdown-item command="profile">个人中心</el-dropdown-item><el-dropdown-item command="logout" divided>退出登录</el-dropdown-item></el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else><el-button type="text" @click="navigate('/login')">登录</el-button><el-button type="primary" round size="small" @click="navigate('/login?mode=register')">免费注册</el-button></template>
        </div>
      </div>
    </header>
    <main><router-view /></main>
    <footer class="footer"><div class="container"><strong>爪爪家园</strong><span>让每一次相遇，都通往温暖的家</span><span>本地公益演示平台 · 请对领养信息认真核验</span></div></footer>
  </div>
</template>
<script>
import { authApi, chatApi } from '../api'
export default {
  data: () => ({ user: null, unread: 0, timer: null }),
  created () { this.loadUser(); if (this.user) { this.loadUnread(); this.timer = setInterval(this.loadUnread, 15000) } },
  beforeDestroy () { clearInterval(this.timer) },
  methods: {
    loadUser () { try { this.user = JSON.parse(localStorage.getItem('pet_user')) } catch (_) { this.user = null } },
    async loadUnread () { try { const data = await chatApi.unread(); this.unread = data.count } catch (_) {} },
    navigate (target) {
      if (this.$route.fullPath === target) return Promise.resolve()
      return this.$router.push(target).catch(() => {})
    },
    async handleCommand (command) {
      if (command === 'profile') return this.navigate('/profile')
      try { await authApi.logout() } catch (_) {}
      localStorage.removeItem('pet_token')
      localStorage.removeItem('pet_user')
      this.user = null
      if (this.$route.path !== '/') await this.$router.replace('/').catch(() => {})
      this.$message.success('已安全退出')
    }
  }
}
</script>
