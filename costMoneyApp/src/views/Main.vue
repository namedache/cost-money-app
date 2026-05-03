<template>
  <div class="main-layout">
    <header class="header">
      <div class="header-left" v-if="showBack">
        <button class="back-btn" @click="goBack">
          <svg class="back-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>
      </div>
      <h1 class="header-title">{{ title }}</h1>
      <div class="header-right">
        <slot name="right" />
      </div>
    </header>
    <main class="content">
      <router-view />
    </main>
    <nav class="tab-bar">
      <!-- 首页 -->
      <router-link to="/" class="tab-item" :class="{ active: $route.path === '/' }">
        <svg class="tab-icon" viewBox="0 0 24 24" :fill="isActive('/') ? 'currentColor' : 'none'" :stroke="currentColor" stroke-width="2">
          <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
          <polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        <span class="tab-label">首页</span>
      </router-link>

      <!-- 账单 -->
      <router-link to="/records" class="tab-item" :class="{ active: $route.path === '/records' }">
        <svg class="tab-icon" viewBox="0 0 24 24" :fill="isActive('/records') ? 'currentColor' : 'none'" :stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/>
          <polyline points="10 9 9 9 8 9"/>
        </svg>
        <span class="tab-label">账单</span>
      </router-link>

      <!-- 记一笔（中间突出按钮） -->
      <router-link to="/add" class="tab-item center-tab" :class="{ active: $route.path === '/add' }">
        <div class="center-btn">
          <svg class="plus-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </div>
        <span class="tab-label center-label">记一笔</span>
      </router-link>

      <!-- 统计 -->
      <router-link to="/statistics" class="tab-item" :class="{ active: $route.path === '/statistics' }">
        <svg class="tab-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <rect x="4" y="14" width="4" height="7"/>
          <rect x="10" y="9" width="4" height="12"/>
          <rect x="16" y="4" width="4" height="17"/>
        </svg>
        <span class="tab-label">统计</span>
      </router-link>

      <!-- 我的 -->
      <router-link to="/me" class="tab-item" :class="{ active: $route.path === '/me' }">
        <svg class="tab-icon" viewBox="0 0 24 24" :fill="isActive('/me') ? 'currentColor' : 'none'" :stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        <span class="tab-label">我的</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const isActive = (path) => route.path === path

const showBack = computed(() => {
  const noBack = ['/', '/add', '/statistics', '/me', '/records']
  return !noBack.includes(route.path)
})

const title = computed(() => {
  if (route.path.startsWith('/edit/')) return '编辑记录'
  const titles = {
    '/': '记账本',
    '/add': '记一笔',
    '/statistics': '统计',
    '/me': '我的',
    '/records': '账单',
    '/accounts': '账户管理',
    '/categories': '分类管理',
    '/budgets': '预算管理',
    '/auto-rules': '自动记账',
    '/quick-records': '快捷记账'
  }
  return titles[route.path] || '记账本'
})

const goBack = () => router.back()
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-color);

  &-left, &-right {
    width: 50px;
  }

  &-title {
    font-size: 17px;
    font-weight: 600;
    text-align: center;
    color: var(--text-primary);
  }

  &-right {
    text-align: right;
  }
}

.back-btn {
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;

  .back-icon {
    width: 22px;
    height: 22px;
    color: var(--text-primary);
  }
}

.content {
  flex: 1;
  padding: 0 16px;
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 68px;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 6px 4px 0;
  padding-bottom: env(safe-area-inset-bottom, 6px);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 2px;
  color: #8e8e93;
  font-size: 10px;
  transition: all 0.2s;
  padding: 4px 6px;
  min-width: 52px;

  &.active {
    color: var(--xhs-red);

    .tab-icon {
      transform: scale(1.08);
    }
  }

  .tab-icon {
    width: 24px;
    height: 24px;
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
  }

  .tab-label {
    font-weight: 500;
    letter-spacing: 0.2px;
  }
}

.center-tab {
  position: relative;

  .center-btn {
    width: 54px;
    height: 54px;
    background: linear-gradient(135deg, var(--xhs-red) 0%, #ff4d6a 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: -22px;
    box-shadow: 0 4px 16px rgba(254, 44, 85, 0.4);

    .plus-icon {
      width: 26px;
      height: 26px;
      color: #fff;
    }
  }

  &:active .center-btn {
    transform: scale(0.9);
    box-shadow: 0 2px 8px rgba(254, 44, 85, 0.3);
  }

  .tab-label {
    margin-top: 4px;
  }

  .center-label {
    color: var(--xhs-red);
    font-weight: 600;
  }

  &.active .center-label {
    color: var(--xhs-red);
  }
}
</style>
