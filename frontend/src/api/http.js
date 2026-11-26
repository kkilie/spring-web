// 공통 fetch 래퍼: JSON 변환 + 에러 처리를 한 곳에서
// vite.config.js 의 프록시 덕분에 '/api/...' 요청은 Spring Boot(8080)로 전달된다.

export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors // { email: '이메일 형식이...' }
  }
}

export async function request(method, url, { params, body } = {}) {
  // params 객체 → 쿼리스트링 (값이 비어 있으면 제외)
  if (params) {
    const qs = new URLSearchParams(
      Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined),
    ).toString()
    if (qs) url += `?${qs}`
  }

  let res
  try {
    res = await fetch(url, {
      method,
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, '서버에 연결할 수 없습니다. Spring Boot가 실행 중인지 확인하세요.')
  }

  if (res.status === 204) return null // 삭제 성공 등 본문 없는 응답

  const data = await res.json().catch(() => null)
  if (!res.ok) {
    // Spring GlobalExceptionHandler 가 내려준 { status, message, fieldErrors }
    throw new ApiError(res.status, data?.message ?? `요청 실패 (${res.status})`, data?.fieldErrors ?? {})
  }
  return data
}
