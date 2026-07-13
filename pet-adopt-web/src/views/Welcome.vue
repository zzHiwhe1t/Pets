<template>
  <div class="welcome-page">
    <div class="welcome-paw paw-one" aria-hidden="true"><span class="paw-toe toe-one"></span><span class="paw-toe toe-two"></span><span class="paw-toe toe-three"></span><span class="paw-toe toe-four"></span><span class="paw-pad"></span></div>
    <div class="welcome-paw paw-two" aria-hidden="true"><span class="paw-toe toe-one"></span><span class="paw-toe toe-two"></span><span class="paw-toe toe-three"></span><span class="paw-toe toe-four"></span><span class="paw-pad"></span></div>
    <router-link to="/" class="brand welcome-brand"><span class="brand-mark">爪</span><span>爪爪家园</span></router-link>

    <main class="welcome-panel">
      <div class="welcome-heading">
        <h1>你好，{{ nickname }}</h1>
        <p>欢迎来到爪爪家园。告诉我们你今天想做什么，我们会带你直接前往。</p>
      </div>

      <div class="welcome-choices">
        <button class="welcome-choice adopt-choice" @click="choose('adopt')">
          <div class="choice-image"><img src="/api/files/buoumao.jpg" alt="等待领养的宠物"></div>
          <div class="choice-copy">
            <span>我想要</span>
            <h2>领养萌宠</h2>
            <p>看看正在等待温暖新家的伙伴</p>
          </div>
          <i class="el-icon-right choice-arrow" />
        </button>
        <button class="welcome-choice foster-choice" @click="choose('foster')">
          <div class="choice-image"><img src="/api/files/jinmao.jpg" alt="发布寄养信息"></div>
          <div class="choice-copy">
            <span>我想要</span>
            <h2>发布寄养</h2>
            <p>认真填写信息，为它寻找合适的新家</p>
          </div>
          <i class="el-icon-right choice-arrow" />
        </button>
      </div>

      <button class="welcome-skip" @click="choose('home')">暂时还没想好，先逛逛首页 <i class="el-icon-arrow-right" /></button>
      <div class="welcome-note"><i class="el-icon-lock" /> 你的选择只用于本次优先跳转，之后可随时切换</div>
    </main>
  </div>
</template>

<script>
export default {
  computed: {
    user () {
      try { return JSON.parse(localStorage.getItem('pet_user') || '{}') } catch (_) { return {} }
    },
    nickname () { return this.user.nickname || '新朋友' }
  },
  methods: {
    choose (choice) {
      const routes = { adopt: '/pets?status=AVAILABLE', foster: '/publish', home: '/' }
      localStorage.setItem('pet_preference', choice)
      localStorage.setItem('pet_welcome_seen', 'true')
      this.$router.replace(routes[choice]).catch(() => {})
    }
  }
}
</script>
