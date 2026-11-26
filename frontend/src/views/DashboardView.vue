<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchDashboard } from '@/api/admin'
import { ROLE_LABELS, STATUS_LABELS, formatDate } from '@/constants'

const summary = ref(null)
const error = ref('')

onMounted(async () => {
  try {
    summary.value = await fetchDashboard() // GET /api/admin/dashboard
  } catch (e) {
    error.value = e.message
  }
})
</script>

<template>
  <div v-if="error" class="error-box">{{ error }}</div>

  <template v-if="summary">
    <section class="stats">
      <div class="card stat">
        <span class="label">전체 회원</span>
        <strong>{{ summary.totalMembers }}</strong>
      </div>
      <div class="card stat">
        <span class="label">정상</span>
        <strong class="ok">{{ summary.activeMembers }}</strong>
      </div>
      <div class="card stat">
        <span class="label">정지</span>
        <strong class="warn">{{ summary.suspendedMembers }}</strong>
      </div>
      <div class="card stat">
        <span class="label">탈퇴</span>
        <strong class="muted">{{ summary.withdrawnMembers }}</strong>
      </div>
      <div class="card stat">
        <span class="label">관리자 수</span>
        <strong>{{ summary.adminCount }}</strong>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>최근 가입 회원</h2>
        <RouterLink to="/members" class="btn btn-sm">전체 보기</RouterLink>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>이름</th><th>이메일</th><th>권한</th><th>상태</th><th>가입일</th></tr>
          </thead>
          <tbody>
            <tr v-for="m in summary.recentMembers" :key="m.id">
              <td>{{ m.name }}</td>
              <td>{{ m.email }}</td>
              <td><span :class="['badge', `badge-${m.role}`]">{{ ROLE_LABELS[m.role] }}</span></td>
              <td><span :class="['badge', `badge-${m.status}`]">{{ STATUS_LABELS[m.status] }}</span></td>
              <td>{{ formatDate(m.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </template>
</template>

<style scoped>
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}
.stat { display: flex; flex-direction: column; gap: 8px; }
.stat .label { color: var(--muted); font-size: 13px; }
.stat strong { font-size: 28px; }
.ok { color: #16a34a; }
.warn { color: #d97706; }
.muted { color: var(--muted); }

.section-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.section-head h2 { font-size: 16px; margin: 0; }
.section-head .btn { text-decoration: none; }
</style>
