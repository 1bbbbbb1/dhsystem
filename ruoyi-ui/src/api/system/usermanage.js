import request from '@/utils/request'

// 查询用户信息列表
export function listUsermanage(query) {
  return request({
    url: '/system/usermanage/list',
    method: 'get',
    params: query
  })
}

// 查询用户信息详细
export function getUsermanage(userId) {
  return request({
    url: '/system/usermanage/' + userId,
    method: 'get'
  })
}

// 新增用户信息
export function addUsermanage(data) {
  return request({
    url: '/system/usermanage',
    method: 'post',
    data: data
  })
}

// 修改用户信息
export function updateUsermanage(data) {
  return request({
    url: '/system/usermanage',
    method: 'put',
    data: data
  })
}

// 删除用户信息
export function delUsermanage(userId) {
  return request({
    url: '/system/usermanage/' + userId,
    method: 'delete'
  })
}
