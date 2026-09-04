import request from '@/utils/request'

// 提交视觉问答任务
export function submitChatQuestion(data) {
  return request({
    url: '/system/chat/ask',
    method: 'post',
    params: { question: data.question }
  })
}

// 查询问答结果
export function queryChatResult(taskId) {
  return request({
    url: `/system/chat/query/${taskId}`,
    method: 'get'
  })
}

// 创建会话
export function createSession() {
  return request({
    url: '/system/chat/session',
    method: 'post'
  })
}

// 发送消息
export function sendMessage(sessionId, message) {
  return request({
    url: '/system/chat/send',
    method: 'post',
    params: { sessionId, message }
  })
}

// 获取历史消息
export function getSessionHistory(sessionId) {
  return request({
    url: `/system/chat/history/${sessionId}`,
    method: 'get'
  })
}
