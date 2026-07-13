import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const request = axios.create({ baseURL: '/api', timeout: 12000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('pet_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(response => {
  const body = response.data
  if (body.code === 200) return body.data
  Message.error(body.message || '操作失败')
  return Promise.reject(new Error(body.message || '操作失败'))
}, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('pet_token'); localStorage.removeItem('pet_user')
    if (router.currentRoute.path !== '/login') router.push(`/login?redirect=${encodeURIComponent(router.currentRoute.fullPath)}`)
    Message.warning('请先登录后继续操作')
  } else Message.error('网络连接失败，请确认后端服务已启动')
  return Promise.reject(error)
})

export default request
