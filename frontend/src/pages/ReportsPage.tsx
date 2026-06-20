import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Boxes, ChevronRight, Cloud, FileText, Lightbulb, Loader2 } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { EmptyState, ErrorState } from "@/components/states"
import { useAccountContext } from "@/context/AccountContext"
import { listReports } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { formatDate } from "@/lib/utils"
import type { OptimizationReportResponse, Page } from "@/api/types"

const PAGE_SIZE = 9

export default function ReportsPage() {
  const { accounts, loading: accountsLoading, selectedAccountId } = useAccountContext()
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [data, setData] = useState<Page<OptimizationReportResponse> | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setPage(0)
    setData(null)
  }, [selectedAccountId])

  useEffect(() => {
    if (!selectedAccountId) return
    let active = true
    setLoading(true)
    setError(null)
    listReports(selectedAccountId, page, PAGE_SIZE)
      .then((res) => active && setData(res))
      .catch((e) => active && setError(apiErrorMessage(e, "Failed to load reports")))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
    }
  }, [selectedAccountId, page])

  if (!accountsLoading && accounts.length === 0) {
    return (
      <EmptyState
        icon={Cloud}
        title="No cloud accounts connected"
        description="Connect an AWS account to view its optimization reports."
        action={
          <Button asChild>
            <Link to="/accounts">Connect AWS account</Link>
          </Button>
        }
      />
    )
  }

  const reports = data?.content ?? []

  return (
    <div className="space-y-5">
      <p className="text-sm text-muted-foreground">
        Optimization report history for the selected account.
      </p>

      {loading ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} className="h-32 w-full" />
          ))}
        </div>
      ) : error ? (
        <ErrorState description={error} />
      ) : reports.length === 0 ? (
        <EmptyState
          icon={FileText}
          title="No reports yet"
          description="Run an optimization from the Optimization Center to generate your first report."
          action={
            <Button asChild>
              <Link to="/optimization">Go to Optimization Center</Link>
            </Button>
          }
        />
      ) : (
        <>
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {reports.map((r) => (
              <button
                key={r.id}
                onClick={() => navigate(`/reports/${r.id}`)}
                className="text-left"
              >
                <Card className="group h-full gap-0 p-5 transition-colors hover:border-primary/40">
                  <div className="flex items-center justify-between">
                    <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                      <FileText className="size-4.5" />
                    </div>
                    <ChevronRight className="size-4 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
                  </div>
                  <p className="mt-3 text-sm font-medium">{formatDate(r.createdAt)}</p>
                  <div className="mt-3 flex gap-4 text-sm text-muted-foreground">
                    <span className="flex items-center gap-1.5">
                      <Boxes className="size-4" />
                      {r.totalResources}
                    </span>
                    <span className="flex items-center gap-1.5">
                      <Lightbulb className="size-4" />
                      {r.totalFindings}
                    </span>
                  </div>
                </Card>
              </button>
            ))}
          </div>

          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-between">
              <Button
                variant="outline"
                size="sm"
                disabled={data.first || loading}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                {loading && <Loader2 className="size-4 animate-spin" />}
                Previous
              </Button>
              <span className="text-xs text-muted-foreground">
                Page {data.number + 1} of {data.totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={data.last || loading}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
