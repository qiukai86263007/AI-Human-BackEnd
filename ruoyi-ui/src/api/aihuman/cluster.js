import request from '@/utils/request'

// 查询GPU集群管理列表
export function listCluster(query) {
  return request({
    url: '/aihuman/cluster/list',
    method: 'get',
    params: query
  })
}

// 查询GPU集群管理详细
export function getCluster(clusterId) {
  return request({
    url: '/aihuman/cluster/' + clusterId,
    method: 'get'
  })
}

// 新增GPU集群管理
export function addCluster(data) {
  return request({
    url: '/aihuman/cluster',
    method: 'post',
    data: data
  })
}

// 修改GPU集群管理
export function updateCluster(data) {
  return request({
    url: '/aihuman/cluster',
    method: 'put',
    data: data
  })
}

// 删除GPU集群管理
export function delCluster(clusterId) {
  return request({
    url: '/aihuman/cluster/' + clusterId,
    method: 'delete'
  })
}
