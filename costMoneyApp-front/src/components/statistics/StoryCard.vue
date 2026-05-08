<template>
  <div class="story-card">
    <!-- 卡片头部：月份 -->
    <div class="story-header">
      <span class="story-month">{{ monthTitle }}</span>
      <button class="share-btn">📤 分享</button>
    </div>

    <!-- 收支概览 -->
    <div class="story-summary">
      <div class="summary-item">
        <span class="summary-label">总收入</span>
        <span class="summary-value income">+¥{{ formatMoney(totalIncome) }}</span>
      </div>
      <div class="summary-divider"></div>
      <div class="summary-item">
        <span class="summary-label">总支出</span>
        <span class="summary-value expense">-¥{{ formatMoney(totalExpense) }}</span>
      </div>
    </div>

    <!-- 金句总结 -->
    <div class="story-quote">
      <span class="quote-mark">"</span>
      <span class="quote-text">{{ insightQuote }}</span>
      <span class="quote-mark">"</span>
    </div>

    <!-- 分类分布（马卡龙色条） -->
    <div class="story-categories" v-if="topCategories.length > 0">
      <div
        v-for="(cat, index) in topCategories"
        :key="cat.name"
        class="category-bar"
      >
        <span class="cat-emoji">{{ cat.icon }}</span>
        <span class="cat-name">{{ cat.name }}</span>
        <div class="cat-bar">
          <div
            class="cat-fill"
            :style="{ width: cat.percent + '%', background: getMacaronColor(index) }"
          ></div>
        </div>
        <span class="cat-amount">¥{{ formatMoney(cat.amount) }}</span>
      </div>
    </div>

    <!-- 底部数据 -->
    <div class="story-footer">
      <span class="footer-stat">共 {{ recordCount }} 笔记账</span>
      <span class="footer-stat" v-if="topCategory">最高支出：{{ topCategory.name }} ¥{{ formatMoney(topCategory.amount) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  monthTitle: {
    type: String,
    default: ''
  },
  totalIncome: {
    type: Number,
    default: 0
  },
  totalExpense: {
    type: Number,
    default: 0
  },
  recordCount: {
    type: Number,
    default: 0
  },
  categories: {
    type: Array,
    default: () => []
  }
})

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) : '0'
}

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

const topCategories = computed(() => {
  return props.categories.slice(0, 5).map(cat => ({
    ...cat,
    percent: props.totalExpense > 0 ? (cat.amount / props.totalExpense) * 100 : 0
  }))
})

const topCategory = computed(() => {
  if (props.categories.length === 0) return null
  return props.categories[0]
})

const insightQuotes = [
  '这个月的小金库又在默默长大呢～',
  '每一笔消费都是生活的小确幸呀',
  '记账让我更懂生活的美好',
  '今天的你比昨天更会理财了呢',
  '花钱是为了更好地赚钱呀',
  '本月收获满满，继续保持！',
  '懂生活的红薯，运气都不会差 🍠',
  '本月美食支出 +32% —— 是个懂生活的你 🍜'
]

const insightQuote = computed(() => {
  if (props.totalExpense > 0 && props.recordCount > 0) {
    return insightQuotes[Math.floor(Math.random() * insightQuotes.length)]
  }
  return '开始记账，记录生活的每一刻 ✨'
})
</script>

<style lang="scss" scoped>
.story-card {
  background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F0 100%);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

.story-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .story-month {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-primary);
  }

  .share-btn {
    font-size: 13px;
    color: var(--xhs-red);
    padding: 6px 12px;
    border-radius: 16px;
    background: #FFF5F5;

    &:active {
      opacity: 0.8;
    }
  }
}

.story-summary {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid var(--border-color);
}

.summary-item {
  flex: 1;
  text-align: center;

  .summary-label {
    font-size: 12px;
    color: var(--text-hint);
    display: block;
    margin-bottom: 4px;
  }

  .summary-value {
    font-size: 20px;
    font-weight: 700;

    &.income { color: var(--text-primary); }
    &.expense { color: var(--accent-expense); }
  }
}

.summary-divider {
  width: 1px;
  height: 36px;
  background: var(--border-color);
}

.story-quote {
  background: linear-gradient(135deg, #FFF5F5 0%, #FFF9F0 100%);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 16px;
  text-align: center;

  .quote-mark {
    font-size: 24px;
    color: var(--xhs-red);
    opacity: 0.5;
  }

  .quote-text {
    font-size: 14px;
    color: var(--text-secondary);
    font-style: italic;
    line-height: 1.5;
  }
}

.story-categories {
  margin-bottom: 12px;
}

.category-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;

  .cat-emoji {
    font-size: 16px;
    width: 24px;
    text-align: center;
  }

  .cat-name {
    font-size: 13px;
    color: var(--text-secondary);
    width: 50px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .cat-bar {
    flex: 1;
    height: 8px;
    background: var(--border-color);
    border-radius: 4px;
    overflow: hidden;

    .cat-fill {
      height: 100%;
      border-radius: 4px;
      transition: width 0.3s ease;
    }
  }

  .cat-amount {
    font-size: 12px;
    color: var(--text-hint);
    width: 60px;
    text-align: right;
  }
}

.story-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);

  .footer-stat {
    font-size: 12px;
    color: var(--text-hint);
  }
}
</style>
