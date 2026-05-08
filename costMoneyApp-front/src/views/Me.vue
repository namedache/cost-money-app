<template>
  <div class="me-page page">
    <!-- 个人主页头部 -->
    <div class="profile-header">
      <div class="profile-bg"></div>
      <div class="profile-content">
        <div class="avatar-wrapper">
          <span class="avatar">{{ userStore.userInfo?.username?.[0]?.toUpperCase() || 'U' }}</span>
        </div>
        <div class="profile-info">
          <span class="username">{{ userStore.userInfo?.username || '未登录' }}</span>
          <span class="user-id" v-if="userStore.userInfo">ID: {{ userStore.userInfo.id }}</span>
        </div>
      </div>

      <!-- 数据成就 -->
      <div class="achievements">
        <div class="achievement-item">
          <span class="achievement-value">{{ totalRecords }}</span>
          <span class="achievement-label">记账天数</span>
        </div>
        <div class="achievement-divider"></div>
        <div class="achievement-item">
          <span class="achievement-value">{{ streakDays }}</span>
          <span class="achievement-label">连续记账</span>
        </div>
        <div class="achievement-divider"></div>
        <div class="achievement-item">
          <span class="achievement-value">¥{{ formatMoney(totalAmount) }}</span>
          <span class="achievement-label">累计金额</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单卡片 -->
    <div class="menu-cards">
      <router-link to="/accounts" class="menu-card">
        <span class="card-icon">💳</span>
        <span class="card-title">账户管理</span>
        <span class="card-desc">管理你的钱包</span>
      </router-link>
      <router-link to="/categories" class="menu-card">
        <span class="card-icon">📑</span>
        <span class="card-title">分类管理</span>
        <span class="card-desc">自定义分类</span>
      </router-link>
      <router-link to="/quick-records" class="menu-card">
        <span class="card-icon">⚡</span>
        <span class="card-title">快捷记账</span>
        <span class="card-desc">一键记常用</span>
      </router-link>
      <router-link to="/budgets" class="menu-card">
        <span class="card-icon">🎯</span>
        <span class="card-title">预算管理</span>
        <span class="card-desc">设置每月预算</span>
      </router-link>
    </div>

    <!-- 导出功能 -->
    <div class="export-section">
      <div class="section-header">
        <span class="section-title">📤 导出账单</span>
      </div>
      <div class="export-buttons">
        <button class="export-btn" @click="exportData('excel')">
          <span class="export-icon">📊</span>
          <span>Excel</span>
        </button>
        <button class="export-btn" @click="exportData('csv')">
          <span class="export-icon">📄</span>
          <span>CSV</span>
        </button>
        <button class="export-btn" @click="exportData('pdf')">
          <span class="export-icon">📰</span>
          <span>PDF</span>
        </button>
      </div>
    </div>

    <!-- 设置列表 -->
    <div class="menu-list">
      <div class="menu-item" @click="handleClearData">
        <span class="menu-icon">🗑️</span>
        <span class="menu-text">清除本地数据</span>
        <span class="arrow">→</span>
      </div>
      <div class="menu-item danger" @click="handleLogout">
        <span class="menu-icon">🚪</span>
        <span class="menu-text">退出登录</span>
        <span class="arrow">→</span>
      </div>
    </div>

    <!-- 版本信息 -->
    <div class="version-info">
      <span>记账本 v1.0</span>
      <span class="copyright">made with ❤️</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { request, API_BASE } from '@/utils/api'

const router = useRouter()
const userStore = useUserStore()
const $alert = inject('$alert')
const $confirm = inject('$confirm')

const totalRecords = ref(0)
const streakDays = ref(0)
const totalAmount = ref(0)

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) : '0'
}

const loadUserStats = async () => {
  try {
    // 获取总记录数和累计金额
    const records = await request('/records')
    totalRecords.value = records.length

    // 计算总金额
    let total = 0
    records.forEach(r => {
      if (r.type === 'income') {
        total += Number(r.amount)
      } else {
        total -= Number(r.amount)
      }
    })
    totalAmount.value = Math.abs(total)

    // 模拟连续记账天数（实际应从后端获取）
    streakDays.value = Math.min(records.length, 7)
  } catch (e) {
    console.error('Load user stats failed:', e)
  }
}

