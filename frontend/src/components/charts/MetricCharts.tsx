import {
  Bar,
  BarChart,
  Cell,
  PolarAngleAxis,
  RadialBar,
  RadialBarChart,
  ResponsiveContainer,
  XAxis,
  YAxis,
} from "recharts"
import { Card } from "@/components/ui/card"
import { formatBytes } from "@/lib/utils"
import type { ResourceMetricsResponse } from "@/api/types"

function cpuTone(v: number) {
  if (v >= 80) return "var(--chart-5)" // hot — red-ish
  if (v >= 40) return "var(--chart-3)" // moderate — amber
  return "var(--chart-4)" // healthy — green
}

function CpuGauge({ value }: { value: number }) {
  const v = Math.max(0, Math.min(100, value ?? 0))
  return (
    <div className="relative h-[180px] w-full">
      <ResponsiveContainer width="100%" height="100%">
        <RadialBarChart
          innerRadius="72%"
          outerRadius="100%"
          data={[{ value: v, fill: cpuTone(v) }]}
          startAngle={220}
          endAngle={-40}
        >
          <PolarAngleAxis type="number" domain={[0, 100]} tick={false} />
          <RadialBar background={{ fill: "var(--secondary)" }} dataKey="value" cornerRadius={999} />
        </RadialBarChart>
      </ResponsiveContainer>
      <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-3xl font-semibold tabular-nums">{v.toFixed(1)}%</span>
        <span className="text-xs text-muted-foreground">CPU utilization</span>
      </div>
    </div>
  )
}

function BytesBars({
  title,
  data,
}: {
  title: string
  data: { name: string; value: number; color: string }[]
}) {
  return (
    <div className="h-[180px] w-full">
      <p className="mb-1 text-xs font-medium text-muted-foreground">{title}</p>
      <ResponsiveContainer width="100%" height="90%">
        <BarChart data={data} layout="vertical" margin={{ left: 8, right: 16 }}>
          <XAxis type="number" hide />
          <YAxis
            type="category"
            dataKey="name"
            tickLine={false}
            axisLine={false}
            width={64}
            tick={{ fill: "var(--muted-foreground)", fontSize: 12 }}
          />
          <Bar dataKey="value" radius={[0, 6, 6, 0]} maxBarSize={28} label={{
            position: "right",
            formatter: (v) => formatBytes(Number(v) || 0),
            fill: "var(--foreground)",
            fontSize: 11,
          }}>
            {data.map((d, i) => (
              <Cell key={i} fill={d.color} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export default function MetricCharts({ metrics }: { metrics: ResourceMetricsResponse }) {
  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <Card className="p-5">
        <CpuGauge value={metrics.cpuUtilization} />
      </Card>
      <Card className="p-5">
        <BytesBars
          title="Network usage"
          data={[
            { name: "In", value: metrics.networkIn ?? 0, color: "var(--chart-2)" },
            { name: "Out", value: metrics.networkOut ?? 0, color: "var(--chart-1)" },
          ]}
        />
      </Card>
      <Card className="p-5">
        <BytesBars
          title="Disk activity"
          data={[
            { name: "Read", value: metrics.diskReadBytes ?? 0, color: "var(--chart-4)" },
            { name: "Write", value: metrics.diskWriteBytes ?? 0, color: "var(--chart-3)" },
          ]}
        />
      </Card>
    </div>
  )
}
