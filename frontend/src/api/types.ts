// API contract types — mirror the Spring Boot backend DTOs (records).

export type CloudProvider = "AWS" | "AZURE" | "GCP"
export type Role = "USER" | "ADMIN"
export type ResourceType = "EC2" | "S3" | "RDS" | "EKS"
export type ResourceTypeFilter = ResourceType | "ALL"

// --- Auth ---
export interface AuthResponse {
  token: string
  message: string
}

export interface CurrentUser {
  name: string
  email: string
  role: Role
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
}

// --- Cloud accounts ---
export interface ConnectCloudRequest {
  accountName: string
  provider: CloudProvider
  accessKey: string
  secretKey: string
}

export interface CloudAccount {
  id: string
  accountName: string
  provider: CloudProvider
  connected?: boolean // no longer returned by the backend; kept optional
}

// --- Resource discovery ---
export interface ResourceResponse {
  resourceId: string
  resourceName: string
  resourceType: string
  region: string
  status: string
}

export interface Ec2DetailsResponse {
  instanceId: string
  instanceName: string
  instanceType: string
  state: string
  publicIp: string
  privateIp: string
  vpcId: string
  subnetId: string
  availabilityZone: string
}

export interface S3DetailsResponse {
  bucketName: string
  region: string
  versioningEnabled: boolean
  publicAccessBlocked: boolean
  objectCount: number
  bucketSizeBytes: number
}

export interface RdsDetailsResponse {
  dbIdentifier: string
  engine: string
  engineVersion: string
  instanceClass: string
  status: string
  allocatedStorage: number
  availabilityZone: string
}

export interface EksDetailsResponse {
  clusterName: string
  version: string
  status: string
  endpoint: string
  nodeCount: number
}

// Single CloudWatch snapshot (point-in-time, not a time series).
export interface ResourceMetricsResponse {
  cpuUtilization: number
  networkIn: number
  networkOut: number
  diskReadBytes: number
  diskWriteBytes: number
}

// --- Optimization ---
export interface ResourceFinding {
  resourceId: string
  resourceType: string
  recommendation: string
  reason: string
  estimatedMonthlySavings?: number
}

export interface ResourceOptimizationResult {
  resourceId: string
  resourceName: string
  resourceType: string
  findings: ResourceFinding[]
  aiRecommendation: string
}

export interface AccountOptimizationResponse {
  totalResources: number
  totalFindings: number
  resources: ResourceOptimizationResult[]
  executiveSummary: string
}

// Single-resource optimization result (matches the live backend response).
export interface OptimizationResponse {
  resourceId: string
  currentInstanceType: string
  aiRecommendation: string
}

// --- Reports ---
export interface OptimizationReportResponse {
  id: string
  totalResources: number
  totalFindings: number
  createdAt: string // ISO 8601
}

export interface OptimizationReportDetails {
  id: string
  totalResources: number
  totalFindings: number
  executiveSummary: string
  createdAt: string // ISO 8601
}

// --- Dashboard (assembled client-side from listed endpoints) ---
export interface DashboardData {
  totalResources: number
  totalFindings: number
  resourceDistribution: Record<string, number>
  recentReports: OptimizationReportResponse[]
}

// Spring Data Page wrapper (subset of fields we rely on).
export interface Page<T> {
  content: T[]
  number: number
  size: number
  totalPages: number
  totalElements: number
  first: boolean
  last: boolean
}
