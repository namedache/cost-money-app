<template>
  <div class="note-card" @click="$emit('click', record)">
    <!-- 卡片头部：图标+分类 -->
    <div class="note-header">
      <span class="category-emoji" :style="{ background: record.category?.color || 'var(--macaron-pink)' }">
        {{ record.category?.icon || '📝' }}
      </span>
      <span class="category-name">{{ record.category?.name || '未分类' }}</span>
      <span class="note-time">{{ formatTime(record.date) }}</span>
    </div>

    <!-- 金额主体 -->
    <div class="note-amount" :class="record.type">
      {{ record.type === 'expense' ? '-' : '+' }}¥{{ formatMoney(record.amount) }}
    </div>

    <!-- 备注（如果存在） -->
    <div class="note-remark" v-if="record.note">
      {{ record.note }}
    </div>

    <!-- 底部：账户信息 -->
    <div class="note-footer">
      <span class="account-tag">{{ record.account?.name || '默认账户' }}</span>
      <span class="share-btn" v-if="record.note">📤</span>
    </div>
  </div>
</template>

<script setup>
import dayjs from 'dayjs'

const props = defineProps({
  record: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])

const formatMoney = (money) => {
  return money ? Number(money).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'
}

const formatTime = (date) => {
  const d = dayjs(date)
  const today = dayjs().startOf('day')
  const recordDay = d.startOf('day')

  if (recordDay.isSame(today)) {
    return d.format('HH:mm')
  } else if (recordDay.isSame(today.subtract(1, 'day'))) {
    return '昨天'
  } else {
    return d.format('MM-DD')
  }
}
</script>

<style lang="scss" scoped>
.note-card {
  background: var(--card-bg);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    transform: scale(0.98);
  }
}

.note-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;

  .category-emoji {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
  }

  .category-name {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);
    flex: 1;
  }

  .note-time {
    font-size: 12px;
    color: var(--text-hint);
  }
}

.note-amount {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;

  &.expense { color: var(--accent-expense); }
  &.income { color: var(--text-primary); }
}

.note-remark {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.note-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .account-tag {
    font-size: 11px;
    color: var(--text-hint);
    background: var(--bg-color);
    padding: 2px 8px;
    border-radius: 10px;
  }

  .share-btn {
    font-size: 14px;
    opacity: 0.6;
    cursor: pointer;

    &:hover { opacity: 1; }
  }
}
</style>