const handleLogout = () => {
  $confirm({
    title: '退出登录',
    message: '确定退出登录？',
    icon: '🚪',
    confirmText: '退出',
    danger: true,
    onConfirm: () => {
      userStore.logout()
      router.push('/login')
    }
  })
}

const handleClearData = async () => {
  $confirm({
    title: '清除数据',
    message: '确定清除所有本地数据？此操作不可恢复！',
    icon: '🗑️',
    confirmText: '清除',
    danger: true,
    onConfirm: () => {
      localStorage.clear()
      indexedDB.deleteDatabase('costMoneyApp')
      window.location.reload()
    }
  })
}

const exportData = async (type) => {
  if (type === 'csv') {
    const startDate = '2000-01-01'
    const endDate = new Date().toISOString().slice(0, 10)
    const token = localStorage.getItem('token')
    const url = `${API_BASE}/export/csv?startDate=${startDate}&endDate=${endDate}`
    const link = document.createElement('a')
    link.href = url
    link.download = `records_${startDate}_${endDate}.csv`
    // Use fetch with auth header to download
    try {
      const res = await fetch(url, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (res.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
        return
      }
      const blob = await res.blob()
      const blobUrl = URL.createObjectURL(blob)
      link.href = blobUrl
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(blobUrl)
    } catch (e) {
      $alert({ title: '导出失败', message: e.message, icon: '❌' })
    }
  } else {
    $alert({
      title: '导出功能',
      message: `${type.toUpperCase()} 导出功能即将上线`,
      icon: '📝'
    })
  }
}

onMounted(() => {
  loadUserStats()
})
</script>

<style lang="scss" scoped>
.me-page {
  // padding is handled by .page class in global styles
}

.profile-header {
  background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F0 100%);
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
  padding: 60px 16px 20px;
  margin: -16px -16px 20px;
  position: relative;
  overflow: hidden;
}

.profile-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100px;
  background: linear-gradient(135deg, var(--xhs-red) 0%, var(--xhs-red-light) 100%);
  border-radius: 0 0 50px 50px;
  z-index: 0;
}

.profile-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;

  .avatar-wrapper {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--xhs-red) 0%, var(--xhs-red-light) 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(254, 44, 85, 0.3);
    border: 3px solid #fff;
  }

  .avatar {
    font-size: 24px;
    font-weight: 700;
    color: #fff;
  }

  .profile-info {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .username {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .user-id {
      font-size: 12px;
      color: var(--text-hint);
    }
  }
}

.achievements {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow);
}

.achievement-item {
  flex: 1;
  text-align: center;

  .achievement-value {
    display: block;
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 2px;
  }

  .achievement-label {
    font-size: 11px;
    color: var(--text-hint);
  }
}

.achievement-divider {
  width: 1px;
  height: 32px;
  background: var(--border-color);
}

.menu-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.menu-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: var(--shadow);
  transition: transform 0.2s;

  &:active {
    transform: scale(0.98);
  }

  .card-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }

  .card-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 4px;
  }

  .card-desc {
    font-size: 11px;
    color: var(--text-hint);
  }
}

.export-section {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;

  .section-header {
    margin-bottom: 12px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
    }
  }

  .export-buttons {
    display: flex;
    gap: 12px;
  }

  .export-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    padding: 16px 12px;
    background: var(--bg-color);
    border-radius: var(--radius-md);
    transition: all 0.2s;

    &:active {
      transform: scale(0.98);
    }

    .export-icon {
      font-size: 24px;
    }

    span:last-child {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }
}

.menu-list {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  margin-bottom: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.2s;

  &:active {
    background: var(--bg-color);
  }

  &:last-child {
    border-bottom: none;
  }

  .menu-icon {
    font-size: 20px;
    margin-right: 12px;
  }

  .menu-text {
    flex: 1;
    font-size: 14px;
    color: var(--text-primary);
  }

  .arrow {
    color: var(--text-hint);
    font-size: 14px;
  }

  &.danger {
    .menu-text { color: var(--accent-expense); }
  }
}

.version-info {
  text-align: center;
  padding: 20px;
  font-size: 12px;
  color: var(--text-hint);

  .copyright {
    margin-left: 8px;
  }
}
</style>
