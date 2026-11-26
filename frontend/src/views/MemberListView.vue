<script setup>
import { reactive, ref, onMounted } from 'vue'
import { fetchMembers, changeMemberStatus, deleteMember } from '@/api/admin'
import { ROLE_LABELS, STATUS_LABELS, formatDate } from '@/constants'
import MemberFormModal from '@/components/MemberFormModal.vue'

// 검색 조건 → 그대로 쿼리스트링이 된다 (?keyword=&status=&role=&page=&size=)
const filters = reactive({ keyword: '', status: '', role: '' })
const page = ref(0)
const size = 10

const result = ref({ content: [], totalElements: 0, totalPages: 0, page: 0 })
const loading = ref(false)
const error = ref('')

// 모달 상태: false = 닫힘, null = 등록, 객체 = 수정
const modalTarget = ref(false)
const deleteTarget = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    // GET /api/admin/members?keyword=...&page=0&size=10
    result.value = await fetchMembers({ ...filters, page: page.value, size })
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 0
  load()
}

function resetFilters() {
  Object.assign(filters, { keyword: '', status: '', role: '' })
  search()
}

function goPage(p) {
  page.value = p
  load()
}

async function onStatusChange(member, status) {
  try {
    await changeMemberStatus(member.id, status) // PATCH /api/admin/members/{id}/status
    load()
  } catch (e) {
    error.value = e.message
  }
}

async function confirmDelete() {
  try {
    await deleteMember(deleteTarget.value.id) // DELETE /api/admin/members/{id}
    deleteTarget.value = null
    // 현재 페이지의 마지막 항목을 지웠으면 앞 페이지로
    if (result.value.content.length === 1 && page.value > 0) page.value--
    load()
  } catch (e) {
    error.value = e.message
    deleteTarget.value = null
  }
}

function onSaved() {
  modalTarget.value = false
  load()
}

onMounted(load)
</script>

<template>
  <!-- 검색 영역 -->
  <form class="card toolbar" @submit.prevent="search">
    <input v-model="filters.keyword" placeholder="이름 또는 이메일 검색" class="keyword" />
    <select v-model="filters.status">
      <option value="">전체 상태</option>
      <option v-for="(label, value) in STATUS_LABELS" :key="value" :value="value">{{ label }}</option>
    </select>
    <select v-model="filters.role">
      <option value="">전체 권한</option>
      <option v-for="(label, value) in ROLE_LABELS" :key="value" :value="value">{{ label }}</option>
    </select>
    <button type="submit" class="btn btn-primary">검색</button>
    <button type="button" class="btn" @click="resetFilters">초기화</button>
    <span class="spacer" />
    <button type="button" class="btn btn-primary" @click="modalTarget = null">+ 회원 등록</button>
  </form>

  <div v-if="error" class="error-box">{{ error }}</div>

  <!-- 목록 -->
  <section class="card">
    <div class="list-head">
      총 <strong>{{ result.totalElements }}</strong>명
      <span v-if="loading" class="loading">불러오는 중...</span>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th><th>이름</th><th>이메일</th><th>전화번호</th>
            <th>권한</th><th>상태</th><th>가입일</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in result.content" :key="m.id">
            <td>{{ m.id }}</td>
            <td>{{ m.name }}</td>
            <td>{{ m.email }}</td>
            <td>{{ m.phone ?? '-' }}</td>
            <td><span :class="['badge', `badge-${m.role}`]">{{ ROLE_LABELS[m.role] }}</span></td>
            <td>
              <select
                :value="m.status"
                :class="['status-select', `badge-${m.status}`]"
                @change="onStatusChange(m, $event.target.value)"
              >
                <option v-for="(label, value) in STATUS_LABELS" :key="value" :value="value">{{ label }}</option>
              </select>
            </td>
            <td>{{ formatDate(m.createdAt) }}</td>
            <td class="row-actions">
              <button class="btn btn-sm" @click="modalTarget = m">수정</button>
              <button class="btn btn-sm btn-danger" @click="deleteTarget = m">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && result.content.length === 0" class="empty">조건에 맞는 회원이 없습니다.</div>
    </div>

    <!-- 페이지네이션 -->
    <nav v-if="result.totalPages > 1" class="pagination">
      <button class="btn btn-sm" :disabled="page === 0" @click="goPage(page - 1)">이전</button>
      <button
        v-for="p in result.totalPages"
        :key="p"
        :class="['btn', 'btn-sm', { 'btn-primary': p - 1 === page }]"
        @click="goPage(p - 1)"
      >
        {{ p }}
      </button>
      <button class="btn btn-sm" :disabled="page >= result.totalPages - 1" @click="goPage(page + 1)">다음</button>
    </nav>
  </section>

  <!-- 등록/수정 모달 -->
  <MemberFormModal
    v-if="modalTarget !== false"
    :member="modalTarget"
    @close="modalTarget = false"
    @saved="onSaved"
  />

  <!-- 삭제 확인 -->
  <div v-if="deleteTarget" class="confirm-backdrop" @click.self="deleteTarget = null">
    <div class="card confirm">
      <p><strong>{{ deleteTarget.name }}</strong> ({{ deleteTarget.email }}) 회원을 삭제할까요?</p>
      <p class="hint">삭제하면 되돌릴 수 없습니다.</p>
      <div class="confirm-actions">
        <button class="btn" @click="deleteTarget = null">취소</button>
        <button class="btn btn-primary danger-fill" @click="confirmDelete">삭제</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-bottom: 16px; padding: 14px 16px; }
.keyword { flex: 1 1 220px; min-width: 0; }
.spacer { flex: 1; }

.list-head { margin-bottom: 12px; color: var(--muted); }
.list-head strong { color: var(--text); }
.loading { margin-left: 8px; font-size: 13px; }

.status-select { border: none; border-radius: 999px; padding: 3px 8px; font-size: 12px; font-weight: 600; cursor: pointer; }
.row-actions { display: flex; gap: 6px; }

.pagination { display: flex; flex-wrap: wrap; justify-content: center; gap: 4px; margin-top: 16px; }

.confirm-backdrop {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.45);
  display: flex; align-items: center; justify-content: center; padding: 16px; z-index: 10;
}
.confirm { max-width: 380px; width: 100%; }
.confirm p { margin: 0 0 8px; }
.confirm .hint { color: var(--muted); font-size: 13px; }
.confirm-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
.danger-fill { background: var(--danger); border-color: var(--danger); }
.danger-fill:hover { background: #b91c1c; }
</style>
