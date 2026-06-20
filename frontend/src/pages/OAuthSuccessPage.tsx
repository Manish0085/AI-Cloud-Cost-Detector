import { useEffect, useRef, useState } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import { Loader2, Sparkles } from "lucide-react"
import { toast } from "sonner"
import { useAuth } from "@/context/AuthContext"
import { apiErrorMessage } from "@/api/client"

/**
 * Landing target for the Spring Security OAuth2 success handler, which redirects
 * to `/oauth-success?token=<JWT>`. Reads the token, completes the session via
 * AuthContext, and routes to the dashboard (or back to /login on failure).
 */
export default function OAuthSuccessPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const { loginWithToken, logout } = useAuth()
  const handled = useRef(false)
  const [status, setStatus] = useState<"loading" | "error">("loading")

  useEffect(() => {
    if (handled.current) return // guard React StrictMode double-invoke
    handled.current = true

    console.log("[OAuth] success callback executing")
    const token = params.get("token")
    const error = params.get("error")
    console.log(
      "[OAuth] extracted token:",
      token ? `${token.slice(0, 12)}… (len ${token.length})` : null,
      "| error param:",
      error
    )

    if (error || !token) {
      console.warn("[OAuth] missing token / error present → redirecting to /login")
      setStatus("error")
      navigate(`/login?error=${error || "oauth_failed"}`, { replace: true })
      return
    }

    ;(async () => {
      try {
        console.log("[OAuth] storing token + loading user profile (/auth/me)")
        await loginWithToken(token)
        console.log("[OAuth] authentication state populated → redirecting to /dashboard")
        toast.success("Signed in successfully")
        navigate("/dashboard", { replace: true })
      } catch (e) {
        console.error("[OAuth] failed to load user; clearing session:", e)
        logout()
        setStatus("error")
        toast.error(apiErrorMessage(e, "Could not complete sign-in"))
        navigate("/login?error=oauth_failed", { replace: true })
      }
    })()
  }, [params, navigate, loginWithToken, logout])

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-background px-4">
      <div className="absolute inset-0 bg-grid [mask-image:radial-gradient(70%_60%_at_50%_40%,black,transparent)]" />
      <div className="absolute inset-0 bg-glow" />
      <div className="relative flex flex-col items-center gap-5 text-center">
        <div className="flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 text-white shadow-lg shadow-violet-900/40">
          <Sparkles className="size-6" />
        </div>
        <div className="flex items-center gap-2 text-muted-foreground">
          <Loader2 className="size-5 animate-spin text-primary" />
          <span className="text-sm">
            {status === "loading" ? "Completing sign-in…" : "Redirecting…"}
          </span>
        </div>
      </div>
    </div>
  )
}
