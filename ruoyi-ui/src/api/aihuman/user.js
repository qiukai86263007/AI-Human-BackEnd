import request from '@/utils/request'

// 查询用户管理列表
export function listUser(query) {
  return request({
    url: '/aihuman/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户管理详细
export function getUser(userId) {
  return request({
    url: '/aihuman/user/' + userId,
    method: 'get'
  })
}

// 新增用户管理
export function addUser(data) {
  return request({
    url: '/aihuman/user',
    method: 'post',
    data: data
  })
}

// 修改用户管理
export function updateUser(data) {
  return request({
    url: '/aihuman/user',
    method: 'put',
    data: data
  })
}

// 删除用户管理
export function delUser(userId) {
  return request({
    url: '/aihuman/user/' + userId,
    method: 'delete'
  })
}
