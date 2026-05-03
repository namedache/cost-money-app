<template>
  <div class="cover-card">
    <!-- 情绪化欢迎语 -->
    <div class="greeting-section">
      <span class="greeting-emoji">{{ greetingEmoji }}</span>
      <span class="greeting-text">{{ greetingText }}</span>
    </div>

    <!-- 金额展示 -->
    <div class="amount-showcase">
      <div class="amount-card expense-card">
        <div class="amount-label">本月支出</div>
        <div class="amount-value expense">-¥{{ formatMoney(monthExpense) }}</div>
      </div>
      <div class="amount-divider"></div>
      <div class="amount-card income-card">
        <div class="amount-label">本月收入</div>
        <div class="amount-value income">+¥{{ formatMoney(monthIncome) }}</div>
      </div>
    </div>

    <!-- 预算进度条 -->
    <div class="budget-progress" v-if="budgetPercent > 0">
      <div class="progress-header">
        <span class="progress-label">预算进度</span>
        <span class="progress-ratio">¥{{ formatMoney(budgetSpent) }} / ¥{{ formatMoney(budgetTotal) }}</span>
      </div>
      <div class="progress-bar">
        <div
          class="progress-fill"
          :style="{ width: Math.min(budgetPercent, 100) + '%' }"
          :class="{ warning: budgetPercent > 80, danger: budgetPercent > 100 }"
        ></div>
      </div>
    </div>

    <!-- 连续记账徽章 -->
    <div class="streak-badge" v-if="streakDays > 0">
      <span class="streak-fire">🔥</span>
      <span class="streak-text">连续记账 {{ streakDays }} 天</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  monthExpense: {
    type: Number,
    default: 0
  },
  monthIncome: {
    type: Number,
    default: 0
  },
  budgetSpent: {
    type: Number,
    default: 0
  },
  budgetTotal: {
    type: Number,
    default: 0
  },
  streakDays: {
    type: Number,
    default: 0
  }
})

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'
}

const budgetPercent = computed(() => {
  if (props.budgetTotal <= 0) return 0
  return (props.budgetSpent / props.budgetTotal) * 100
})

const greetingEmoji = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '🌙'
  if (hour < 9) return '🌅'
  if (hour < 12) return '☀️'
  if (hour < 14) return '🍱'
  if (hour < 18) return '🌤️'
  if (hour < 21) return '🌆'
  return '🌙'
})

const greetingText = computed(() => {
  const remaining = props.budgetTotal - props.budgetSpent
  const hour = new Date().getHours()

  if (props.budgetTotal > 0 && remaining > 0) {
    return `今天还剩 🍱 ¥${formatMoney(remaining)} 干饭基金`
  }
  if (props.budgetTotal > 0 && remaining <= 0) {
    return '本月预算已用完啦，要省着点花哦 💸'
  }
  if (hour < 6) return '夜深了，早点休息哦 🌙'
  if (hour < 9) return '新的一天，从记账开始 ✨'
  if (hour < 12) return '上午好，记得吃早餐 🍳'
  if (hour < 14) return '中午好，记得按时吃饭 🍜'
  if (hour < 18) return '下午好，充实的一天 🌤️'
  return '晚上好，今天辛苦了 🌙'
})
</script>

<style lang="scss" scoped>
.cover-card {
  background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F0 100%);
  border-radius: var(--radius-lg);
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

.greeting-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;

  .greeting-emoji {
    font-size: 24px;
  }

  .greeting-text {
    font-size: 14px;
    color: var(--text-secondary);
  }
}

.amount-showcase {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid var(--border-color);
}

.amount-card {
  flex: 1;
  text-align: center;

  .amount-label {
    font-size: 12px;
    color: var(--text-hint);
    margin-bottom: 4px;
  }

  .amount-value {
    font-size: 20px;
    font-weight: 700;

    &.expense { color: var(--accent-expense); }
    &.income { color: var(--text-primary); }
  }
}

.amount-divider {
  width: 1px;
  height: 40px;
  background: var(--border-color);
}

.budget-progress {
  margin-bottom: 12px;

  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;

    .progress-label {
      font-size: 13px;
      color: var(--text-secondary);
    }

    .progress-ratio {
      font-size: 12px;
      color: var(--text-hint);
    }
  }

  .progress-bar {
    height: 8px;
    background: var(--border-color);
    border-radius: 4px;
    overflow: hidden;

    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, var(--xhs-red) 0%, var(--xhs-red-light) 100%);
      border-radius: 4px;
      transition: width 0.3s ease;

      &.warning {
        background: linear-gradient(90deg, #FF9F43 0%, #FECA57 100%);
      }

      &.danger {
        background: linear-gradient(90deg, #FF6B6B 0%, #EE5A5A 100%);
      }
    }
  }
}

.streak-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #FFF3E6 0%, #FFE4CC 100%);
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid #FFD4B8;

  .streak-fire {
    font-size: 16px;
  }

  .streak-text {
    font-size: 12px;
    color: #E07B3C;
    font-weight: 500;
  }
}
</style>
