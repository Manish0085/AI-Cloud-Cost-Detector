import { Server } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import AiRecommendation from "./AiRecommendation"
import { formatCurrency } from "@/lib/utils"
import type { ResourceOptimizationResult } from "@/api/types"

interface Props {
  resource: ResourceOptimizationResult
}

export default function ResourceCard({ resource }: Props) {
  const findingsCount = resource.findings?.length ?? 0

  return (
    <Card className="overflow-hidden py-0">
      <Accordion type="single" collapsible>
        <AccordionItem value="item" className="border-b-0">
          <AccordionTrigger className="px-5 py-4 hover:no-underline">
            <div className="flex flex-1 items-center gap-3 text-left">
              <div className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-secondary">
                <Server className="size-4 text-muted-foreground" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium">
                  {resource.resourceName || resource.resourceId}
                </p>
                <p className="truncate text-xs text-muted-foreground">
                  {resource.resourceId}
                </p>
              </div>
              <Badge variant="secondary" className="hidden sm:inline-flex">
                {resource.resourceType}
              </Badge>
              <Badge variant="outline" className="mr-2 tabular-nums">
                {findingsCount} {findingsCount === 1 ? "finding" : "findings"}
              </Badge>
            </div>
          </AccordionTrigger>
          <AccordionContent className="px-5 pb-5">
            <div className="space-y-4">
              {findingsCount > 0 && (
                <div className="space-y-2">
                  <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                    Findings
                  </p>
                  <div className="space-y-2">
                    {resource.findings.map((f, i) => (
                      <div
                        key={i}
                        className="rounded-lg border border-border bg-secondary/30 p-3"
                      >
                        <div className="flex items-start justify-between gap-3">
                          <p className="text-sm font-medium">{f.recommendation}</p>
                          {typeof f.estimatedMonthlySavings === "number" &&
                            f.estimatedMonthlySavings > 0 && (
                              <Badge className="shrink-0 bg-emerald-500/15 text-emerald-400">
                                {formatCurrency(f.estimatedMonthlySavings)}/mo
                              </Badge>
                            )}
                        </div>
                        {f.reason && (
                          <p className="mt-1 text-sm text-muted-foreground">{f.reason}</p>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {resource.aiRecommendation && (
                <AiRecommendation text={resource.aiRecommendation} />
              )}
            </div>
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    </Card>
  )
}
