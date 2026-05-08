<template>
  <div class="refresh-loader" :class="{ spinning: isSpinning }">
    <svg viewBox="0 0 24 24" class="refresh-icon">
      <path d="M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46A7.93 7.93 0 0020 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74A7.93 7.93 0 004 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z" fill="currentColor"/>
    </svg>
  </div>
</template>

<script setup>
defineProps({
  isSpinning: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.refresh-loader {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;

  .refresh-icon {
    width: 24px;
    height: 24px;
    color: var(--xhs-red);
  }

  &.spinning .refresh-icon {
    animation: spin 1s linear infinite;
  }
}
</style>
