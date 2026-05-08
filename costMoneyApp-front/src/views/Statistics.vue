<template>
  <div class="statistics-page page">
    <!-- 月份切换器 -->
    <div class="month-carousel">
      <button class="month-nav-btn" @click="changeMonth(-1)">‹</button>
      <span class="current-month">{{ currentMonth }}</span>
      <button class="month-nav-btn" @click="changeMonth(1)">›</button>
    </div>

    <!-- 账单故事卡片 -->
    <div class="story-carousel">
      <StoryCard
        :month-title="currentMonth"
        :total-income="stats.totalIncome"
        :total-expense="stats.totalExpense"
        :record-count="stats.recordCount"
        :categories="categoryStats"
      />
    </div>

    <!-- 月度对比 -->
    <div class="compare-section card">
      <div class="section-header">
        <span class="section-title">📊 月度对比</span>
      </div>
      <div class="compare-list">
        <div class="compare-item">
          <span class="compare-label">上月同期</span>
          <span class="compare-value expense">-¥{{ formatMoney(lastMonthExpense) }}</span>
        </div>
        <div class="compare-item">
          <span class="compare-label">环比变化</span>
          <span class="compare-value" :class="compareClass">{{ compareText }}</span>
        </div>
      </div>
    </div>

    <!-- 趋势图（保留但风格调整） -->
    <div class="chart-section card">
      <div class="section-header">
        <span class="section-title">📈 收支趋势</span>
      </div>
      <div ref="trendChartRef" class="chart-container"></div>
    </div>

    <!-- 分类占比（小红书风格） -->
    <div class="category-section card">
      <div class="section-header">
        <span class="section-title">🍽️ 支出分类</span>
        <div class="type-toggle">
          <button
            :class="{ active: statsType === 'expense' }"
            @click="statsType = 'expense'"
          >支出</button>
          <button
            :class="{ active: statsType === 'income' }"
            @click="statsType = 'income'"
          >收入</button>
        </div>
      </div>
      <div class="category-grid" v-if="categoryStats.length > 0">
        <div
          v-for="(cat, index) in categoryStats"
          :key="cat.name"
          class="category-item"
          :style="{ background: getMacaronColor(index) }"
        >
          <span class="cat-icon">{{ cat.icon }}</span>
          <span class="cat-name">{{ cat.name }}</span>
          <span class="cat-amount">¥{{ formatMoney(cat.amount) }}</span>
        </div>
      </div>
      <div class="empty-chart" v-else>
        <span class="empty-emoji">📊</span>
        <span class="empty-text">暂无分类数据</span>
      </div>
    </div>

    <!-- 导出功能 -->
    <div class="export-section">
      <button class="btn secondary" @click="exportCSV">导出本月账单</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { request, API_BASE } from '@/utils/api'
import StoryCard from '@/components/statistics/StoryCard.vue'

const currentMonth = ref(dayjs().format('YYYY年MM月'))
const currentMonthKey = ref(dayjs().format('YYYY-MM'))
const statsType = ref('expense')
const trendChartRef = ref(null)

let trendChart = null

const stats = ref({
  totalIncome: 0,
  totalExpense: 0,
  recordCount: 0
})

const categoryStats = ref([])
const lastMonthExpense = ref(0)

const macaronColors = [
  'var(--macaron-pink)',
  'var(--macaron-mint)',
  'var(--macaron-apricot)',
  'var(--macaron-lavender)',
  'var(--macaron-yellow)'
]

const getMacaronColor = (index) => {
  return macaronColors[index % macaronColors.length]
}

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) : '0'
}

const compareText = computed(() => {
  if (lastMonthExpense.value === 0) return '-'
  const diff = stats.value.totalExpense - lastMonthExpense.value
  const percent = Math.abs((diff / lastMonthExpense.value) * 100).toFixed(0)
  if (diff > 0) return `+${percent}%`
  if (diff < 0) return `-${percent}%`
  return '持平'
})

