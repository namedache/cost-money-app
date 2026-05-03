<template>
  <transition name="success-pop">
    <div class="success-overlay" v-if="show" @click.self="handleClose">
      <div class="success-content">
        <div class="success-checkmark">
          <svg viewBox="0 0 52 52" class="checkmark">
            <circle cx="26" cy="26" r="25" fill="none" stroke="currentColor" stroke-width="2"/>
            <path fill="none" stroke="currentColor" stroke-width="3" d="M14 27l7 7 16-16"/>
          </svg>
        </div>
        <div class="success-emoji">{{ successEmoji }}</div>
        <div class="success-text">{{ successMessage }}</div>
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
  }
})

const emit = defineEmits(['close'])

const successEmoji = ref('')
const successMessage = ref('')

const emojis = ['🎉', '✨', '💖', '👏', '🎊', '💫']
const messages = [
  '记好了，真棒！',
  '又是一笔小财富！',
  '记账小能手就是你！',
  '太厉害了，继续保持！',
  '今天的你很棒！'
]

const handleClose = () => {
  emit('close')
}

watch(() => props.show, (newVal) => {
  if (newVal) {
    successEmoji.value = emojis[Math.floor(Math.random() * emojis.length)]
    successMessage.value = messages[Math.floor(Math.random() * messages.length)]
  }
})
</script>

<style lang="scss" scoped>
.success-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.success-content {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 40px 50px;
  text-align: center;
  animation: pop-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.success-checkmark {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;

  .checkmark {
    width: 100%;
    height: 100%;
    color: var(--xhs-red);
    animation: draw-check 0.5s ease-out 0.2s both;
  }

  circle {
    stroke-dasharray: 166;
    stroke-dashoffset: 166;
    animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
  }

  path {
    stroke-dasharray: 48;
    stroke-dashoffset: 48;
    animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.5s forwards;
  }
}

@keyframes stroke {
  100% { stroke-dashoffset: 0; }
}

@keyframes draw-check {
  0% { transform: scale(0.8); opacity: 0; }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.success-emoji {
  font-size: 48px;
  margin-bottom: 12px;
  animation: bounce 0.6s ease-out 0.3s both;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.success-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.success-pop-enter-active {
  animation: fade-in 0.3s ease;
}

.success-pop-leave-active {
  animation: fade-out 0.3s ease;
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fade-out {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style>
