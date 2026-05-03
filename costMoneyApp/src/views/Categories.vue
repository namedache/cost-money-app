<template>
  <div class="categories-page page">
    <div class="tab-header">
      <button :class="{ active: activeTab === 'expense' }" @click="activeTab = 'expense'">支出分类</button>
      <button :class="{ active: activeTab === 'income' }" @click="activeTab = 'income'">收入分类</button>
    </div>

    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div class="category-list" v-else-if="categories.length > 0">
      <div v-for="cat in categories" :key="cat.id" class="category-group">
        <div class="category-parent card">
          <span class="cat-icon" :style="{ background: cat.color || 'var(--macaron-pink)' }">{{ cat.icon }}</span>
          <span class="cat-name">{{ cat.name }}</span>
          <div class="cat-actions" v-if="!cat.isSystem">
            <button class="action-btn edit" @click.stop="openEdit(cat)">✏️</button>
            <button class="action-btn delete" @click.stop="handleDelete(cat)">🗑️</button>
          </div>
        </div>
        <div class="category-children" v-if="getChildren(cat.id).length > 0">
          <div
            v-for="child in getChildren(cat.id)"
            :key="child.id"
            class="category-child"
          >
            <span class="cat-icon" :style="{ background: child.color || 'var(--macaron-pink)' }">{{ child.icon }}</span>
            <span class="cat-name">{{ child.name }}</span>
            <div class="cat-actions small" v-if="!child.isSystem">
              <button class="action-btn edit" @click.stop="openEdit(child)">✏️</button>
              <button class="action-btn delete" @click.stop="handleDelete(child)">🗑️</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <p>暂无分类</p>
    </div>

    <button class="btn primary add-btn" @click="openAdd(null)">+ 添加分类</button>

    <!-- 添加/编辑弹窗 -->
    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content card">
        <h3>{{ editTarget ? '编辑分类' : '添加分类' }}</h3>
        <div class="form-item">
          <label>父分类</label>
          <select v-model="form.parentId">
            <option :value="null">作为一级分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.icon }} {{ cat.name }}
            </option>
          </select>
        </div>
        <div class="form-item">
          <label>名称</label>
          <input v-model="form.name" type="text" placeholder="分类名称" />
        </div>
        <div class="form-item icon-picker">
          <label>图标</label>
          <div class="emoji-grid">
            <button
              v-for="emoji in emojiOptions"
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
import { ref, onMounted, watch, inject } from 'vue'
import { request } from '@/utils/api'

const $confirm = inject('$confirm')

const activeTab = ref('expense')
const categories = ref([])
const allCategories = ref([])
const loading = ref(false)
const showModal = ref(false)
const editTarget = ref(null)

const form = ref({
  parentId: null,
  name: '',
  icon: '📌',
  color: '#FF6B6B'
})

const emojiOptions = ['🍔', '🚗', '🛒', '🎬', '💊', '✈️', '📱', '👕', '🏠', '📚', '🎁', '🎮', '💰', '💼', '🎯', '❤️', '🐱', '☕', '🍕', '🎵']
const colorOptions = ['#FF6B6B', '#FF8E53', '#FFC53D', '#52C41A', '#13C2C2', '#1890FF', '#722ED1', '#EB2F96', '#FA541C', '#2F54EB']

const getChildren = (parentId) => {
  return allCategories.value.filter(c => c.parentId === parentId)
}

const loadCategories = async () => {
  loading.value = true
  try {
    const data = await request(`/categories/parents?type=${activeTab.value}`)
    categories.value = data
    const childrenPromises = data.map(cat => request(`/categories/${cat.id}/children`))
    const childrenArrays = await Promise.all(childrenPromises)
    allCategories.value = childrenArrays.flat()
  } catch (e) {
    console.error('Load categories failed:', e)
  } finally {
    loading.value = false
  }
}

const openAdd = (parentId) => {
  editTarget.value = null
  form.value = {
    parentId: parentId,
    name: '',
    icon: '📌',
    color: '#FF6B6B'
  }
  showModal.value = true
}

const openEdit = (cat) => {
  editTarget.value = cat
  form.value = {
    parentId: cat.parentId || null,
    name: cat.name,
    icon: cat.icon || '📌',
    color: cat.color || '#FF6B6B'
  }
  showModal.value = true
}

const handleSave = async () => {
  if (!form.value.name.trim()) {
    $alert({ title: '提示', message: '请输入分类名称', icon: '⚠️' })
    return
  }

  try {
    const body = {
      name: form.value.name,
      icon: form.value.icon,
      color: form.value.color,
      type: activeTab.value,
      parentId: form.value.parentId
    }

    if (editTarget.value) {
      await request(`/categories/${editTarget.value.id}`, {
        method: 'PUT',
        body
      })
    } else {
      await request('/categories', {
        method: 'POST',
        body
      })
    }
    closeModal()
    loadCategories()
  } catch (e) {
    $alert({ title: '保存失败', message: e.message || '保存失败', icon: '❌' })
  }
}

const handleDelete = (cat) => {
  $confirm({
    title: '删除分类',
    message: `确定要删除"${cat.name}"吗？`,
    icon: '🗑️',
    confirmText: '删除',
    danger: true,
    onConfirm: async () => {
      try {
        await request(`/categories/${cat.id}`, { method: 'DELETE' })
        loadCategories()
      } catch (e) {
        $alert({ title: '删除失败', message: e.message || '删除失败', icon: '❌' })
      }
    }
  })
}

const closeModal = () => {
  showModal.value = false
  editTarget.value = null
}

watch(activeTab, () => {
  loadCategories()
})

onMounted(() => {
  loadCategories()
})
</script>

<style lang="scss" scoped>
.categories-page {
  padding-bottom: 100px;
}

.tab-header {
  display: flex;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 4px;
  margin-bottom: 16px;

  button {
    flex: 1;
    padding: 10px;
    border-radius: var(--radius-sm);
    font-size: 14px;
    color: var(--text-secondary);

    &.active {
      background: var(--xhs-red);
      color: #fff;
    }
  }
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-parent {
  display: flex;
  align-items: center;
  gap: 12px;

  .cat-actions {
    margin-left: auto;
    display: flex;
    gap: 6px;
  }
}

.category-children {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  padding: 0 12px;
  margin-top: 8px;
}

.category-child {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 8px;
  background: #fff;
  border-radius: var(--radius-sm);
  position: relative;

  .cat-actions {
    position: absolute;
    top: 2px;
    right: 2px;
    display: flex;
    gap: 2px;

    &.small .action-btn {
      width: 18px;
      height: 18px;
      font-size: 8px;
    }
  }
}

.action-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;

  &.delete {
    background: #FFF0F0;
  }
}

.cat-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.cat-name {
  font-size: 14px;
  font-weight: 500;
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
  max-height: 80vh;
  overflow-y: auto;

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
