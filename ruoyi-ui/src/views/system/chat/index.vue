<template>
  <div class="app-container">
    <div class="page-header">
      <el-page-header content="AI数字人助手" />
    </div>

    <!-- 对话区域 -->
    <el-card class="chat-container" v-if="sessionId">
      <div class="chat-messages">
        <!-- 历史消息 -->
        <div v-for="(msg, index) in messages" :key="index"
             :class="['message-item', msg.role === 'user' ? 'user-message' : 'assistant-message']">
          <div class="avatar">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
          <div class="content">{{ msg.content }}</div>
        </div>

        <!-- 加载中 -->
        <div class="loading-item" v-if="isProcessing">
          <div class="avatar">AI</div>
          <div class="content">
            <el-loading-spinner />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 输入区域 -->
    <el-card class="input-card">
      <el-input
          v-model="currentMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您想说的话"
          resize="none"
          @keyup.enter.native="handleSend"
      />
      <div class="input-actions">
        <el-button type="primary" @click="handleSend" :loading="isLoading">
          发送
        </el-button>
        <el-button type="text" @click="newSession">
          新会话
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { createSession, sendMessage, getSessionHistory, queryChatResult } from '@/api/system/chat'
import { Message } from 'element-ui'

export default {
  name: 'Chat',
  data() {
    return {
      sessionId: '',
      currentMessage: '',
      messages: [],
      isLoading: false,
      isProcessing: false,
      queryTimer: null,
      taskId: ''
    }
  },
  created() {
    // 初始化时创建新会话
    this.newSession()
  },
  beforeDestroy() {
    if (this.queryTimer) {
      clearInterval(this.queryTimer)
    }
  },
  methods: {
    // 新建会话
    async newSession() {
      try {
        const response = await createSession()
        this.sessionId = response.data
        this.messages = []
        this.currentMessage = ''
      } catch (error) {
        Message.error('创建会话失败：' + error.message)
      }
    },

    // 发送消息
    async handleSend() {
      if (!this.currentMessage.trim()) return

      this.isLoading = true
      const message = this.currentMessage.trim()

      // 添加用户消息到界面
      this.messages.push({
        role: 'user',
        content: message
      })
      this.currentMessage = ''

      try {
        const response = await sendMessage(this.sessionId, message)
        this.taskId = response.data
        this.isProcessing = true
        this.startQueryResult()
      } catch (error) {
        Message.error('发送失败：' + error.message)
        this.isLoading = false
      }
    },

    // 轮询查询结果
    startQueryResult() {
      this.queryResult()
      this.queryTimer = setInterval(() => {
        this.queryResult()
      }, 2000)
    },

    // 查询结果
    async queryResult() {
      try {
        const response = await queryChatResult(this.taskId)
        if (response.code === 200) {
          const result = response.data
          if (result.status === 'succeeded') {
            // 添加AI回复
            this.messages.push({
              role: 'assistant',
              content: result.answer
            })
            this.isProcessing = false
            this.isLoading = false
            clearInterval(this.queryTimer)
          } else if (result.status === 'failed') {
            Message.error('处理失败：' + result.errorMsg)
            this.isProcessing = false
            this.isLoading = false
            clearInterval(this.queryTimer)
          }
        }
      } catch (error) {
        Message.error('查询失败：' + error.message)
      }
    }
  }
}
</script>

<style scoped>
.chat-container {
  margin-bottom: 20px;
  min-height: 400px;
  max-height: 600px;
  overflow-y: auto;
  padding: 15px;
  width: 100%;
  height: 100%;
  background-image: url('/static/images/dhimage.jpeg');
  background-size: cover;
  background-attachment: fixed;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.chat-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 0;
}

.chat-messages {
  position: relative;
  z-index: 1;
}

.user-message .ai-message {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  padding: 8px 12px;
  margin: 8px 0;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
}

.user-message {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  flex-shrink: 0;
}

.user-message .avatar {
  background-color: #67c23a;
  margin-right: 0;
  margin-left: 10px;
}

.content {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 4px;
  background-color: #a6ffff;
}

.input-card {
  padding: 15px;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.loading-item {
  display: flex;
  margin-bottom: 15px;
}
</style>
