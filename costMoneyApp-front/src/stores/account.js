import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '@/utils/api'

export const useAccountStore = defineStore('account', () => {
  const accounts = ref([])

  const getList = async () => {
    const data = await request('/accounts')
    accounts.value = data
    return data
  }

  const create = async (data) => {
    const result = await request('/accounts', {
      method: 'POST',
      body: {
        name: data.name,
        icon: data.icon || '💳',
        color: data.color || '#1E88E5',
        balance: Number(data.balance) || 0,
        sortOrder: data.sortOrder || 0,
        isActive: true
      }
    })
    await getList()
    return result
  }

  const update = async (id, data) => {
    await request(`/accounts/${id}`, {
      method: 'PUT',
      body: data
    })
    await getList()
  }

  const remove = async (id) => {
    await request(`/accounts/${id}`, {
      method: 'DELETE'
    })
    await getList()
  }

  return {
    accounts,
    getList,
    create,
    update,
    remove
  }
})