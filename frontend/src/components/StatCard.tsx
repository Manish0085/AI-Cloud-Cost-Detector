import { type LucideIcon } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { cn } from "@/lib/utils"

interface Props {
  label: string
  value: string | number
  icon: LucideIcon
  iconClassName?: string
  hint?: string
  loading?: boolean
}

export default function StatCard({
  label,
  value,
  icon: Icon,
  iconClassName,
  hint,
  loading,
}: Props) {
  return (
    <Card className="gap-0 p-5">
      <div className="flex items-center justify-between">
        <p className="text-sm font-medium text-muted-foreground">{label}</p>
        <div
          className={cn(
            "flex size-9 items-center justify-center rounded-lg bg-secondary text-foreground",
            iconClassName
          )}
        >
          <Icon className="size-4.5" />
        </div>
      </div>
      {loading ? (
        <Skeleton className="mt-3 h-9 w-24" />
      ) : (
        <p className="mt-2 text-3xl font-semibold tracking-tight tabular-nums">{value}</p>
      )}
      {hint && <p className="mt-1 text-xs text-muted-foreground">{hint}</p>}
    </Card>
  )
}
