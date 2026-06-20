import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts"
import { RESOURCE_TYPE_META } from "@/lib/constants"
import type { ResourceType } from "@/api/types"

interface Props {
  distribution: Record<string, number>
}

const ORDER: ResourceType[] = ["EC2", "S3", "RDS", "EKS"]

export default function ResourceDistributionChart({ distribution }: Props) {
  const data = ORDER.map((t) => ({
    name: t,
    value: distribution[t] ?? 0,
    color: RESOURCE_TYPE_META[t].color,
  })).filter((d) => d.value > 0)

  const total = data.reduce((a, d) => a + d.value, 0)

  if (total === 0) {
    return (
      <div className="flex h-[260px] items-center justify-center text-sm text-muted-foreground">
        No resources discovered yet.
      </div>
    )
  }

  return (
    <div className="flex flex-col items-center gap-4 sm:flex-row">
      <div className="relative h-[200px] w-[200px] shrink-0">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              dataKey="value"
              nameKey="name"
              innerRadius={62}
              outerRadius={92}
              paddingAngle={2}
              strokeWidth={0}
            >
              {data.map((d) => (
                <Cell key={d.name} fill={d.color} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                background: "var(--popover)",
                border: "1px solid var(--border)",
                borderRadius: "0.5rem",
                fontSize: "0.8rem",
                color: "var(--popover-foreground)",
              }}
            />
          </PieChart>
        </ResponsiveContainer>
        <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-semibold tabular-nums">{total}</span>
          <span className="text-xs text-muted-foreground">resources</span>
        </div>
      </div>
      <ul className="grid w-full grid-cols-2 gap-3 sm:grid-cols-1">
        {data.map((d) => (
          <li key={d.name} className="flex items-center justify-between gap-3 text-sm">
            <span className="flex items-center gap-2">
              <span className="size-2.5 rounded-sm" style={{ background: d.color }} />
              {d.name}
            </span>
            <span className="font-medium tabular-nums">{d.value}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
