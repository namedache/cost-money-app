import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Main.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'add',
        name: 'AddRecord',
        component: () => import('@/views/AddRecord.vue')
      },
      {
        path: 'edit/:id',
        name: 'EditRecord',
        component: () => import('@/views/AddRecord.vue'),
        props: true
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue')
      },
      {
        path: 'me',
        name: 'Me',
        component: () => import('@/views/Me.vue')
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/Records.vue')
      },
      {
        path: 'accounts',
        name: 'Accounts',
        component: () => import('@/views/Accounts.vue')
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/Categories.vue')
      },
      {
        path: 'budgets',
        name: 'Budgets',
        component: () => import('@/views/Budgets.vue')
      },
      {
        path: 'auto-rules',
        name: 'AutoRules',
        component: () => import('@/views/AutoRules.vue')
      },
      {
        path: 'quick-records',
        name: 'QuickRecords',
        component: () => import('@/views/QuickRecords.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const publicPages = ['/login', '/register']
  if (!publicPages.includes(to.path) && !userStore.isLoggedIn()) {
    next('/login')
  } else {
    next()
  }
})

export default router
