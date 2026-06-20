import { useEffect, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { ArrowLeft, Check, Loader2, Sparkles, X } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { StatusBadge } from "@/components/badges"
import { ErrorState } from "@/components/states"
import MetricCharts from "@/components/charts/MetricCharts"
import AiRecommendation from "@/components/AiRecommendation"
import {
  getEc2Details,
  getEksDetails,
  getRdsDetails,
  getResourceMetrics,
  getS3Details,
  optimizeResource,
} from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { formatBytes, formatNumber } from "@/lib/utils"
import type {
  Ec2DetailsResponse,
  EksDetailsResponse,
  RdsDetailsResponse,
  ResourceMetricsResponse,
  ResourceType,
  S3DetailsResponse,
} from "@/api/types"

type Details =
  | { type: "EC2"; data: Ec2DetailsResponse }
  | { type: "S3"; data: S3DetailsResponse }
  | { type: "RDS"; data: RdsDetailsResponse }
  | { type: "EKS"; data: EksDetailsResponse }

function InfoRow({ label, value }: { label: string; value?: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-border py-2.5 last:border-0">
      <span className="text-sm text-muted-foreground">{label}</span>
      <span className="text-right text-sm font-medium">
        {value === undefined || value === null || value === "" ? "—" : value}
      </span>
    </div>
  )
}

function BoolPill({ value }: { value: boolean }) {
  return (
    <span
      className={
        "inline-flex items-center gap-1 text-sm " +
        (value ? "text-emerald-500" : "text-muted-foreground")
      }
    >
      {value ? <Check className="size-4" /> : <X className="size-4" />}
      {value ? "Enabled" : "Disabled"}
    </span>
  )
}

async function fetchDetails(
  accountId: string,
  type: ResourceType,
  resourceId: string
): Promise<Details> {
  switch (type) {
    case "S3":
      return { type, data: await getS3Details(accountId, resourceId) }
    case "RDS":
      return { type, data: await getRdsDetails(accountId, resourceId) }
    case "EKS":
      return { type, data: await getEksDetails(accountId, resourceId) }
    case "EC2":
    default:
      return { type: "EC2", data: await getEc2Details(accountId, resourceId) }
  }
}

export default function ResourceDetailsPage() {
  const { accountId, type, resourceId } = useParams<{
    accountId: string
    type: string
    resourceId: string
  }>()
  const rType = (type?.toUpperCase() as ResourceType) ?? "EC2"

  const [details, setDetails] = useState<Details | null>(null)
  const [metrics, setMetrics] = useState<ResourceMetricsResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [optimizing, setOptimizing] = useState(false)
  const [recommendation, setRecommendation] = useState<string | null>(null)

  useEffect(() => {
    if (!accountId || !resourceId) return
    let active = true
    setLoading(true)
    setError(null)
    setDetails(null)
    setMetrics(null)
    setRecommendation(null)

    fetchDetails(accountId, rType, resourceId)
      .then((d) => active && setDetails(d))
      .catch((e) => active && setError(apiErrorMessage(e, "Failed to load resource")))
      .finally(() => active && setLoading(false))

    // CloudWatch metrics are only meaningful for EC2 instances.
    if (rType === "EC2") {
      getResourceMetrics(accountId, resourceId)
        .then((m) => active && setMetrics(m))
        .catch(() => {})
    }

    return () => {
      active = false
    }
  }, [accountId, resourceId, rType])

  async function runOptimize() {
    if (!accountId || !resourceId) return
    setOptimizing(true)
    try {
      const res = await optimizeResource(accountId, resourceId, rType)
      setRecommendation(res.aiRecommendation || "No recommendation available")
    } catch (e) {
      toast.error(apiErrorMessage(e, "Optimization failed"))
    } finally {
      setOptimizing(false)
    }
  }

  const heading =
    details?.type === "EC2"
      ? details.data.instanceName || details.data.instanceId
      : details?.type === "S3"
        ? details.data.bucketName
        : details?.type === "RDS"
          ? details.data.dbIdentifier
          : details?.type === "EKS"
            ? details.data.clusterName
            : resourceId

  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/explorer">
          <ArrowLeft className="size-4" />
          Back to Resource Explorer
        </Link>
      </Button>

      {loading ? (
        <div className="space-y-4">
          <Skeleton className="h-40 w-full" />
          <Skeleton className="h-44 w-full" />
        </div>
      ) : error ? (
        <ErrorState description={error} />
      ) : details ? (
        <>
          <div className="flex flex-wrap items-end justify-between gap-3">
            <div>
              <h2 className="text-xl font-semibold tracking-tight">{heading}</h2>
              <p className="text-sm text-muted-foreground">
                {rType} · {resourceId}
              </p>
            </div>
            <Button onClick={runOptimize} disabled={optimizing}>
              {optimizing ? <Loader2 className="size-4 animate-spin" /> : <Sparkles className="size-4" />}
              Run AI Optimization
            </Button>
          </div>

          {/* Type-specific resource information */}
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Resource Information</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-x-8 sm:grid-cols-2">
              {details.type === "EC2" && (
                <>
                  <div>
                    <InfoRow label="Instance ID" value={details.data.instanceId} />
                    <InfoRow label="Instance Type" value={details.data.instanceType} />
                    <InfoRow label="State" value={<StatusBadge status={details.data.state} />} />
                    <InfoRow label="Availability Zone" value={details.data.availabilityZone} />
                  </div>
                  <div>
                    <InfoRow label="Public IP" value={details.data.publicIp} />
                    <InfoRow label="Private IP" value={details.data.privateIp} />
                    <InfoRow label="VPC ID" value={details.data.vpcId} />
                    <InfoRow label="Subnet ID" value={details.data.subnetId} />
                  </div>
                </>
              )}

              {details.type === "S3" && (
                <>
                  <div>
                    <InfoRow label="Bucket Name" value={details.data.bucketName} />
                    <InfoRow label="Region" value={details.data.region} />
                    <InfoRow label="Versioning" value={<BoolPill value={details.data.versioningEnabled} />} />
                  </div>
                  <div>
                    <InfoRow label="Public Access Blocked" value={<BoolPill value={details.data.publicAccessBlocked} />} />
                    <InfoRow label="Object Count" value={formatNumber(details.data.objectCount)} />
                    <InfoRow label="Bucket Size" value={formatBytes(details.data.bucketSizeBytes)} />
                  </div>
                </>
              )}

              {details.type === "RDS" && (
                <>
                  <div>
                    <InfoRow label="DB Identifier" value={details.data.dbIdentifier} />
                    <InfoRow label="Engine" value={details.data.engine} />
                    <InfoRow label="Engine Version" value={details.data.engineVersion} />
                    <InfoRow label="Status" value={<StatusBadge status={details.data.status} />} />
                  </div>
                  <div>
                    <InfoRow label="Instance Class" value={details.data.instanceClass} />
                    <InfoRow label="Allocated Storage" value={`${details.data.allocatedStorage} GB`} />
                    <InfoRow label="Availability Zone" value={details.data.availabilityZone} />
                  </div>
                </>
              )}

              {details.type === "EKS" && (
                <>
                  <div>
                    <InfoRow label="Cluster Name" value={details.data.clusterName} />
                    <InfoRow label="Version" value={details.data.version} />
                    <InfoRow label="Status" value={<StatusBadge status={details.data.status} />} />
                  </div>
                  <div>
                    <InfoRow label="Node Count" value={formatNumber(details.data.nodeCount)} />
                    <InfoRow label="Endpoint" value={<span className="break-all">{details.data.endpoint}</span>} />
                  </div>
                </>
              )}
            </CardContent>
          </Card>

          {/* Metrics (EC2 only) */}
          {rType === "EC2" && (
            <div>
              <div className="mb-3 flex items-center justify-between">
                <h3 className="text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                  CloudWatch Metrics
                </h3>
                <span className="text-xs text-muted-foreground">Current snapshot</span>
              </div>
              {metrics ? (
                <MetricCharts metrics={metrics} />
              ) : (
                <Card>
                  <CardContent className="py-8 text-center text-sm text-muted-foreground">
                    Metrics are not available for this resource.
                  </CardContent>
                </Card>
              )}
            </div>
          )}

          {/* AI recommendation */}
          {recommendation && (
            <div>
              <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                Optimization Result
              </h3>
              <AiRecommendation text={recommendation} />
            </div>
          )}
        </>
      ) : null}
    </div>
  )
}
