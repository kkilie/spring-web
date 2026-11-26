import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'dashboard', component: DashboardView, meta: { title: '대시보드' } },
    {
      path: '/members',
      name: 'members',
      component: () => import('../views/MemberListView.vue'),
      meta: { title: '회원 관리' },
    },
  ],
})

export default router
