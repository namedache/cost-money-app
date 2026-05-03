<template>
  <div class="quick-records-page page">
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div class="quick-grid" v-else-if="quickRecords.length > 0">
      <div
        v-for="item in quickRecords"
        :key="item.id"
        class="quick-item"
        :style="{ background: item.color }"
      >
        <span class="quick-icon">{{ item.icon }}</span>
        <span class="quick-name">{{ item.name }}</span>
        <span class="quick-amount">¥{{ Number(item.amount).toFixed(2) }}</span>
        <button class="edit-btn" @click="handleEdit(item)">✏️</button>
        <button class="delete-btn" @click="handleDelete(item.id)">×</button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无快捷记账</p>
    </div>

    <button class="btn primary add-btn" @click="showAddModal = true">+ 添加快捷方式</button>

    <!-- 添加弹窗 -->
    <div class="modal-overlay" v-if="showAddModal" @click.self="closeModal">
      <div class="modal-content card">
        <h3>{{ editingId ? '编辑' : '添加' }}快捷方式</h3>
        <div class="form-item">
          <label>名称</label>
          <input v-model="form.name" type="text" placeholder="如：早餐" />
        </div>
        <div class="form-item">
          <label>金额</label>
          <input v-model="form.amount" type="number" placeholder="0.00" />
        </div>
        <div class="form-item">
          <label>图标</label>
          <input v-model="form.icon" type="text" placeholder="如：🥐" />
        </div>
        <div class="form-item">
          <label>颜色</label>
          <input v-model="form.color" type="color" />
        </div>
        <div class="form-item">
          <label>分类</label>
          <select v-model="form.category_id">
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.icon }} {{ cat.name }}
            </option>
          </select>
        </div>
        <div class="form-item">
          <label>账户</label>
          <select v-model="form.account_id">
            <option v-for="acc in accounts" :key="acc.id" :value="acc.id">
              {{ acc.icon }} {{ acc.name }}
            </option>
          </select>
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
import { useQuickRecordStore } from '@/stores/quickRecord'
import { useAccountStore } from '@/stores/account'
import { request } from '@/utils/api'

const quickStore = useQuickRecordStore()
const accountStore = useAccountStore()
const $alert = inject('$alert')
const $confirm = inject('$confirm')

const quickRecords = ref([])
const categories = ref([])
const accounts = ref([])
const loading = ref(false)
const showAddModal = ref(false)
const editingId = ref(null)

const form = ref({
  name: '',
  amount: 0,
  icon: '⚡',
  color: '#1E88E5',
  category_id: null,
  account_id: null
})

const loadData = async () => {
  loading.value = true
  try {
    await quickStore.getList()
    quickRecords.value = quickStore.quickRecords

    const [catsData, accsData] = await Promise.all([
      request('/categories?type=expense'),
      request('/accounts')
    ])
    categories.value = catsData.filter(c => c.parentId !== null)
    accounts.value = accsData

    if (accounts.value.length > 0 && !form.value.account_id) {
      form.value.account_id = accounts.value[0].id
    }
    if (categories.value.length > 0 && !form.value.category_id) {
      form.value.category_id = categories.value[0].id
    }
  } catch (e) {
    console.error('Load data failed:', e)
  } finally {
    loading.value = false
  }
}

const handleEdit = (item) => {
  editingId.value = item.id
  form.value = { ...item }
  showAddModal.value = true
}

const handleDelete = (id) => {
  $confirm({
    title: '删除快捷方式',
    message: '确定要删除这个快捷方式吗？',
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      await quickStore.remove(id)
      loadData()
    }
  })
}

const closeModal = () => {
  showAddModal.value = false
  editingId.value = null
  form.value = { name: '', amount: 0, icon: '⚡', color: '#1E88E5', category_id: null, account_id: null }
}

const handleSave = async () => {
  if (!form.value.name || !form.value.amount) {
    $alert({ title: '提示', message: '请填写名称和金额', icon: '⚠️' })
    return
  }

  if (editingId.value) {
    await quickStore.update(editingId.value, form.value)
  } else {
    await quickStore.create(form.value)
  }
  closeModal()
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.quick-records-page {
  padding-bottom: 100px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-item {
  padding: 16px;
  border-radius: var(--radius-md);
  color: #fff;
  position: relative;
  cursor: pointer;

  .quick-icon {
    font-size: 28px;
    display: block;
    margin-bottom: 8px;
  }

  .quick-name {
    font-size: 14px;
    display: block;
    margin-bottom: 4px;
  }

  .quick-amount {
    font-size: 12px;
    opacity: 0.9;
  }

  .edit-btn, .delete-btn {
    position: absolute;
    right: 8px;
    top: 8px;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: rgba(255,255,255,0.3);
    font-size: 12px;
    opacity: 0.7;
    transition: opacity 0.2s;

    &:active { opacity: 1; }
  }

  .delete-btn {
    top: 36px;
  }

  &:hover .edit-btn,
  &:hover .delete-btn {
    opacity: 1;
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