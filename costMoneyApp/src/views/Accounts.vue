<template>
  <div class="accounts-page page">
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div class="account-list" v-else-if="accounts.length > 0">
      <div
        v-for="account in accounts"
        :key="account.id"
        class="account-card card"
        @click="openEdit(account)"
      >
        <div class="account-icon" :style="{ background: account.color || '#1E88E5' }">
          {{ account.icon || '💳' }}
        </div>
        <div class="account-info">
          <div class="account-name">{{ account.name }}</div>
        </div>
        <div class="account-balance" :class="{ negative: account.balance < 0 }">
          ¥{{ Number(account.balance).toFixed(2) }}
        </div>
        <button class="delete-btn" @click.stop="handleDelete(account)">×</button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无账户</p>
    </div>

    <button class="btn primary add-btn" @click="openAdd">+ 添加账户</button>

    <!-- 添加/编辑弹窗 -->
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content card">
        <h3>{{ editTarget ? '编辑账户' : '添加账户' }}</h3>
        <div class="form-item">
          <label>名称</label>
          <input v-model="form.name" type="text" placeholder="如：支付宝" />
        </div>
        <div class="form-item">
          <label>余额</label>
          <input v-model="form.balance" type="number" placeholder="0.00" />
        </div>
        <div class="form-item icon-picker">
          <label>图标</label>
          <div class="emoji-grid">
            <button
              v-for="emoji in iconOptions"
              :key="emoji"
              class="emoji-btn"
              :class="{ selected: form.icon === emoji }"
              @click="form.icon = emoji"
            >{{ emoji }}</button>
          </div>
        </div>
        <div class="form-item">
          <label>颜色</label>
          <div class="color-grid">
            <button
              v-for="color in colorOptions"
              :key="color"
              class="color-btn"
              :class="{ selected: form.color === color }"
              :style="{ background: color }"
              @click="form.color = color"
            ></button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button class="btn primary" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useAccountStore } from '@/stores/account'

const accountStore = useAccountStore()
const $confirm = inject('$confirm')
const $alert = inject('$alert')

const accounts = ref([])
const loading = ref(false)
const showModal = ref(false)
const editTarget = ref(null)

const iconOptions = ['💳', '💰', '🏦', '📱', '💵', 'itcoin', '📊', '🏠', '👛', '🏧']
const colorOptions = ['#1E88E5', '#43A047', '#FB8C00', '#E53935', '#8E24AA', '#00ACC1', '#6D4C41', '#546E7A', '#FF6B6B', '#3949AB']

const form = ref({
  name: '',
  balance: 0,
  icon: '💳',
  color: '#1E88E5'
})

const loadAccounts = async () => {
  loading.value = true
  try {
    accounts.value = await accountStore.getList()
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  editTarget.value = null
  form.value = { name: '', balance: 0, icon: '💳', color: '#1E88E5' }
  showModal.value = true
}

const openEdit = (account) => {
  editTarget.value = account
  form.value = {
    name: account.name,
    balance: account.balance,
    icon: account.icon || '💳',
    color: account.color || '#1E88E5'
  }
  showModal.value = true
}

const handleSave = async () => {
  if (!form.value.name) {
    $alert({ title: '提示', message: '请输入账户名称', icon: '⚠️' })
    return
  }

  try {
    if (editTarget.value) {
      await accountStore.update(editTarget.value.id, {
        ...editTarget.value,
        ...form.value,
        balance: Number(form.value.balance)
      })
    } else {
      await accountStore.create(form.value)
    }
    closeModal()
    loadAccounts()
  } catch (e) {
    $alert({ title: '保存失败', message: e.message || '保存失败', icon: '❌' })
  }
}

const handleDelete = (account) => {
  $confirm({
    title: '删除账户',
    message: `确定要删除"${account.name}"吗？关联的记录不会被删除。`,
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      await accountStore.remove(account.id)
      loadAccounts()
    }
  })
}

const closeModal = () => {
  showModal.value = false
  editTarget.value = null
}

onMounted(() => {
  loadAccounts()
})
</script>

<style lang="scss" scoped>
.accounts-page {
  padding-bottom: 100px;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-card {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  cursor: pointer;

  .account-icon {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
  }

  .account-info {
    flex: 1;

    .account-name {
      font-size: 15px;
      font-weight: 500;
    }
  }

  .account-balance {
    font-size: 16px;
    font-weight: 600;

    &.negative {
      color: var(--accent-expense);
    }
  }

  .delete-btn {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: var(--bg-color);
    color: var(--text-hint);
    font-size: 16px;
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  color: var(--text-hint);
  font-size: 14px;
  gap: 12px;

  .loading-spinner {
    width: 28px;
    height: 28px;
    border: 3px solid var(--border-color);
    border-top-color: var(--xhs-red);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.add-btn {
  width: 100%;
  margin-top: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 400px;
  padding: 24px;

  h3 {
    font-size: 18px;
    margin-bottom: 20px;
    text-align: center;
  }

  .form-item {
    margin-bottom: 16px;

    label {
      display: block;
      font-size: 13px;
      color: var(--text-secondary);
      margin-bottom: 6px;
    }

    input, select {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      font-size: 14px;
    }
  }

  .emoji-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .emoji-btn {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      background: var(--bg-color);

      &.selected {
        background: #FFF0F0;
        border: 2px solid var(--xhs-red);
      }
    }
  }

  .color-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .color-btn {
      width: 32px;
      height: 32px;
      border-radius: 50%;

      &.selected {
        border: 3px solid var(--text-primary);
      }
    }
  }

  .modal-actions {
    display: flex;
    gap: 12px;
    margin-top: 20px;

    .btn {
      flex: 1;
      padding: 12px;
    }
  }
}
</style>
