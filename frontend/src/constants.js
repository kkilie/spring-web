// 서버 enum 값 ↔ 화면 표시용 한글 라벨
export const ROLE_LABELS = { ADMIN: '관리자', MANAGER: '매니저', USER: '일반' }
export const STATUS_LABELS = { ACTIVE: '정상', SUSPENDED: '정지', WITHDRAWN: '탈퇴' }

export const formatDate = (iso) => (iso ? iso.slice(0, 16).replace('T', ' ') : '-')
