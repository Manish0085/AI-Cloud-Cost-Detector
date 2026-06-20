import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react"
import * as authApi from "@/api/auth"
import { clearToken, getToken, setToken } from "@/api/client"
import type { CurrentUser } from "@/api/types"

interface AuthContextValue {
  user: CurrentUser | null
  token: string | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  signup: (name: string, email: string, password: string) => Promise<void>
  loginWithToken: (token: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUser | null>(null)
  const [token, setTokenState] = useState<string | null>(() => getToken())
  const [loading, setLoading] = useState<boolean>(!!getToken())

  // Hydrate the user from the token on first load (restores session on refresh).
  useEffect(() => {
    let active = true
    if (!getToken()) {
      console.log("[auth] startup: no token in storage → unauthenticated")
      setLoading(false)
      return
    }
    console.log("[auth] startup: token found → restoring session via /auth/me")
    authApi
      .getMe()
      .then((me) => {
        if (active) {
          console.log("[auth] startup: session restored for", me.email)
          setUser(me)
        }
      })
      .catch(() => {
        console.warn("[auth] startup: token invalid/expired → clearing session")
        clearToken()
        if (active) {
          setTokenState(null)
          setUser(null)
        }
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  const login = useCallback(async (email: string, password: string) => {
    const res = await authApi.login(email, password)
    setToken(res.token)
    setTokenState(res.token)
    const me = await authApi.getMe()
    setUser(me)
  }, [])

  const signup = useCallback(async (name: string, email: string, password: string) => {
    const res = await authApi.register(name, email, password)
    setToken(res.token)
    setTokenState(res.token)
    const me = await authApi.getMe()
    setUser(me)
  }, [])

  // Completes an OAuth redirect: store the token the backend handed back and
  // hydrate the user from it.
  const loginWithToken = useCallback(async (t: string) => {
    console.log("[auth] loginWithToken: storing token in localStorage")
    setToken(t)
    setTokenState(t)
    const me = await authApi.getMe()
    console.log("[auth] loginWithToken: user profile loaded:", me.email)
    setUser(me)
  }, [])

  const logout = useCallback(() => {
    console.log("[auth] logout: clearing token + user")
    clearToken()
    setTokenState(null)
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({ user, token, loading, login, signup, loginWithToken, logout }),
    [user, token, loading, login, signup, loginWithToken, logout]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider")
  return ctx
}
