export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

export async function request(endpoint, options = {}) {
  const token = localStorage.getItem('token')

  const config = {
    method: options.method || 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    }
  }

  if (options.body) {
    config.body = JSON.stringify(options.body)
  }

  const response = await fetch(`${API_BASE}${endpoint}`, config)

  if (response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    window.location.href = '/login'
    throw new Error('登录已过期，请重新登录')
  }

  const data = await response.json()

  if (!response.ok) {
    throw new Error(data.message || '请求失败')
  }

  return data
}
