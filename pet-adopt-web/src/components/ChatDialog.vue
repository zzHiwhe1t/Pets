<template>
  <el-dialog :visible.sync="visible" width="560px" :close-on-click-modal="false" custom-class="chat-dialog" @open="loadMessages">
    <div slot="title" class="chat-title"><img :src="pet.coverImage" @error="imageFallback"><div><strong>与 {{ otherName }} 沟通</strong><small>关于 {{ pet.name }}</small></div></div>
    <div ref="messages" class="message-list" v-loading="loading">
      <div v-if="!messages.length && !loading" class="empty-chat">打个招呼，了解它更多吧 👋</div>
      <div v-for="item in messages" :key="item.id" class="message-row" :class="{ mine: item.senderId === currentUser.id }">
        <div class="bubble"><p>{{ item.content }}</p><small>{{ item.createTime }}</small></div>
      </div>
    </div>
    <div class="message-compose"><el-input v-model.trim="content" maxlength="500" show-word-limit type="textarea" :rows="2" placeholder="请输入想对主人说的话，Enter 发送" @keyup.enter.native.exact="send" /><el-button type="primary" :loading="sending" @click="send">发送</el-button></div>
  </el-dialog>
</template>
<script>
import { chatApi } from '../api'
import { imageFallback } from '../utils/format'
export default {
  props: { value: Boolean, pet: { type: Object, required: true }, otherId: { type: Number, required: true }, otherName: { type: String, default: '宠物主人' } },
  data: () => ({ messages: [], content: '', loading: false, sending: false, poller: null }),
  computed: { visible: { get () { return this.value }, set (v) { this.$emit('input', v) } }, currentUser () { return JSON.parse(localStorage.getItem('pet_user') || '{}') } },
  watch: { visible (v) { if (!v) clearInterval(this.poller) } },
  methods: {
    imageFallback,
    async loadMessages () { this.loading = !this.messages.length; try { this.messages = await chatApi.thread({ petId: this.pet.id, otherId: this.otherId }); this.$nextTick(() => { const box = this.$refs.messages; if (box) box.scrollTop = box.scrollHeight }) } finally { this.loading = false; clearInterval(this.poller); this.poller = setInterval(() => this.refresh(), 5000) } },
    async refresh () { if (!this.visible) return; try { this.messages = await chatApi.thread({ petId: this.pet.id, otherId: this.otherId }) } catch (_) {} },
    async send () { if (!this.content || this.sending) return; this.sending = true; try { await chatApi.send({ petId: this.pet.id, receiverId: this.otherId, content: this.content }); this.content = ''; await this.loadMessages() } finally { this.sending = false } }
  }
}
</script>
