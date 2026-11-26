import { request } from './http'

const BASE = '/api/admin'

// ===== 대시보드 =====
export const fetchDashboard = () => request('GET', `${BASE}/dashboard`)

// ===== 회원 =====
// params: { keyword, status, role, page, size }
export const fetchMembers = (params) => request('GET', `${BASE}/members`, { params })
export const fetchMember = (id) => request('GET', `${BASE}/members/${id}`)
export const createMember = (body) => request('POST', `${BASE}/members`, { body })
export const updateMember = (id, body) => request('PUT', `${BASE}/members/${id}`, { body })
export const changeMemberStatus = (id, status) =>
  request('PATCH', `${BASE}/members/${id}/status`, { body: { status } })
export const deleteMember = (id) => request('DELETE', `${BASE}/members/${id}`)
