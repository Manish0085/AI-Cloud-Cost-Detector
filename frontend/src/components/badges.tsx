import { Badge } from "@/components/ui/badge"
import { cn } from "@/lib/utils"
import { RESOURCE_TYPE_META } from "@/lib/constants"
import type { ResourceType } from "@/api/types"

export function ResourceTypeBadge({
  type,
  className,
}: {
  type: string
  className?: string
}) {
  const meta = RESOURCE_TYPE_META[type as ResourceType]
  if (!meta) {
    return (
      <Badge variant="secondary" className={className}>
        {type}
      </Badge>
    )
  }
  const Icon = meta.icon
  return (
    <Badge variant="secondary" className={cn("gap-1.5", className)}>
      <Icon className="size-3.5" />
      {meta.label}
    </Badge>
  )
}

// Maps common AWS resource states to a colored dot + label.
export function StatusBadge({ status }: { status: string }) {
  const s = (status ?? "").toLowerCase()
  const tone =
    s.includes("run") || s.includes("avail") || s.includes("active") || s.includes("ok")
      ? "bg-emerald-500"
      : s.includes("stop") || s.includes("terminat") || s.includes("fail") || s.includes("error")
        ? "bg-rose-500"
        : s.includes("pend") || s.includes("creat") || s.includes("start")
          ? "bg-amber-500"
          : "bg-muted-foreground"
  return (
    <span className="inline-flex items-center gap-1.5 text-sm">
      <span className={cn("size-2 rounded-full", tone)} />
      <span className="capitalize">{status || "unknown"}</span>
    </span>
  )
}
