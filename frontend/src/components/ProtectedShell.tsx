import AppLayout from "@/components/layout/AppLayout"
import { AccountProvider } from "@/context/AccountContext"
import { OptimizationProvider } from "@/context/OptimizationContext"

// Mounts account/optimization providers only inside the authenticated area, so
// public pages never trigger authenticated API calls.
export default function ProtectedShell() {
  return (
    <AccountProvider>
      <OptimizationProvider>
        <AppLayout />
      </OptimizationProvider>
    </AccountProvider>
  )
}
