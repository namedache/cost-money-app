import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '@/utils/api'
import dayjs from 'dayjs'

export const useRecordStore = defineStore('record', () => {
  const records = ref([])

  const getMonthRecords = async (month = dayjs().format('YYYY-MM')) => {
    const startDate = `${month}-01`
    const endDate = dayjs(startDate).endOf('month').format('YYYY-MM-DD')

    const data = await request(`/records?startDate=${startDate}&endDate=${endDate}`)

    // Fetch categories and accounts for populating relations
    const [categories, accounts] = await Promise.all([
      request('/categories'),
      request('/accounts')
    ])

    const categoryMap = new Map(categories.map(c => [c.id, c]))
    const accountMap = new Map(accounts.map(a => [a.id, a]))

    // Populate category and account info
    records.value = data.map(r => ({
      ...r,
      category: r.categoryId ? categoryMap.get(r.categoryId) : null,
      account: r.accountId ? accountMap.get(r.accountId) : null
    }))

    return records.value
  }

  const create = async (recordData) => {
    const data = await request('/records', {
      method: 'POST',
      body: {
        amount: Number(recordData.amount),
        type: recordData.type || 'expense',
        categoryId: recordData.category_id,
        accountId: recordData.account_id,
        note: recordData.note || '',
        date: recordData.date || dayjs().format('YYYY-MM-DD')
      }
    })
    return data
  }

  const update = async (id, data) => {
    return request(`/records/${id}`, {
      method: 'PUT',
      body: data
    })
  }

  const remove = async (id) => {
    return request(`/records/${id}`, {
      method: 'DELETE'
    })
  }

  const getStats = async (month = dayjs().format('YYYY-MM')) => {
    const startDate = `${month}-01`
    const endDate = dayjs(startDate).endOf('month').format('YYYY-MM-DD')

    const [recordsData, accountsData] = await Promise.all([
      request(`/records?startDate=${startDate}&endDate=${endDate}`),
      request('/accounts')
    ])

    let monthIncome = 0
    let monthExpense = 0

    recordsData.forEach(record => {
      if (record.type === 'income') {
        monthIncome += Number(record.amount)
      } else {
        monthExpense += Number(record.amount)
      }
    })

    const totalBalance = accountsData.reduce((sum, acc) => sum + Number(acc.balance), 0)

    return { monthIncome, monthExpense, totalBalance }
  }

  return {
    records,
    getMonthRecords,
    create,
    update,
    remove,
    getStats
  }
})