const compareClass = computed(() => {
  if (lastMonthExpense.value === 0) return ''
  const diff = stats.value.totalExpense - lastMonthExpense.value
  if (diff > 0) return 'up'
  if (diff < 0) return 'down'
  return ''
})

const changeMonth = (delta) => {
  currentMonthKey.value = dayjs(currentMonthKey.value).add(delta, 'month').format('YYYY-MM')
  currentMonth.value = dayjs(currentMonthKey.value).format('YYYY年MM月')
  loadData()
}

const loadData = async () => {
  await Promise.all([
    loadStats(),
    loadCategoryStats(),
    loadLastMonthExpense()
  ])
  loadTrendChart()
}

const loadStats = async () => {
  try {
    const startDate = `${currentMonthKey.value}-01`
    const endDate = dayjs(startDate).endOf('month').format('YYYY-MM-DD')
    const records = await request(`/records?startDate=${startDate}&endDate=${endDate}`)

    let income = 0
    let expense = 0
    records.forEach(r => {
      if (r.type === 'income') income += Number(r.amount)
      else expense += Number(r.amount)
    })

    stats.value = {
      totalIncome: income,
      totalExpense: expense,
      recordCount: records.length
    }
  } catch (e) {
    console.error('Load stats failed:', e)
  }
}

const loadCategoryStats = async () => {
  try {
    const startDate = `${currentMonthKey.value}-01`
    const endDate = dayjs(startDate).endOf('month').format('YYYY-MM-DD')
    const records = await request(`/records?startDate=${startDate}&endDate=${endDate}&type=${statsType.value}`)
    const categories = await request('/categories?type=' + statsType.value)

    const categoryMap = new Map()
    for (const r of records) {
      if (r.type !== statsType.value) continue
      const cat = categories.find(c => c.id === r.categoryId)
      if (cat) {
        const existing = categoryMap.get(cat.name) || { name: cat.name, icon: cat.icon, amount: 0 }
        existing.amount += Number(r.amount)
        categoryMap.set(cat.name, existing)
      }
    }

    categoryStats.value = Array.from(categoryMap.values())
      .sort((a, b) => b.amount - a.amount)
  } catch (e) {
    console.error('Load category stats failed:', e)
  }
}

const loadLastMonthExpense = async () => {
  try {
    const lastMonth = dayjs(currentMonthKey.value).subtract(1, 'month')
    const startDate = lastMonth.startOf('month').format('YYYY-MM-DD')
    const endDate = lastMonth.endOf('month').format('YYYY-MM-DD')
    const records = await request(`/records?startDate=${startDate}&endDate=${endDate}&type=expense`)

    let expense = 0
    records.forEach(r => {
      if (r.type === 'expense') expense += Number(r.amount)
    })

    lastMonthExpense.value = expense
  } catch (e) {
    console.error('Load last month expense failed:', e)
  }
}

const loadTrendChart = async () => {
  try {
    const monthParams = []
    const months = []

    for (let i = 5; i >= 0; i--) {
      const month = dayjs().subtract(i, 'month')
      const monthStr = month.format('YYYY-MM')
      const startDate = `${monthStr}-01`
      const endDate = month.endOf('month').format('YYYY-MM-DD')
      months.push(month.format('MM月'))
      monthParams.push({ startDate, endDate })
    }

    const results = await Promise.all(
      monthParams.map(({ startDate, endDate }) =>
        request(`/records?startDate=${startDate}&endDate=${endDate}`)
      )
    )

    const incomeData = []
    const expenseData = []
    results.forEach(records => {
      let income = 0
      let expense = 0
      records.forEach(r => {
        if (r.type === 'income') income += Number(r.amount)
        else expense += Number(r.amount)
      })
      incomeData.push(income)
      expenseData.push(expense)
    })

    if (!trendChart && trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
    }

    if (trendChart) {
      trendChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#eee', textStyle: { color: '#333' } },
        legend: {
          data: ['收入', '支出'],
          top: 5,
          right: 10,
          backgroundColor: 'rgba(255,255,255,0.8)',
          textStyle: { color: '#666', fontSize: 12 },
          itemWidth: 12,
          itemHeight: 12
        },
        grid: { top: 40, right: 15, bottom: 30, left: 50 },
        xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#E8E8E8' } }, axisLabel: { color: '#999' } },
        yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: '#999' }, splitLine: { lineStyle: { color: '#F5F5F5' } } },
        series: [
          { name: '收入', type: 'line', data: incomeData, smooth: true, itemStyle: { color: '#2D2D2D' }, lineStyle: { width: 2 }, areaStyle: { color: 'rgba(45,45,45,0.08)' } },
          { name: '支出', type: 'line', data: expenseData, smooth: true, itemStyle: { color: '#FF6B6B' }, lineStyle: { width: 2 }, areaStyle: { color: 'rgba(255,107,107,0.08)' } }
        ]
      })
    }
  } catch (e) {
    console.error('Load trend chart failed:', e)
  }
}

