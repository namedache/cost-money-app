<template>
  <div class="add-record-page page">
    <!-- 顶部金额输入区 -->
    <div class="amount-section">
      <div class="type-toggle">
        <button
          class="toggle-btn"
          :class="{ active: form.type === 'expense' }"
          @click="form.type = 'expense'"
        >支出</button>
        <button
          class="toggle-btn"
          :class="{ active: form.type === 'income' }"
          @click="form.type = 'income'"
        >收入</button>
      </div>

      <div class="amount-display">
        <span class="currency">¥</span>
        <input
          v-model="form.amount"
          type="number"
          placeholder="0"
          class="amount-input"
        />
      </div>
    </div>

    <!-- 分类选择（横向滚动） -->
    <div class="category-section">
      <div class="section-label">选择分类</div>
      <div class="category-scroll">
        <div
          v-for="cat in parentCategories"
          :key="cat.id"
          class="category-pill"
          :class="{ selected: form.category_id === cat.id }"
          @click="selectCategory(cat)"
        >
          <span class="pill-icon">{{ cat.icon }}</span>
          <span class="pill-name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- 子分类选择（如果有） -->
    <div class="category-section child" v-if="currentParentId">
      <div class="section-label">选择子分类</div>
      <div class="category-scroll">
        <div
          v-for="cat in childCategories"
          :key="cat.id"
          class="category-pill"
          :class="{ selected: form.category_id === cat.id }"
          @click="selectChildCategory(cat)"
        >
          <span class="pill-icon">{{ cat.icon }}</span>
          <span class="pill-name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- 表单信息 -->
    <div class="form-section">
      <div class="form-item" @click="showAccountPicker = true">
        <span class="label-icon">💳</span>
        <span class="label">账户</span>
        <div class="value" v-if="selectedAccount">
          <span>{{ selectedAccount.name }}</span>
        </div>
        <span class="arrow" v-else>请选择</span>
      </div>

      <div class="form-item" @click="showDatePicker = true">
        <span class="label-icon">📅</span>
        <span class="label">日期</span>
        <div class="value">
          <span>{{ form.date }}</span>
        </div>
      </div>

      <div class="form-item remark-item">
        <span class="label-icon">📝</span>
        <span class="label">备注</span>
        <input
          v-model="form.note"
          type="text"
          placeholder="写点啥？比如：今天奖励自己一杯奶茶🍵"
          class="note-input"
        />
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="submit-section">
      <button
        v-if="isEditMode"
        class="btn delete-btn"
        @click="handleDelete"
      >
        删除记录
      </button>
      <button
        class="btn submit-btn"
        :disabled="!canSubmit || isSubmitting"
        @click="handleSubmit"
      >
        <span class="btn-text">{{ isEditMode ? '保存修改' : '记一笔' }}</span>
        <span class="btn-arrow">→</span>
      </button>
    </div>

    <!-- 账户选择器 -->
    <div class="picker-overlay" v-if="showAccountPicker" @click.self="showAccountPicker = false">
      <div class="picker-sheet">
        <div class="picker-header">
          <span>选择账户</span>
          <button @click="showAccountPicker = false">完成</button>
        </div>
        <div class="picker-content">
          <div class="account-list">
            <div
              v-for="acc in accounts"
              :key="acc.id"
              class="account-item"
              :class="{ selected: form.account_id === acc.id }"
              @click="selectAccount(acc)"
            >
              <span class="acc-icon" :style="{ background: acc.color }">{{ acc.icon }}</span>
              <span class="acc-name">{{ acc.name }}</span>
              <span class="acc-balance">¥{{ Number(acc.balance).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 日期选择器 -->
    <div class="picker-overlay" v-if="showDatePicker" @click.self="showDatePicker = false">
      <div class="picker-sheet">
        <div class="picker-header">
          <span>选择日期</span>
          <button @click="showDatePicker = false">完成</button>
        </div>
        <div class="picker-content">
          <input type="date" v-model="form.date" class="date-input" />
        </div>
      </div>
    </div>

    <!-- 记账成功弹窗 -->
    <SuccessModal :show="showSuccess" @close="handleSuccessClose" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useRecordStore } from '@/stores/record'
import { useAccountStore } from '@/stores/account'
import { request } from '@/utils/api'
import SuccessModal from '@/components/common/SuccessModal.vue'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const recordStore = useRecordStore()
const accountStore = useAccountStore()
const $confirm = inject('$confirm')
const $alert = inject('$alert')

const isEditMode = computed(() => !!route.params.id)
const isSubmitting = ref(false)

const form = ref({
  type: 'expense',
  amount: '',
  category_id: null,
  account_id: null,
  date: dayjs().format('YYYY-MM-DD'),
  note: ''
})

const selectedCategory = ref(null)
const selectedAccount = ref(null)
const parentCategories = ref([])
const childCategories = ref([])
const accounts = ref([])

const showAccountPicker = ref(false)
const showDatePicker = ref(false)
const showSuccess = ref(false)
const currentParentId = ref(null)

const canSubmit = computed(() => {
  return form.value.amount && form.value.category_id && form.value.account_id
})

const loadParentCategories = async () => {
  try {
    const data = await request(`/categories/parents?type=${form.value.type}`)
    parentCategories.value = data
  } catch (e) {
    console.error('Load categories failed:', e)
  }
}

const loadChildCategories = async (parentId) => {
  try {
    const data = await request(`/categories/${parentId}/children`)
    childCategories.value = data
  } catch (e) {
    console.error('Load child categories failed:', e)
  }
}

const loadAccounts = async () => {
  try {
    await accountStore.getList()
    accounts.value = accountStore.accounts
  } catch (e) {
    console.error('Load accounts failed:', e)
  }
}

const loadRecord = async () => {
  if (!isEditMode.value) return
  try {
    const record = await request(`/records?startDate=2000-01-01&endDate=2099-12-31`)
    const found = record.find(r => r.id === Number(route.params.id))
    if (!found) {
      router.replace('/')
      return
    }
    form.value.type = found.type
    form.value.amount = found.amount
    form.value.category_id = found.categoryId
    form.value.account_id = found.accountId
    form.value.date = found.date
    form.value.note = found.note || ''

    // Pre-select account
    if (found.accountId) {
      await loadAccounts()
      selectedAccount.value = accounts.value.find(a => a.id === found.accountId) || null
    }

    // Pre-select category
    if (found.categoryId) {
      await loadParentCategories()
      const parent = parentCategories.value.find(c => c.id === found.categoryId)
      if (parent) {
        selectedCategory.value = parent
      } else {
        // It's a child category — find parent and load children
        const allCats = await request('/categories')
        const cat = allCats.find(c => c.id === found.categoryId)
        if (cat && cat.parentId) {
          currentParentId.value = cat.parentId
          await loadChildCategories(cat.parentId)
          selectedCategory.value = cat
        }
      }
    }
  } catch (e) {
    console.error('Load record failed:', e)
  }
}

const selectCategory = async (cat) => {
  if (cat.has_children || (cat.children && cat.children.length > 0)) {
    currentParentId.value = cat.id
    await loadChildCategories(cat.id)
  } else {
    form.value.category_id = cat.id
    selectedCategory.value = cat
    currentParentId.value = null
    childCategories.value = []
  }
}

const selectChildCategory = (cat) => {
  form.value.category_id = cat.id
  selectedCategory.value = cat
  currentParentId.value = null
  childCategories.value = []
}

const selectAccount = (acc) => {
  form.value.account_id = acc.id
  selectedAccount.value = acc
  showAccountPicker.value = false
}

const handleSubmit = async () => {
  if (!canSubmit.value || isSubmitting.value) return
  isSubmitting.value = true
  try {
    const payload = {
      amount: Number(form.value.amount),
      type: form.value.type || 'expense',
      categoryId: form.value.category_id,
      accountId: form.value.account_id,
      note: form.value.note || '',
      date: form.value.date || dayjs().format('YYYY-MM-DD')
    }

    if (isEditMode.value) {
      await recordStore.update(route.params.id, payload)
      router.back()
    } else {
      await recordStore.create(form.value)
      showSuccess.value = true
    }
  } catch (e) {
    $alert({ title: '保存失败', message: e.message || '保存失败', icon: '❌' })
  } finally {
    isSubmitting.value = false
  }
}

const handleDelete = () => {
  $confirm({
    title: '删除记录',
    message: '确定要删除这条记录吗？删除后不可恢复。',
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      try {
        await recordStore.remove(route.params.id)
        router.back()
      } catch (e) {
        $alert({ title: '删除失败', message: e.message || '删除失败', icon: '❌' })
      }
    }
  })
}

