<template>
  <div class="records-page page">
    <!-- 月份选择器 -->
    <div class="month-selector">
      <button class="month-btn" @click="changeMonth(-1)">‹</button>
      <span class="current-month">{{ currentMonth }}</span>
      <button class="month-btn" @click="changeMonth(1)">›</button>
    </div>

    <!-- 筛选tabs -->
    <div class="filter-tabs">
      <button
        v-for="filter in filters"
        :key="filter.value"
        class="filter-tab"
        :class="{ active: currentFilter === filter.value }"
        @click="currentFilter = filter.value"
      >
        {{ filter.label }}
      </button>
    </div>

    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 账单列表 -->
    <div class="records-list" v-else-if="filteredRecords.length > 0">
      <div
        v-for="(record, index) in filteredRecords"
        :key="record.id"
        class="record-item"
        @click="goToDetail(record)"
      >
        <div class="record-left">
          <span class="record-icon" :style="{ background: record.category?.color || 'var(--macaron-pink)' }">
            {{ record.category?.icon || '📝' }}
          </span>
        </div>
        <div class="record-center">
          <span class="record-category">{{ record.category?.name || '未分类' }}</span>
          <span class="record-note" v-if="record.note">{{ record.note }}</span>
          <span class="record-account">{{ record.account?.name }} · {{ formatDate(record.date) }}</span>
        </div>
        <div class="record-right">
          <span class="record-amount" :class="record.type">
            {{ record.type === 'expense' ? '-' : '+' }}¥{{ formatMoney(record.amount) }}
          </span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <div class="empty-illustration">📋</div>
      <div class="empty-title">暂无{{ currentFilter === 'all' ? '' : currentFilter === 'expense' ? '支出' : '收入' }}记录</div>
      <div class="empty-desc">开始记录你的每一笔收支吧</div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore && filteredRecords.length > 0" @click="loadMore">
      加载更多
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRecordStore } from '@/stores/record'
import dayjs from 'dayjs'

const router = useRouter()
const recordStore = useRecordStore()

const currentMonth = ref(dayjs().format('YYYY年MM月'))
const currentMonthKey = ref(dayjs().format('YYYY-MM'))
const currentFilter = ref('all')
const page = ref(1)
const pageSize = 20

const filters = [
  { label: '全部', value: 'all' },
  { label: '支出', value: 'expense' },
  { label: '收入', value: 'income' }
]

const records = ref([])
const hasMore = ref(false)
const loading = ref(false)

const filteredRecords = computed(() => {
  if (currentFilter.value === 'all') return records.value
  return records.value.filter(r => r.type === currentFilter.value)
})

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'
}

const formatDate = (date) => {
  const d = dayjs(date)
  const today = dayjs().startOf('day')
  const recordDay = d.startOf('day')

  if (recordDay.isSame(today)) return '今天'
  if (recordDay.isSame(today.subtract(1, 'day'))) return '昨天'
  return d.format('MM-DD')
}

const changeMonth = (delta) => {
  currentMonthKey.value = dayjs(currentMonthKey.value).add(delta, 'month').format('YYYY-MM')
  currentMonth.value = dayjs(currentMonthKey.value).format('YYYY年MM月')
  page.value = 1
  loadRecords()
}

const loadRecords = async () => {
  loading.value = true
  try {
    const data = await recordStore.getMonthRecords(currentMonthKey.value)
    records.value = data
  } catch (e) {
    console.error('Load records failed:', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  // 暂时不做分页加载
}

const goToDetail = (record) => {
  router.push(`/edit/${record.id}`)
}

watch(currentFilter, () => {
  // 筛选变化时不需要重新加载，computed会自动处理
})

onMounted(() => {
  loadRecords()
})
</script>

<style lang="scss" scoped>
.records-page {
  // padding is handled by .page class in global styles
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

.month-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 16px;

  .month-btn {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #fff;
    font-size: 18px;
    color: var(--xhs-red);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: var(--shadow);

    &:active {
      transform: scale(0.95);
    }
  }

  .current-month {
    font-size: 17px;
    font-weight: 600;
    color: var(--text-primary);
  }
}

.filter-tabs {
  display: flex;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 4px;
  margin-bottom: 16px;
  box-shadow: var(--shadow);

  .filter-tab {
    flex: 1;
    padding: 10px 16px;
    border-radius: 20px;
    font-size: 14px;
    color: var(--text-secondary);
    transition: all 0.2s;

    &.active {
      background: var(--xhs-red);
      color: #fff;
      font-weight: 500;
    }
  }
}

.records-list {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow);
}

.record-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: var(--bg-color);
  }
}

.record-left {
  margin-right: 12px;

  .record-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
  }
}

.record-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;

  .record-category {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-primary);
  }

  .record-note {
    font-size: 13px;
    color: var(--text-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .record-account {
    font-size: 12px;
    color: var(--text-hint);
  }
}

.record-right {
  margin-left: 12px;

  .record-amount {
    font-size: 16px;
    font-weight: 600;

    &.expense { color: var(--accent-expense); }
    &.income { color: var(--text-primary); }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow);

  .empty-illustration {
    font-size: 56px;
    margin-bottom: 16px;
  }

  .empty-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
  }

  .empty-desc {
    font-size: 14px;
    color: var(--text-hint);
  }
}

.load-more {
  text-align: center;
  padding: 16px;
  color: var(--xhs-red);
  font-size: 14px;
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}
</style>
