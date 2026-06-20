import axios from "axios"

export const TOKEN_KEY = "acco_token"

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

const api = axios.create({
  // Defaults to the Vite dev proxy path; override with VITE_API_URL in prod.
  baseURL: import.meta.env.VITE_API_URL ?? "/api/v1",
  headers: { "Content-Type": "application/json" },
})

// Attach the bearer token to every request.
api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Returns true if the JWT is absent or past its `exp`. Used to distinguish an
// expired session from an authorization failure: the backend returns 403 both
// for invalid/expired tokens AND for "Access Denied" (e.g. an account the user
// doesn't own). We must NOT log the user out for the latter.
export function isTokenExpired(token: string | null = getToken()): boolean {
  if (!token) return true
  try {
    const payload = token.split(".")[1]
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")))
    if (!json.exp) return false
    return json.exp * 1000 <= Date.now()
  } catch {
    // Undecodable token — don't force a logout off the back of a single 403.
    return false
  }
}

// On 401/403, only end the session when the token is actually missing/expired.
// A 403 with a still-valid token is an authorization error (surface it instead).
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status
    if ((status === 401 || status === 403) && isTokenExpired()) {
      clearToken()
      if (window.location.pathname !== "/login") {
        window.location.assign("/login")
      }
    }
    return Promise.reject(error)
  }
)

// Pull a human-readable message out of an axios error. The backend uses three
// error shapes: a raw string body, an ApiError object ({ message }), and a
// field-keyed validation map ({ accountName: "...", accessKey: "..." }).
export function apiErrorMessage(error: unknown, fallback = "Something went wrong"): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as Record<string, unknown> | string | undefined
    if (typeof data === "string") {
      if (data.trim()) return data
    } else if (data && typeof data === "object") {
      if (typeof data.message === "string" && data.message.trim()) {
        return data.message
      }
      // Validation map: join the field-level messages.
      const fieldMessages = Object.values(data).filter(
        (v): v is string => typeof v === "string" && v.trim().length > 0
      )
      if (fieldMessages.length) return fieldMessages.join(". ")
    }
    if (error.message) return error.message
  }
  return fallback
}

export default api
