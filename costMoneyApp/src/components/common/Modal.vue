<template>
  <transition name="modal-fade">
    <div class="modal-overlay" v-if="show" @click.self="handleCancel">
      <div class="modal-content" :class="{ 'confirm-modal': type === 'confirm' }">
        <div class="modal-icon" v-if="icon">{{ icon }}</div>
        <div class="modal-title" v-if="title">{{ title }}</div>
        <div class="modal-message" v-if="message">{{ message }}</div>
        <div class="modal-actions">
          <button class="btn btn-cancel" @click="handleCancel" v-if="type === 'confirm'">
            {{ cancelText }}
          </button>
          <button class="btn btn-confirm" :class="{ 'btn-danger': danger }" @click="handleConfirm">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'alert' // 'alert' | 'confirm'
  },
  title: String,
  message: String,
  icon: String,
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  danger: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'confirm', 'cancel'])

const handleConfirm = () => {
  emit('confirm')
  emit('close')
}

const handleCancel = () => {
  emit('cancel')
  emit('close')
}
</script>

<style lang="scss" scoped>
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
  z-index: 9999;
  padding: 20px;
}

.modal-content {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 28px 24px;
  width: 100%;
  max-width: 300px;
  text-align: center;
  animation: modal-pop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.confirm-modal {
    max-width: 280px;
  }
}

.modal-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.modal-message {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
    padding: 12px 16px;
    border-radius: var(--radius-md);
    font-size: 15px;
    font-weight: 500;
    transition: all 0.2s;

    &:active {
      transform: scale(0.98);
    }
  }

  .btn-cancel {
    background: var(--bg-color);
    color: var(--text-secondary);

    &:hover {
      background: #eee;
    }
  }

  .btn-confirm {
    background: var(--xhs-red);
    color: #fff;

    &:hover {
      opacity: 0.9;
    }

    &.btn-danger {
      background: var(--accent-expense);
    }
  }
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.2s;

  .modal-content {
    transition: transform 0.2s, opacity 0.2s;
  }
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;

  .modal-content {
    transform: scale(0.9);
    opacity: 0;
  }
}

@keyframes modal-pop {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