const handleSuccessClose = () => {
  showSuccess.value = false
  router.push('/')
}

watch(() => form.value.type, () => {
  if (!isEditMode.value) {
    form.value.category_id = null
    selectedCategory.value = null
    currentParentId.value = null
    childCategories.value = []
  }
  loadParentCategories()
})

onMounted(async () => {
  await loadAccounts()
  if (isEditMode.value) {
    await loadRecord()
  } else {
    await loadParentCategories()
  }
})
</script>

<style lang="scss" scoped>
.add-record-page {
  padding-bottom: 120px;
}

.amount-section {
  background: linear-gradient(180deg, #FFFFFF 0%, #FFF9F0 100%);
  padding: 32px 24px;
  margin: -16px 0 20px;
  border-radius: 0 0 24px 24px;
}

.type-toggle {
  display: flex;
  background: var(--bg-color);
  border-radius: 24px;
  padding: 4px;
  margin-bottom: 24px;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;

  .toggle-btn {
    padding: 10px 28px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    color: var(--text-secondary);
    transition: all 0.3s;

    &.active {
      background: var(--xhs-red);
      color: #fff;
      box-shadow: var(--shadow-red);
    }
  }
}

.amount-display {
  display: flex;
  align-items: center;
  justify-content: center;

  .currency {
    font-size: 48px;
    font-weight: 600;
    color: var(--xhs-red);
    margin-right: 4px;
  }

  .amount-input {
    font-size: 64px;
    font-weight: 700;
    text-align: center;
    border: none;
    outline: none;
    width: 240px;
    color: var(--text-primary);
    background: transparent;

    &::placeholder {
      color: var(--text-hint);
    }
  }
}

.category-section {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;

  .section-label {
    font-size: 13px;
    color: var(--text-hint);
    margin-bottom: 12px;
  }

  &.child {
    margin-top: -8px;
  }
}

.category-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    display: none;
  }
}

