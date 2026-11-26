<script setup>
import { reactive, ref, watch } from 'vue'
import { createMember, updateMember } from '@/api/admin'
import { ROLE_LABELS, STATUS_LABELS } from '@/constants'

// member 가 있으면 수정, null 이면 등록
const props = defineProps({
  member: { type: Object, default: null },
})
const emit = defineEmits(['close', 'saved'])

const emptyForm = () => ({ name: '', email: '', phone: '', role: 'USER', status: 'ACTIVE' })
const form = reactive(emptyForm())
const fieldErrors = ref({})
const error = ref('')
const saving = ref(false)

// 모달이 열릴 때마다 폼을 대상 회원 값으로 채운다
watch(
  () => props.member,
  (m) => {
    Object.assign(form, emptyForm(), m ? { ...m, phone: m.phone ?? '' } : {})
    fieldErrors.value = {}
    error.value = ''
  },
  { immediate: true },
)

async function submit() {
  saving.value = true
  fieldErrors.value = {}
  error.value = ''
  const body = { name: form.name, email: form.email, phone: form.phone, role: form.role, status: form.status }
  try {
    const saved = props.member
      ? await updateMember(props.member.id, body) // PUT  /api/admin/members/{id}
      : await createMember(body) //                  POST /api/admin/members
    emit('saved', saved)
  } catch (e) {
    // 400: 필드별 검증 에러 / 409: 이메일 중복 등
    fieldErrors.value = e.fieldErrors ?? {}
    error.value = e.message
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="backdrop" @click.self="emit('close')">
    <form class="modal card" @submit.prevent="submit">
      <h2>{{ member ? '회원 수정' : '회원 등록' }}</h2>

      <div v-if="error" class="error-box">{{ error }}</div>

      <label>
        이름 *
        <input v-model="form.name" placeholder="홍길동" />
        <small v-if="fieldErrors.name">{{ fieldErrors.name }}</small>
      </label>
      <label>
        이메일 *
        <input v-model="form.email" type="text" placeholder="user@example.com" />
        <small v-if="fieldErrors.email">{{ fieldErrors.email }}</small>
      </label>
      <label>
        전화번호
        <input v-model="form.phone" placeholder="010-1234-5678" />
        <small v-if="fieldErrors.phone">{{ fieldErrors.phone }}</small>
      </label>
      <div class="row">
        <label>
          권한
          <select v-model="form.role">
            <option v-for="(label, value) in ROLE_LABELS" :key="value" :value="value">{{ label }}</option>
          </select>
        </label>
        <label>
          상태
          <select v-model="form.status">
            <option v-for="(label, value) in STATUS_LABELS" :key="value" :value="value">{{ label }}</option>
          </select>
        </label>
      </div>

      <div class="actions">
        <button type="button" class="btn" @click="emit('close')">취소</button>
        <button type="submit" class="btn btn-primary" :disabled="saving">
          {{ saving ? '저장 중...' : '저장' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  z-index: 10;
}
.modal { width: 100%; max-width: 440px; display: flex; flex-direction: column; gap: 14px; }
.modal h2 { margin: 0 0 4px; font-size: 18px; }
label { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: var(--muted); }
label input, label select { color: var(--text); }
small { color: var(--danger); }
.row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 6px; }
.error-box { margin: 0; }
</style>
