import { useEffect, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { ArrowLeft, Boxes, Download, Info, Lightbulb } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import StatCard from "@/components/StatCard"
import ExecutiveSummaryCard from "@/components/ExecutiveSummaryCard"
import { ErrorState } from "@/components/states"
import { getReportDetails } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { formatDate } from "@/lib/utils"
import type { OptimizationReportDetails } from "@/api/types"

export default function ReportDetailsPage() {
  const { reportId } = useParams<{ reportId: string }>()
  const [report, setReport] = useState<OptimizationReportDetails | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!reportId) return
    let active = true
    setLoading(true)
    getReportDetails(reportId)
      .then((res) => active && setReport(res))
      .catch((e) => active && setError(apiErrorMessage(e, "Failed to load report")))
      .finally(() => active && setLoading(false))
    return () => {
      active = false
    }
  }, [reportId])

  function exportReport() {
    if (!report) return
    const blob = new Blob([JSON.stringify(report, null, 2)], { type: "application/json" })
    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `optimization-report-${report.id}.json`
    a.click()
    URL.revokeObjectURL(url)
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <Button asChild variant="ghost" size="sm" className="-ml-2">
          <Link to="/reports">
            <ArrowLeft className="size-4" />
            Back to Reports
          </Link>
        </Button>
        {report && (
          <Button variant="outline" size="sm" onClick={exportReport}>
            <Download className="size-4" />
            Export Report
          </Button>
        )}
      </div>

      {loading ? (
        <div className="space-y-6">
          <Skeleton className="h-8 w-64" />
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            <Skeleton className="h-28 w-full" />
            <Skeleton className="h-28 w-full" />
            <Skeleton className="h-28 w-full" />
          </div>
          <Skeleton className="h-40 w-full" />
        </div>
      ) : error ? (
        <ErrorState description={error} />
      ) : report ? (
        <>
          <div>
            <h2 className="text-xl font-semibold tracking-tight">Optimization Report</h2>
            <p className="mt-1 text-sm text-muted-foreground">{formatDate(report.createdAt)}</p>
          </div>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <StatCard
              label="Resources Analyzed"
              value={report.totalResources}
              icon={Boxes}
              iconClassName="bg-sky-500/15 text-sky-400"
            />
            <StatCard
              label="Total Findings"
              value={report.totalFindings}
              icon={Lightbulb}
              iconClassName="bg-amber-500/15 text-amber-400"
            />
          </div>

          {report.executiveSummary && <ExecutiveSummaryCard summary={report.executiveSummary} />}

          <Card className="border-dashed">
            <CardContent className="flex items-start gap-3 px-5 py-4 text-sm text-muted-foreground">
              <Info className="mt-0.5 size-4 shrink-0" />
              <p>
                Per-resource findings and recommendations aren&apos;t stored for historical
                reports. Run a new analysis from the Optimization Center to see the full
                resource-level breakdown.
              </p>
            </CardContent>
          </Card>
        </>
      ) : null}
    </div>
  )
}
