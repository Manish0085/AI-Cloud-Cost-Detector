import { Link } from "react-router-dom"
import { Boxes, DollarSign, Lightbulb, Wand2 } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import StatCard from "@/components/StatCard"
import ExecutiveSummaryCard from "@/components/ExecutiveSummaryCard"
import ResourceCard from "@/components/ResourceCard"
import { EmptyState } from "@/components/states"
import { useOptimization } from "@/context/OptimizationContext"
import { formatCurrency, formatDate } from "@/lib/utils"

export default function OptimizationResultsPage() {
  const { lastRun } = useOptimization()

  if (!lastRun) {
    return (
      <EmptyState
        icon={Wand2}
        title="No optimization results yet"
        description="Run an analysis from the Optimization Center to see results here."
        action={
          <Button asChild>
            <Link to="/optimization">Go to Optimization Center</Link>
          </Button>
        }
      />
    )
  }

  const { result, accountName, scope, ranAt } = lastRun

  // Total estimated monthly savings = sum of per-finding savings across resources.
  const totalSavings = result.resources?.reduce(
    (sum, r) => sum + (r.findings?.reduce((s, f) => s + (f.estimatedMonthlySavings ?? 0), 0) ?? 0),
    0
  )
  const hasSavings = result.resources?.some((r) =>
    r.findings?.some((f) => typeof f.estimatedMonthlySavings === "number")
  )

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-semibold tracking-tight">Optimization Results</h2>
            <Badge variant="secondary">{scope}</Badge>
          </div>
          <p className="mt-1 text-sm text-muted-foreground">
            {accountName} · {formatDate(ranAt)}
          </p>
        </div>
        <Button asChild variant="outline" size="sm">
          <Link to="/optimization">
            <Wand2 className="size-4" />
            Run again
          </Link>
        </Button>
      </div>

      {/* Summary */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard
          label="Resources Analyzed"
          value={result.totalResources}
          icon={Boxes}
          iconClassName="bg-sky-500/15 text-sky-400"
        />
        <StatCard
          label="Total Findings"
          value={result.totalFindings}
          icon={Lightbulb}
          iconClassName="bg-amber-500/15 text-amber-400"
        />
        <StatCard
          label="Est. Monthly Savings"
          value={hasSavings ? formatCurrency(totalSavings) : "—"}
          icon={DollarSign}
          iconClassName="bg-emerald-500/15 text-emerald-400"
          hint={hasSavings ? "Across all findings" : "No savings estimate returned"}
        />
      </div>

      {result.executiveSummary && <ExecutiveSummaryCard summary={result.executiveSummary} />}

      <div className="space-y-3">
        <h3 className="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
          Resource Recommendations
        </h3>
        {result.resources?.length ? (
          result.resources.map((r) => <ResourceCard key={r.resourceId} resource={r} />)
        ) : (
          <p className="text-sm text-muted-foreground">
            No resources were returned for this analysis.
          </p>
        )}
      </div>
    </div>
  )
}
