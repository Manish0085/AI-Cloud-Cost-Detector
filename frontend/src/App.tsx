import { Navigate, Route, Routes } from "react-router-dom"
import ProtectedRoute from "@/components/ProtectedRoute"
import ProtectedShell from "@/components/ProtectedShell"
import GuestRoute from "@/components/GuestRoute"
import LandingPage from "@/pages/LandingPage"
import LoginPage from "@/pages/LoginPage"
import RegisterPage from "@/pages/RegisterPage"
import OAuthSuccessPage from "@/pages/OAuthSuccessPage"
import DashboardPage from "@/pages/DashboardPage"
import CloudAccountsPage from "@/pages/CloudAccountsPage"
import ResourceExplorerPage from "@/pages/ResourceExplorerPage"
import ResourceDetailsPage from "@/pages/ResourceDetailsPage"
import OptimizationCenterPage from "@/pages/OptimizationCenterPage"
import OptimizationResultsPage from "@/pages/OptimizationResultsPage"
import ReportsPage from "@/pages/ReportsPage"
import ReportDetailsPage from "@/pages/ReportDetailsPage"
import SettingsPage from "@/pages/SettingsPage"

export default function App() {
  return (
    <Routes>
      {/* OAuth2 callback — must stay ungated: the token isn't stored yet, so
          neither the guest guard nor the auth guard should intercept it. */}
      <Route path="/oauth-success" element={<OAuthSuccessPage />} />

      {/* Public (guest-only): authenticated users are redirected to /dashboard */}
      <Route element={<GuestRoute />}>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      {/* Authenticated */}
      <Route element={<ProtectedRoute />}>
        <Route element={<ProtectedShell />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/accounts" element={<CloudAccountsPage />} />
          <Route path="/explorer" element={<ResourceExplorerPage />} />
          <Route
            path="/explorer/:accountId/:type/:resourceId"
            element={<ResourceDetailsPage />}
          />
          <Route path="/optimization" element={<OptimizationCenterPage />} />
          <Route path="/optimization/results" element={<OptimizationResultsPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/reports/:reportId" element={<ReportDetailsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
