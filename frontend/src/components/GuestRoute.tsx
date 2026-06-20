import { Navigate, Outlet } from "react-router-dom"
import { Loader2 } from "lucide-react"
import { useAuth } from "@/context/AuthContext"

// Public-only routes (landing, login, register). Authenticated users are sent
// to the dashboard instead.
export default function GuestRoute() {
  const { token, loading } = useAuth()

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="size-6 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (token) {
    console.log("[auth] GuestRoute: already authenticated → redirecting to /dashboard")
    return <Navigate to="/dashboard" replace />
  }

  return <Outlet />
}
