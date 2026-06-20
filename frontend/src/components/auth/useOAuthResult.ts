import { useEffect, useRef, useState } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import { toast } from "sonner"
import { useAuth } from "@/context/AuthContext"
import { apiErrorMessage } from "@/api/client"

const ERROR_MESSAGES: Record<string, string> = {
  oauth_failed: "OAuth sign-in failed. Please try again.",
  access_denied: "Access was denied. You can try a different account or provider.",
}

function mapError(code: string): string {
  return ERROR_MESSAGES[code] ?? "Sign-in failed. Please try again."
}

/**
 * Handles the OAuth2 redirect back to the SPA. Reads:
 *  - `?token=<jwt>`  → completes login, shows success, routes to the dashboard
 *  - `?error=<code>` → returns a friendly message for an inline alert + toast
 * Query params are stripped afterwards so a refresh doesn't re-trigger.
 * Returns the current error message (if any) for inline display.
 */
export function useOAuthResult(): string | null {
  const [params, setParams] = useSearchParams()
  const navigate = useNavigate()
  const { loginWithToken } = useAuth()
  const [error, setError] = useState<string | null>(null)
  const handled = useRef(false)

  useEffect(() => {
    if (handled.current) return
    const errorCode = params.get("error")
    const token = params.get("token")

    if (errorCode) {
      handled.current = true
      const message = mapError(errorCode)
      setError(message)
      toast.error(message)
      params.delete("error")
      setParams(params, { replace: true })
      return
    }

    if (token) {
      handled.current = true
      params.delete("token")
      setParams(params, { replace: true })
      loginWithToken(token)
        .then(() => {
          toast.success("Signed in successfully")
          navigate("/dashboard", { replace: true })
        })
        .catch((e) => {
          const message = apiErrorMessage(e, "Could not complete sign-in")
          setError(message)
          toast.error(message)
        })
    }
  }, [params, setParams, navigate, loginWithToken])

  return error
}
