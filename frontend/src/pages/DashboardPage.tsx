import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { Boxes, Cloud, Lightbulb, TrendingUp } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import StatCard from "@/components/StatCard"
import ResourceDistributionChart from "@/components/charts/ResourceDistributionChart"
import { EmptyState, ErrorState, LoadingState } from "@/components/states"
import { useAccountContext } from "@/context/AccountContext"
import { loadDashboard } from "@/api/dashboard"
import { apiErrorMessage } from "@/api/client"
import { formatDate, formatNumber } from "@/lib/utils"
import type { DashboardData } from "@/api/types"

export default function DashboardPage() {
  const { accounts, loading: accountsLoading, selectedAccount, selectedAccountId } =
    useAccountContext()
  const [data, setData] = useState<DashboardData | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!selectedAccountId) return
    let active = true
    setLoading(true)
    setError(null)
    setData(null)
    loadDashboard(selectedAccountId)
      .then((d) => active && setData(d))
      .catch((e) => active && setError(apiErrorMessage(e, "Failed to load dashboard")))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
    }
  }, [selectedAccountId])

  if (accountsLoading) return <LoadingState label="Loading accounts…" />

  if (accounts.length === 0) {
    return (
      <EmptyState
        icon={Cloud}
        title="No cloud accounts connected"
        description="Connect your first AWS account to populate your dashboard with resources and findings."
        action={
          <Button asChild>
            <Link to="/accounts">Connect AWS account</Link>
          </Button>
        }
      />
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold tracking-tight">
          {selectedAccount?.accountName ?? "Overview"}
        </h2>
        <p className="text-sm text-muted-foreground">
          Cost optimization overview for this account.
        </p>
      </div>

      {error ? (
        <ErrorState description={error} />
      ) : (
        <>
          {/* KPI cards */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            <StatCard
              label="Connected Accounts"
              value={accounts.length}
              icon={Cloud}
              iconClassName="bg-violet-500/15 text-violet-400"
            />
            <StatCard
              label="Total Resources"
              value={formatNumber(data?.totalResources)}
              icon={Boxes}
              iconClassName="bg-sky-500/15 text-sky-400"
              loading={loading}
            />
            <StatCard
              label="Total Findings"
              value={formatNumber(data?.totalFindings)}
              icon={Lightbulb}
              iconClassName="bg-amber-500/15 text-amber-400"
              hint="From the latest report"
              loading={loading}
            />
          </div>

          {/* Distribution + recent reports */}
          <div className="grid gap-4 lg:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Resource Distribution</CardTitle>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <LoadingState className="py-10" />
                ) : (
                  <ResourceDistributionChart distribution={data?.resourceDistribution ?? {}} />
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex-row items-center justify-between">
                <CardTitle className="text-base">Recent Reports</CardTitle>
                <Button asChild variant="ghost" size="sm">
                  <Link to="/reports">View all</Link>
                </Button>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <LoadingState className="py-8" />
                ) : data && data.recentReports.length > 0 ? (
                  <ul className="divide-y divide-border">
                    {data.recentReports.map((r) => (
                      <li key={r.id}>
                        <Link
                          to={`/reports/${r.id}`}
                          className="flex items-center justify-between gap-3 py-3 text-sm transition-colors hover:text-primary"
                        >
                          <span>{formatDate(r.createdAt)}</span>
                          <span className="flex items-center gap-3 text-muted-foreground">
                            <span>{r.totalResources} res</span>
                            <span>{r.totalFindings} findings</span>
                          </span>
                        </Link>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <EmptyState
                    icon={TrendingUp}
                    title="No reports yet"
                    description="Run an optimization to generate your first report."
                    className="border-0 bg-transparent py-8"
                  />
                )}
              </CardContent>
            </Card>
          </div>
        </>
      )}
    </div>
  )
}
