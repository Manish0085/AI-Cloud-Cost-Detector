import api from "./client"
import type {
  AccountOptimizationResponse,
  CloudAccount,
  ConnectCloudRequest,
  Ec2DetailsResponse,
  EksDetailsResponse,
  OptimizationReportDetails,
  OptimizationReportResponse,
  OptimizationResponse,
  Page,
  RdsDetailsResponse,
  ResourceMetricsResponse,
  ResourceResponse,
  ResourceType,
  ResourceTypeFilter,
  S3DetailsResponse,
} from "./types"

export const RESOURCE_TYPES: ResourceType[] = ["EC2", "S3", "RDS", "EKS"]

export async function connectAccount(body: ConnectCloudRequest): Promise<string> {
  const { data } = await api.post<string>("/cloud/connect", body)
  return data
}

export async function listAccounts(): Promise<CloudAccount[]> {
  const { data } = await api.get<CloudAccount[]>("/cloud/accounts")
  return data
}

export async function listResources(
  accountId: string,
  type: ResourceTypeFilter = "EC2"
): Promise<ResourceResponse[]> {
  const { data } = await api.get<ResourceResponse[]>(
    `/cloud/${accountId}/resources`,
    { params: { type } }
  )
  return data
}

// --- Type-specific resource details ---
export async function getEc2Details(
  accountId: string,
  resourceId: string
): Promise<Ec2DetailsResponse> {
  const { data } = await api.get<Ec2DetailsResponse>(
    `/cloud/${accountId}/resources/${encodeURIComponent(resourceId)}`
  )
  return data
}

export async function getS3Details(
  accountId: string,
  bucketName: string
): Promise<S3DetailsResponse> {
  const { data } = await api.get<S3DetailsResponse>(
    `/cloud/${accountId}/s3/${encodeURIComponent(bucketName)}`
  )
  return data
}

export async function getRdsDetails(
  accountId: string,
  dbIdentifier: string
): Promise<RdsDetailsResponse> {
  const { data } = await api.get<RdsDetailsResponse>(
    `/cloud/${accountId}/rds/${encodeURIComponent(dbIdentifier)}`
  )
  return data
}

export async function getEksDetails(
  accountId: string,
  clusterName: string
): Promise<EksDetailsResponse> {
  const { data } = await api.get<EksDetailsResponse>(
    `/cloud/${accountId}/eks/${encodeURIComponent(clusterName)}`
  )
  return data
}

export async function getResourceMetrics(
  accountId: string,
  resourceId: string
): Promise<ResourceMetricsResponse> {
  const { data } = await api.get<ResourceMetricsResponse>(
    `/cloud/${accountId}/resources/${encodeURIComponent(resourceId)}/metrics`
  )
  return data
}

// Single-resource optimization. Requires the resource type as a query param.
export async function optimizeResource(
  accountId: string,
  resourceId: string,
  type: ResourceType
): Promise<OptimizationResponse> {
  const { data } = await api.post<OptimizationResponse>(
    `/cloud/${accountId}/resources/${encodeURIComponent(resourceId)}/optimize`,
    null,
    { params: { type } }
  )
  console.log("Optimization Response:", data)
  return data
}

export async function optimizeAccount(
  accountId: string
): Promise<AccountOptimizationResponse> {
  const { data } = await api.post<AccountOptimizationResponse>(
    `/cloud/${accountId}/optimize`
  )
  return data
}

// The backend may return a standard Spring Page or a custom { content, pageable }
// wrapper. Normalize both into a single Page<T> shape the UI can rely on.
export async function listReports(
  accountId: string,
  page = 0,
  size = 10
): Promise<Page<OptimizationReportResponse>> {
  const { data } = await api.get(`/cloud/${accountId}/reports`, {
    params: { page, size },
  })
  return normalizePage<OptimizationReportResponse>(data, page, size)
}

export async function getReportDetails(
  reportId: string
): Promise<OptimizationReportDetails> {
  const { data } = await api.get<OptimizationReportDetails>(
    `/cloud/reports/${reportId}`
  )
  return data
}

/* eslint-disable @typescript-eslint/no-explicit-any */
function normalizePage<T>(data: any, page: number, size: number): Page<T> {
  const content: T[] = Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data)
      ? data
      : []
  const meta = data?.pageable && typeof data.pageable === "object" ? data.pageable : data ?? {}
  const number = meta.pageNumber ?? data?.number ?? page
  const sz = meta.pageSize ?? data?.size ?? size
  const totalPages = data?.totalPages ?? meta.totalPages ?? 1
  const totalElements = data?.totalElements ?? meta.totalElements ?? content.length
  return {
    content,
    number,
    size: sz,
    totalPages,
    totalElements,
    first: data?.first ?? number === 0,
    last: data?.last ?? number >= totalPages - 1,
  }
}
