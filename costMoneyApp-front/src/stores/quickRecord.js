import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '@/utils/api'

export const useQuickRecordStore = defineStore('quickRecord', () => {
  const quickRecords = ref([])

  const getList = async () => {
    const data = await request('/quick-records')
    quickRecords.value = data
    return data
  }

  const create = async (data) => {
    const result = await request('/quick-records', {
      method: 'POST',
      body: {
        name: data.name,
        amount: Number(data.amount),
        icon: data.icon || '⚡',
        color: data.color || '#1E88E5',
        categoryId: data.category_id,
        accountId: data.account_id,
        sortOrder: data.sort_order || 0
      }
    })
    await getList()
    return result
  }

  const update = async (id, data) => {
    await request(`/quick-records/${id}`, {
      method: 'PUT',
      body: {
        name: data.name,
        amount: Number(data.amount),
        icon: data.icon,
        color: data.color,
        categoryId: data.category_id,
        accountId: data.account_id,
        sortOrder: data.sort_order || 0
      }
    })
    await getList()
  }

  const remove = async (id) => {
    await request(`/quick-records/${id}`, {
      method: 'DELETE'
    })
    await getList()
  }

  return {
    quickRecords,
    getList,
    create,
    update,
    remove
  }
})