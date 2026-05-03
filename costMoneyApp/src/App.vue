<template>
  <router-view />
  <Modal
    :show="showModal"
    :type="modalConfig.type"
    :title="modalConfig.title"
    :message="modalConfig.message"
    :icon="modalConfig.icon"
    :confirm-text="modalConfig.confirmText"
    :cancel-text="modalConfig.cancelText"
    :danger="modalConfig.danger"
    @confirm="handleConfirm"
    @cancel="handleCancel"
    @close="closeModal"
  />
</template>

<script setup>
import { provide } from 'vue'
import Modal from '@/components/common/Modal.vue'
import { useModal } from '@/composables/useModal'

const { showModal, modalConfig, openAlert, openConfirm, closeModal, handleConfirm, handleCancel } = useModal()

provide('$alert', openAlert)
provide('$confirm', openConfirm)
</script>
