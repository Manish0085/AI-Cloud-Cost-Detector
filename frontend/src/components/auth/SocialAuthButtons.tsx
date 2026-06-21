import { useEffect, useState } from "react"
import { Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { GithubIcon, GoogleIcon } from "./ProviderIcons"

type Provider = "google" | "github"

// Optional override for the OAuth origin; defaults to a relative path so the
// Vite dev proxy (and a same-origin gateway in prod) forwards it to the backend.
const OAUTH_BASE = import.meta.env.VITE_OAUTH_BASE_URL ?? ""

function authorizationUrl(provider: Provider) {
  const url = `${OAUTH_BASE}/oauth2/authorization/${provider}`;
  console.log("OAuth URL:", url);
  return url;
}

interface Props {
  /** "in" for sign-in copy, "up" for sign-up copy. */
  intent?: "in" | "up"
}

export default function SocialAuthButtons({ intent = "in" }: Props) {
  const [redirecting, setRedirecting] = useState<Provider | null>(null)
  const busy = redirecting !== null

  // Navigate in an effect (not directly in the click handler) so React first
  // paints the loading state before the browser unloads for the OAuth redirect.
  useEffect(() => {
    if (!redirecting) return
    window.location.assign(authorizationUrl(redirecting))
  }, [redirecting])

  function go(provider: Provider) {
    setRedirecting(provider)
  }

  const verb = intent === "up" ? "Sign up" : "Continue"

  return (
    <div className="space-y-4">
      {/* Divider */}
      <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <span className="w-full border-t border-border" />
        </div>
        <div className="relative flex justify-center">
          <span className="bg-card px-3 text-xs font-medium uppercase tracking-wider text-muted-foreground">
            Or continue with
          </span>
        </div>
      </div>

      <div className="grid gap-3">
        <Button
          type="button"
          variant="outline"
          className="w-full"
          disabled={busy}
          onClick={() => go("google")}
          aria-label={`${verb} with Google`}
        >
          {redirecting === "google" ? (
            <>
              <Loader2 className="size-4 animate-spin" aria-hidden="true" />
              Redirecting to Google…
            </>
          ) : (
            <>
              <GoogleIcon className="size-4" />
              {verb} with Google
            </>
          )}
        </Button>

        <Button
          type="button"
          variant="outline"
          className="w-full"
          disabled={busy}
          onClick={() => go("github")}
          aria-label={`${verb} with GitHub`}
        >
          {redirecting === "github" ? (
            <>
              <Loader2 className="size-4 animate-spin" aria-hidden="true" />
              Redirecting to GitHub…
            </>
          ) : (
            <>
              <GithubIcon className="size-4" />
              {verb} with GitHub
            </>
          )}
        </Button>
      </div>

      {busy && (
        <p className="text-center text-xs text-muted-foreground" role="status" aria-live="polite">
          Taking you to your provider to sign in securely…
        </p>
      )}
    </div>
  )
}
