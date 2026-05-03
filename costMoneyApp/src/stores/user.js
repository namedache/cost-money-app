import { defineStore } from 'pinia'
import { ref } from 'vue'
import { API_BASE } from '@/utils/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  // Restore userInfo from localStorage on init
  const savedUser = localStorage.getItem('userInfo')
  if (savedUser) {
    try { userInfo.value = JSON.parse(savedUser) } catch {}
  }

  const login = async (username, password) => {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    const data = await res.json()
    if (!res.ok) {
      throw new Error(data.message || '登录失败')
    }
    token.value = data.token
    userInfo.value = { id: data.userId, username: data.username }
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const register = async (username, password) => {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    const data = await res.json()
    if (!res.ok) {
      throw new Error(data.message || '注册失败')
    }
    token.value = data.token
    userInfo.value = { id: data.userId, username: data.username }
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userInfo')
  }

  const getUserId = () => {
    return localStorage.getItem('userId')
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    token,
    userInfo,
    login,
    register,
    logout,
    getUserId,
    isLoggedIn
  }
})
