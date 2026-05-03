import { ref } from 'vue'

const showModal = ref(false)
const modalConfig = ref({
  type: 'alert',
  title: '',
  message: '',
  icon: '',
  confirmText: '确定',
  cancelText: '取消',
  danger: false,
  onConfirm: null,
  onCancel: null
})

export function useModal() {
  const modal = ref(null)

  const openAlert = (options) => {
    const { title = '', message = '', icon = '', confirmText = '确定', onConfirm = null } = options
    modalConfig.value = {
      type: 'alert',
      title,
      message,
      icon,
      confirmText,
      cancelText: '取消',
      danger: false,
      onConfirm,
      onCancel: null
    }
    showModal.value = true
  }

  const openConfirm = (options) => {
    const { title = '', message = '', icon = '', confirmText = '确定', cancelText = '取消', danger = false, onConfirm = null, onCancel = null } = options
    modalConfig.value = {
      type: 'confirm',
      title,
      message,
      icon,
      confirmText,
      cancelText,
      danger,
      onConfirm,
      onCancel
    }
    showModal.value = true
  }

  const closeModal = () => {
    showModal.value = false
  }

  const handleConfirm = () => {
    if (modalConfig.value.onConfirm) {
      modalConfig.value.onConfirm()
    }
    closeModal()
  }

  const handleCancel = () => {
    if (modalConfig.value.onCancel) {
      modalConfig.value.onCancel()
    }
    closeModal()
  }

  return {
    showModal,
    modalConfig,
    openAlert,
    openConfirm,
    closeModal,
    handleConfirm,
    handleCancel
  }
}