.category-pill {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  background: var(--bg-color);
  cursor: pointer;
  transition: all 0.2s;
  min-width: 70px;
  border: 2px solid transparent;

  &.selected {
    border-color: var(--xhs-red);
    background: #FFF5F5;
  }

  &:active {
    transform: scale(0.95);
  }

  .pill-icon {
    font-size: 28px;
    margin-bottom: 4px;
  }

  .pill-name {
    font-size: 12px;
    color: var(--text-secondary);
    white-space: nowrap;
  }

  &.selected .pill-name {
    color: var(--xhs-red);
    font-weight: 500;
  }
}

.form-section {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }

  .label-icon {
    font-size: 18px;
    margin-right: 12px;
  }

  .label {
    font-size: 14px;
    color: var(--text-secondary);
    width: 50px;
  }

  .value {
    flex: 1;
    font-size: 14px;
    color: var(--text-primary);
  }

  .arrow {
    color: var(--text-hint);
    font-size: 14px;
  }

  &.remark-item {
    align-items: flex-start;

    .note-input {
      flex: 1;
      border: none;
      outline: none;
      font-size: 14px;
      color: var(--text-primary);
      background: transparent;
      line-height: 1.5;

      &::placeholder {
        color: var(--text-hint);
      }
    }
  }
}

.submit-section {
  position: fixed;
  bottom: 80px;
  left: 16px;
  right: 16px;
  z-index: 50;
  display: flex;
  gap: 12px;
}

.submit-btn {
  flex: 1;
  height: 56px;
  background: linear-gradient(135deg, var(--xhs-red) 0%, var(--xhs-red-dark) 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-red);
  transition: all 0.3s;

  &:disabled {
    background: #ccc;
    box-shadow: none;
  }

  &:active:not(:disabled) {
    transform: scale(0.98);
  }

  .btn-arrow {
    margin-left: 8px;
  }
}

.delete-btn {
  width: auto;
  height: 56px;
  padding: 0 20px;
  background: #fff;
  color: var(--xhs-red);
  font-size: 16px;
  font-weight: 500;
  border-radius: var(--radius-lg);
  border: 1px solid var(--xhs-red);
  transition: all 0.3s;

  &:active {
    background: #FFF5F5;
  }
}

.picker-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.picker-sheet {
  background: #fff;
  width: 100%;
  max-height: 70vh;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid var(--border-color);

  span {
    font-size: 16px;
    font-weight: 600;
  }

  button {
    color: var(--xhs-red);
    font-size: 14px;
    font-weight: 500;
  }
}

.picker-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

.account-list {
  .account-item {
    display: flex;
    align-items: center;
    padding: 14px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: background 0.2s;

    &:active {
      background: var(--bg-color);
    }

    &.selected {
      background: #FFF5F5;
    }

    .acc-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      margin-right: 14px;
    }

    .acc-name {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
    }

    .acc-balance {
      font-size: 13px;
      color: var(--text-hint);
    }
  }
}

.date-input {
  width: 100%;
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 16px;
  outline: none;

  &:focus {
    border-color: var(--xhs-red);
  }
}
</style>
