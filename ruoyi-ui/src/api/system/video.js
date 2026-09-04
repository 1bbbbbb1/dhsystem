import request from '@/utils/request'

// 创建视频生成任务
export function createVideoTask(prompt) {
  return request({
    url: '/system/video/createTask',
    method: 'post',
    params: { prompt }
  })
}

// 查询视频任务状态
export function queryVideoTask(taskId) {
  return request({
    url: '/system/video/queryTask',
    method: 'get',
    params: { taskId }
  })
}

// 取消视频任务
export function cancelVideoTask(taskId) {
  return request({
    url: '/system/video/cancelTask',
    method: 'post',
    params: { taskId }
  })
}
