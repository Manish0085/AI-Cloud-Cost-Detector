import { useCallback, useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { Boxes, Cloud, Eye, Loader2, RefreshCw, Sparkles } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { EmptyState, ErrorState } from "@/components/states"
import { ResourceTypeBadge, StatusBadge } from "@/components/badges"
import AiRecommendation from "@/components/AiRecommendation"
import { RESOURCE_TYPES, listResources, optimizeResource } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { useAccountContext } from "@/context/AccountContext"
import type { OptimizationResponse, ResourceResponse, ResourceType } from "@/api/types"

export default function ResourceExplorerPage() {
  const { accounts, loading: accountsLoading, selectedAccountId } = useAccountContext()

  const [type, setType] = useState<ResourceType>("EC2")
  const [resources, setResources] = useState<ResourceResponse[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [optimizingRow, setOptimizingRow] = useState<string | null>(null)
  const [single, setSingle] = useState<OptimizationResponse | null>(null)

  const load = useCallback(async () => {
    if (!selectedAccountId) return
    setLoading(true)
    setError(null)
    try {
      setResources(await listResources(selectedAccountId, type))
    } catch (e) {
      setError(apiErrorMessage(e, "Failed to load resources"))
    } finally {
      setLoading(false)
    }
  }, [selectedAccountId, type])

  useEffect(() => {
    void load()
  }, [load])

  async function handleOptimizeRow(r: ResourceResponse) {
    if (!selectedAccountId) return
    setOptimizingRow(r.resourceId)
    try {
      setSingle(await optimizeResource(selectedAccountId, r.resourceId, type))
    } catch (e) {
      toast.error(apiErrorMessage(e, "Optimization failed"))
    } finally {
      setOptimizingRow(null)
    }
  }

  if (!accountsLoading && accounts.length === 0) {
    return (
      <EmptyState
        icon={Cloud}
        title="No cloud accounts connected"
        description="Connect an AWS account to explore its resources."
        action={
          <Button asChild>
            <Link to="/accounts">Connect AWS account</Link>
          </Button>
        }
      />
    )
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <Tabs value={type} onValueChange={(v) => setType(v as ResourceType)}>
          <TabsList>
            {RESOURCE_TYPES.map((t) => (
              <TabsTrigger key={t} value={t}>
                {t}
              </TabsTrigger>
            ))}
          </TabsList>
        </Tabs>
        <Button variant="outline" size="sm" onClick={load} disabled={loading}>
          <RefreshCw className={loading ? "size-4 animate-spin" : "size-4"} />
          Refresh
        </Button>
      </div>

      {loading ? (
        <div className="space-y-2">
          <Skeleton className="h-12 w-full" />
          <Skeleton className="h-12 w-full" />
          <Skeleton className="h-12 w-full" />
        </div>
      ) : error ? (
        <ErrorState description={error} onRetry={load} />
      ) : resources.length === 0 ? (
        <EmptyState
          icon={Boxes}
          title={`No ${type} resources found`}
          description="Try a different resource type, or verify this account has resources in the analyzed region."
        />
      ) : (
        <Card className="overflow-hidden py-0">
          <Table>
            <TableHeader>
              <TableRow className="hover:bg-transparent">
                <TableHead>Name</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Region</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {resources.map((r) => (
                <TableRow key={r.resourceId}>
                  <TableCell>
                    <div className="font-medium">{r.resourceName || r.resourceId}</div>
                    <div className="text-xs text-muted-foreground">{r.resourceId}</div>
                  </TableCell>
                  <TableCell>
                    <ResourceTypeBadge type={r.resourceType} />
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground">
                    {r.region || "—"}
                  </TableCell>
                  <TableCell>
                    <StatusBadge status={r.status} />
                  </TableCell>
                  <TableCell>
                    <div className="flex justify-end gap-2">
                      <Button asChild variant="ghost" size="sm">
                        <Link
                          to={`/explorer/${selectedAccountId}/${type}/${encodeURIComponent(r.resourceId)}`}
                        >
                          <Eye className="size-4" />
                          <span className="hidden sm:inline">Details</span>
                        </Link>
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleOptimizeRow(r)}
                        disabled={optimizingRow === r.resourceId}
                      >
                        {optimizingRow === r.resourceId ? (
                          <Loader2 className="size-4 animate-spin" />
                        ) : (
                          <Sparkles className="size-4" />
                        )}
                        <span className="hidden sm:inline">Optimize</span>
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Card>
      )}

      {/* Single-resource optimization result */}
      <Dialog open={!!single} onOpenChange={(o) => !o && setSingle(null)}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Optimization Recommendation</DialogTitle>
            <DialogDescription>
              {single?.resourceId}
              {single?.currentInstanceType ? ` · ${single.currentInstanceType}` : ""}
            </DialogDescription>
          </DialogHeader>
          {single && (
            <AiRecommendation
              text={single.aiRecommendation || "No recommendation available"}
            />
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
