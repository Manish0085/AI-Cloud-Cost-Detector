import { RESOURCE_TYPES, listReports, listResources } from "./cloud"
import type { DashboardData, ResourceType } from "./types"

/**
 * Assembles the dashboard entirely from the documented endpoints:
 * `/resources?type=ALL` for the inventory + distribution, and `/reports` for
 * history and the latest findings count.
 */
export async function loadDashboard(accountId: string): Promise<DashboardData> {
  const [resources, reportsPage] = await Promise.all([
    listResources(accountId, "ALL").catch(() => []),
    listReports(accountId, 0, 5).catch(() => null),
  ])

  const resourceDistribution: Record<string, number> = Object.fromEntries(
    RESOURCE_TYPES.map((t) => [t, 0])
  )
  for (const r of resources) {
    const key = (r.resourceType ?? "").toUpperCase() as ResourceType
    if (key in resourceDistribution) resourceDistribution[key] += 1
    else resourceDistribution[key] = (resourceDistribution[key] ?? 0) + 1
  }

  const recentReports = reportsPage?.content ?? []

  return {
    totalResources: resources.length,
    // Latest report's findings count is the only documented source for this.
    totalFindings: recentReports[0]?.totalFindings ?? 0,
    resourceDistribution,
    recentReports,
  }
}
