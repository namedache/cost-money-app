<template>
  <div class="home-page page">
    <!-- 下拉刷新指示器 -->
    <RefreshLoader :isSpinning="isRefreshing" />

    <!-- 封面卡片 -->
    <CoverCard
      :month-expense="stats.monthExpense"
      :month-income="stats.monthIncome"
      :budget-spent="budgetSpent"
      :budget-total="budgetTotal"
      :streak-days="streakDays"
    />

    <!-- 快捷记账 -->
    <div class="quick-section" v-if="quickRecords.length > 0">
      <div class="section-header">
        <span class="section-title">快捷记账</span>
      </div>
      <div class="quick-chips">
        <div
          v-for="item in quickRecords"
          :key="item.id"
          class="quick-chip"
          :style="{ background: item.color || 'var(--macaron-pink)' }"
          @click="handleQuickRecord(item)"
        >
          <span class="chip-icon">{{ item.icon }}</span>
          <span class="chip-name">{{ item.name }}</span>
          <span class="chip-amount">¥{{ item.amount }}</span>
        </div>
      </div>
    </div>

    <!-- 笔记式账单列表 -->
    <div class="records-section">
      <div class="section-header">
        <span class="section-title">本月记账</span>
        <span class="record-count">{{ records.length }} 笔记</span>
      </div>

      <div class="notes-waterfall" v-if="records.length > 0">
        <div class="notes-column left">
          <NoteRecordCard
            v-for="item in leftRecords"
            :key="item.id"
            :record="item"
            @click="goToDetail"
          />
        </div>
        <div class="notes-column right">
          <NoteRecordCard
            v-for="item in rightRecords"
            :key="item.id"
            :record="item"
            @click="goToDetail"
          />
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else>
        <div class="empty-illustration">📒</div>
        <div class="empty-title">还没记账？</div>
        <div class="empty-desc">今天喝奶茶了吗 🧋</div>
        <button class="btn xhs-btn" @click="$router.push('/add')">记一笔</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useRecordStore } from '@/stores/record'
import { useQuickRecordStore } from '@/stores/quickRecord'
import { request } from '@/utils/api'
import CoverCard from '@/components/home/CoverCard.vue'
import NoteRecordCard from '@/components/home/NoteRecordCard.vue'
import RefreshLoader from '@/components/common/RefreshLoader.vue'
import dayjs from 'dayjs'

const router = useRouter()
const recordStore = useRecordStore()
const quickStore = useQuickRecordStore()
const $confirm = inject('$confirm')

const records = ref([])
const quickRecords = ref([])
const isRefreshing = ref(false)

const stats = ref({
  monthIncome: 0,
  monthExpense: 0,
  totalBalance: 0
})

// 预算数据（暂时用固定值，后续可从 store 获取）
const budgetSpent = ref(0)
const budgetTotal = ref(3000)
const streakDays = ref(0)

const leftRecords = computed(() => records.value.filter((_, i) => i % 2 === 0))
const rightRecords = computed(() => records.value.filter((_, i) => i % 2 === 1))

const loadData = async () => {
  isRefreshing.value = true
  try {
    const [recordsData, quickData, statsData] = await Promise.all([
      recordStore.getMonthRecords(),
      quickStore.getList(),
      recordStore.getStats()
    ])
    records.value = recordsData
    quickRecords.value = quickData
    stats.value = statsData

    // Load budget data for real total
    budgetSpent.value = statsData.monthExpense
    try {
      const budgetData = await request('/budgets/with-spent')
      const totalBudget = budgetData.reduce((sum, b) => sum + Number(b.amount || 0), 0)
      if (totalBudget > 0) budgetTotal.value = totalBudget
    } catch {}

    // Calculate streak: count consecutive days with records from today backwards
    if (recordsData.length > 0) {
      const dateSet = new Set(recordsData.map(r => r.date))
      let streak = 0
      let checkDate = dayjs()
      while (dateSet.has(checkDate.format('YYYY-MM-DD'))) {
        streak++
        checkDate = checkDate.subtract(1, 'day')
      }
      streakDays.value = streak
    }
  } finally {
    isRefreshing.value = false
  }
}

const handleQuickRecord = async (item) => {
  $confirm({
    title: '确认记账',
    message: `确认记账：${item.name} ¥${item.amount}？`,
    icon: item.icon || '💰',
    confirmText: '确认',
    onConfirm: async () => {
      await recordStore.create({
        account_id: item.account_id,
        category_id: item.category_id,
        amount: item.amount,
        type: 'expense',
        date: dayjs().format('YYYY-MM-DD')
      })
      loadData()
    }
  })
}

const goToDetail = (item) => {
  router.push(`/edit/${item.id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.home-page {
  // padding is handled by .page class in global styles
}

.quick-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
  }

  .record-count {
    font-size: 12px;
    color: var(--text-hint);
    background: var(--bg-color);
    padding: 4px 10px;
    border-radius: 10px;
  }
}

.quick-chips {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    display: none;
  }
}

.quick-chip {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s;
  min-width: 80px;

  &:active { transform: scale(0.95); }

  .chip-icon {
    font-size: 24px;
    margin-bottom: 4px;
  }

  .chip-name {
    font-size: 12px;
    font-weight: 500;
    margin-bottom: 2px;
  }

  .chip-amount {
    font-size: 11px;
    opacity: 0.9;
  }
}

.records-section {
  margin-top: 8px;
}

.notes-waterfall {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.notes-column {
  display: flex;
  flex-direction: column;
}
</style>
