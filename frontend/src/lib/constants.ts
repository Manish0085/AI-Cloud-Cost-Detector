import {
  Boxes,
  Database,
  HardDrive,
  Server,
  type LucideIcon,
} from "lucide-react"
import type { ResourceType } from "@/api/types"

export interface ResourceTypeMeta {
  label: string
  description: string
  icon: LucideIcon
  // Tailwind classes for the icon chip.
  className: string
  // Recharts fill (CSS var).
  color: string
}

export const RESOURCE_TYPE_META: Record<ResourceType, ResourceTypeMeta> = {
  EC2: {
    label: "EC2",
    description: "Elastic Compute Cloud instances",
    icon: Server,
    className: "bg-violet-500/15 text-violet-400",
    color: "var(--chart-1)",
  },
  S3: {
    label: "S3",
    description: "Simple Storage Service buckets",
    icon: HardDrive,
    className: "bg-sky-500/15 text-sky-400",
    color: "var(--chart-2)",
  },
  RDS: {
    label: "RDS",
    description: "Relational Database Service",
    icon: Database,
    className: "bg-amber-500/15 text-amber-400",
    color: "var(--chart-3)",
  },
  EKS: {
    label: "EKS",
    description: "Elastic Kubernetes Service clusters",
    icon: Boxes,
    className: "bg-emerald-500/15 text-emerald-400",
    color: "var(--chart-4)",
  },
}

export const CHART_COLORS = [
  "var(--chart-1)",
  "var(--chart-2)",
  "var(--chart-3)",
  "var(--chart-4)",
  "var(--chart-5)",
]
