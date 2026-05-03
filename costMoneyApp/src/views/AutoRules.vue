<template>
  <div class="auto-rules-page page">
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div class="rule-list" v-else-if="rules.length > 0">
      <div
        v-for="rule in rules"
        :key="rule.id"
        class="rule-card card"
      >
        <div class="rule-info">
          <span class="rule-name">{{ rule.name }}</span>
          <span class="rule-keyword" v-if="rule.keyword">关键词: {{ rule.keyword }}</span>
        </div>
        <div class="rule-amount">
          <span :class="rule.amount < 0 ? 'expense' : 'income'">
            ¥{{ Number(rule.amount).toFixed(2) }}
          </span>
        </div>
        <div class="rule-status">
          <button
            class="toggle-btn"
            :class="{ active: rule.isActive }"
            @click="toggleRule(rule)"
          >
            {{ rule.isActive ? '已启用' : '已禁用' }}
          </button>
        </div>
        <button class="delete-btn" @click="handleDelete(rule.id)">×</button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无自动记账规则</p>
    </div>

    <button class="btn primary add-btn" @click="showAddModal = true">+ 添加规则</button>

    <!-- 添加弹窗 -->
    <div class="modal-overlay" v-if="showAddModal" @click.self="closeModal">
      <div class="modal-content card">
        <h3>添加自动记账规则</h3>
        <div class="form-item">
          <label>规则名称</label>
          <input v-model="form.name" type="text" placeholder="如：每天咖啡" />
        </div>
        <div class="form-item">
          <label>关键词</label>
          <input v-model="form.keyword" type="text" placeholder="如：咖啡" />
        </div>
        <div class="form-item">
          <label>金额</label>
          <input v-model="form.amount" type="number" placeholder="0.00" />
        </div>
        <div class="modal-actions">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button class="btn primary" @click="handleAdd">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { request } from '@/utils/api'

const $alert = inject('$alert')
const $confirm = inject('$confirm')

const rules = ref([])
const loading = ref(false)
const showAddModal = ref(false)

const form = ref({
  name: '',
  keyword: '',
  amount: 0
})

const loadRules = async () => {
  loading.value = true
  try {
    rules.value = await request('/auto-rules')
  } catch (e) {
    console.error('Load rules failed:', e)
  } finally {
    loading.value = false
  }
}

const handleAdd = async () => {
  if (!form.value.name || !form.value.amount) {
    $alert({ title: '提示', message: '请填写完整信息', icon: '⚠️' })
    return
  }

  try {
    await request('/auto-rules', {
      method: 'POST',
      body: {
        name: form.value.name,
        keyword: form.value.keyword,
        amount: Number(form.value.amount),
        categoryId: 1,
        accountId: 1
      }
    })
    closeModal()
    loadRules()
  } catch (e) {
    $alert({ title: '添加失败', message: e.message || '添加失败', icon: '❌' })
  }
}

const toggleRule = async (rule) => {
  try {
    await request(`/auto-rules/${rule.id}`, {
      method: 'PUT',
      body: {
        ...rule,
        isActive: !rule.isActive
      }
    })
    loadRules()
  } catch (e) {
    $alert({ title: '更新失败', message: e.message || '更新失败', icon: '❌' })
  }
}

const handleDelete = (id) => {
  $confirm({
    title: '删除规则',
    message: '确定要删除这条自动记账规则吗？',
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      try {
        await request(`/auto-rules/${id}`, { method: 'DELETE' })
        loadRules()
      } catch (e) {
        $alert({ title: '删除失败', message: e.message || '删除失败', icon: '❌' })
      }
    }
  })
}

const closeModal = () => {
  showAddModal.value = false
  form.value = { name: '', keyword: '', amount: 0 }
}

onMounted(() => {
  loadRules()
})
</script>

<style lang="scss" scoped>
.auto-rules-page {
  padding-bottom: 100px;
}

.rule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rule-card {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;

  .rule-info {
    flex: 1;

    .rule-name {
      font-weight: 500;
      display: block;
    }

    .rule-keyword {
      font-size: 12px;
      color: var(--text-hint);
    }
  }

  .rule-amount {
    font-weight: 600;

    .expense { color: var(--accent-expense); }
    .income { color: var(--accent-income); }
  }

  .toggle-btn {
    padding: 6px 12px;
    border-radius: 12px;
    font-size: 12px;
    background: var(--bg-color);
    color: var(--text-hint);

    &.active {
      background: var(--primary-color);
      color: #fff;
    }
  }

  .delete-btn {
    position: absolute;
    right: 8px;
    top: 8px;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: var(--bg-color);
    color: var(--text-hint);
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