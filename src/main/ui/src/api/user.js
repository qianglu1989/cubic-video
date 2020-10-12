import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/video/user/valid',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/video/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/video/user/logout',
    method: 'post'
  })
}
