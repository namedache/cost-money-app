<template>
  <div class="register-page page">
    <div class="logo">
      <h1>记账本</h1>
      <p>开启你的记账之旅</p>
    </div>
    <div class="form-card card">
      <div class="form-item">
        <input
          v-model="form.username"
          type="text"
          placeholder="请输入用户名"
          class="input"
        />
      </div>
      <div class="form-item">
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          class="input"
        />
      </div>
      <div class="form-item">
        <input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请确认密码"
          class="input"
        />
      </div>
      <button class="btn primary btn-block" @click="handleRegister" :disabled="loading">
        {{ loading ? '注册中...' : '注册' }}
      </button>
      <div class="links">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const $alert = inject('$alert')

const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)

const handleRegister = async () => {
  if (!form.value.username || !form.value.password) {
    $alert({ title: '提示', message: '请输入用户名和密码', icon: '⚠️' })
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    $alert({ title: '提示', message: '两次密码输入不一致', icon: '⚠️' })
    return
  }
  loading.value = true
  try {
    await userStore.register(form.value.username, form.value.password)
    router.push('/')
  } catch (e) {
    $alert({ title: '注册失败', message: e.message || '注册失败', icon: '❌' })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px 20px;
}

.logo {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    font-size: 32px;
    color: var(--primary-color);
    margin-bottom: 8px;
  }

  p {
    color: var(--text-secondary);
    font-size: 14px;
  }
}

.form-card {
  .form-item {
    margin-bottom: 16px;
  }

  .input {
    width: 100%;
    padding: 14px 16px;
    border: 1px solid var(--border-color);
    border-radius: var(--radius-sm);
    font-size: 16px;
    outline: none;
    transition: border-color 0.2s;

    &:focus {
      border-color: var(--primary-color);
    }
  }

  .btn-block {
    width: 100%;
    padding: 14px;
    font-size: 16px;
    margin-top: 8px;
  }

  .links {
    text-align: center;
    margin-top: 16px;

    a {
      color: var(--primary-color);
      font-size: 14px;
    }
  }
}
</style>