const exportCSV = async () => {
  const startDate = dayjs(currentMonthKey.value).startOf('month').format('YYYY-MM-DD')
  const endDate = dayjs(currentMonthKey.value).endOf('month').format('YYYY-MM-DD')
  const token = localStorage.getItem('token')
  const url = `${API_BASE}/export/csv?startDate=${startDate}&endDate=${endDate}`

  try {
    const res = await fetch(url, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (res.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return
    }
    const blob = await res.blob()
    const blobUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = `records_${startDate}_${endDate}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(blobUrl)
  } catch (e) {
    console.error('Export failed:', e)
  }
}

watch(statsType, () => {
  loadCategoryStats()
})

const handleResize = () => {
  if (trendChart) trendChart.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
})
</script>

<style lang="scss" scoped>
.statistics-page {
  // padding is handled by .page class in global styles
}

.month-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding-top: 0;

  .month-nav-btn {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #fff;
    font-size: 20px;
    color: var(--xhs-red);
    box-shadow: var(--shadow);
    transition: transform 0.2s;

    &:active {
      transform: scale(0.95);
    }
  }

  .current-month {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-primary);
  }
}

.story-carousel {
  margin-bottom: 20px;
}

.compare-section {
  margin-bottom: 16px;

  .section-header {
    margin-bottom: 12px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
    }
  }

  .compare-list {
    display: flex;
    gap: 16px;
  }

  .compare-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .compare-label {
      font-size: 12px;
      color: var(--text-hint);
    }

    .compare-value {
      font-size: 18px;
      font-weight: 600;

      &.expense { color: var(--accent-expense); }
      &.up { color: #FF6B6B; }
      &.down { color: #4CAF50; }
    }
  }
}

.chart-section {
  margin-bottom: 16px;

  .section-header {
    margin-bottom: 12px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
    }
  }
}

.chart-container {
  height: 200px;
}

.category-section {
  margin-bottom: 16px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
    }
  }

  .type-toggle {
    display: flex;
    background: var(--bg-color);
    border-radius: 16px;
    padding: 2px;

    button {
      padding: 6px 14px;
      border-radius: 14px;
      font-size: 12px;
      color: var(--text-secondary);
      transition: all 0.2s;

      &.active {
        background: #fff;
        color: var(--xhs-red);
        font-weight: 500;
        box-shadow: var(--shadow);
      }
    }
  }

  .category-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .category-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px 12px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: transform 0.2s;

    &:active {
      transform: scale(0.98);
    }

    .cat-icon {
      font-size: 28px;
      margin-bottom: 6px;
    }

    .cat-name {
      font-size: 13px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .cat-amount {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }

  .empty-chart {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 0;

    .empty-emoji {
      font-size: 48px;
      margin-bottom: 12px;
    }

    .empty-text {
      font-size: 14px;
      color: var(--text-hint);
    }
  }
}

.export-section {
  .btn {
    width: 100%;
  }
}
</style>
