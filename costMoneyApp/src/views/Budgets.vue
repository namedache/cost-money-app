<template>
  <div class="budgets-page page">
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div class="budget-list" v-else-if="budgets.length > 0">
      <div
        v-for="budget in budgets"
        :key="budget.id"
        class="budget-card card"
      >
        <div class="budget-info">
          <span class="budget-name">{{ budget.categoryName || '全部' }}</span>
          <span class="budget-period">{{ budget.period === 'monthly' ? '每月' : '每周' }}</span>
        </div>
        <div class="budget-progress">
          <div class="progress-bar">
            <div
              class="progress-fill"
              :class="{ over: budget.progress >= 100 }"
              :style="{ width: Math.min(budget.progress, 100) + '%' }"
            ></div>
          </div>
          <div class="progress-text">
            <span>已用 ¥{{ budget.spent ? budget.spent.toFixed(2) : '0.00' }}</span>
            <span>预算 ¥{{ Number(budget.amount).toFixed(2) }}</span>
          </div>
        </div>
        <button class="delete-btn" @click="handleDelete(budget.id)">×</button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无预算</p>
    </div>

    <button class="btn primary add-btn" @click="showAddModal = true">+ 添加预算</button>

    <!-- 添加弹窗 -->
    <div class="modal-overlay" v-if="showAddModal" @click.self="closeModal">
      <div class="modal-content card">
        <h3>添加预算</h3>
        <div class="form-item">
          <label>分类</label>
          <select v-model="form.categoryId">
            <option :value="null">全部分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>
        <div class="form-item">
          <label>周期</label>
          <select v-model="form.period">
            <option value="monthly">每月</option>
            <option value="weekly">每周</option>
          </select>
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
import dayjs from 'dayjs'

const $alert = inject('$alert')
const $confirm = inject('$confirm')

const budgets = ref([])
const categories = ref([])
const loading = ref(false)
const showAddModal = ref(false)

const form = ref({
  categoryId: null,
  period: 'monthly',
  amount: 0
})

const loadBudgets = async () => {
  loading.value = true
  try {
    const data = await request('/budgets/with-spent')
    budgets.value = data.map(b => ({
      ...b,
      categoryName: b.categoryId ? categories.value.find(c => c.id === b.categoryId)?.name : null
    }))
  } catch (e) {
    console.error('Load budgets failed:', e)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const data = await request('/categories/parents?type=expense')
    categories.value = data
  } catch (e) {
    console.error('Load categories failed:', e)
  }
}

const handleAdd = async () => {
  if (!form.value.amount) {
    $alert({ title: '提示', message: '请输入金额', icon: '⚠️' })
    return
  }

  try {
    await request('/budgets', {
      method: 'POST',
      body: {
        categoryId: form.value.categoryId,
        period: form.value.period,
        amount: Number(form.value.amount),
        type: 'expense',
        alertThreshold: 80,
        isActive: true
      }
    })
    closeModal()
    loadBudgets()
  } catch (e) {
    $alert({ title: '添加失败', message: e.message || '添加失败', icon: '❌' })
  }
}

const handleDelete = (id) => {
  $confirm({
    title: '删除预算',
    message: '确定要删除这条预算吗？',
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      try {
        await request(`/budgets/${id}`, { method: 'DELETE' })
        loadBudgets()
      } catch (e) {
        $alert({ title: '删除失败', message: e.message || '删除失败', icon: '❌' })
      }
    }
  })
}

const closeModal = () => {
  showAddModal.value = false
  form.value = { categoryId: null, period: 'monthly', amount: 0 }
}

onMounted(async () => {
  await loadCategories()
  await loadBudgets()
})
</script>

<style lang="scss" scoped>
.budgets-page {
  padding-bottom: 100px;
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.budget-card {
  position: relative;

  .budget-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;

    .budget-name {
      font-weight: 500;
    }

    .budget-period {
      font-size: 12px;
      color: var(--text-hint);
    }
  }

  .progress-bar {
    height: 8px;
    background: var(--bg-color);
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 6px;

    .progress-fill {
      height: 100%;
      background: var(--primary-color);
      transition: width 0.3s;

      &.over {
        background: var(--accent-expense);
      }
    }
  }

  .progress-text {
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: var(--text-hint);
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