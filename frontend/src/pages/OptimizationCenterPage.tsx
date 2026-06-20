import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Cloud, Loader2, Wand2 } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { EmptyState } from "@/components/states"
import { optimizeAccount } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { useAccountContext } from "@/context/AccountContext"
import { useOptimization } from "@/context/OptimizationContext"

export default function OptimizationCenterPage() {
  const { accounts, loading: accountsLoading, selectedAccount, selectedAccountId } =
    useAccountContext()
  const { setLastRun } = useOptimization()
  const navigate = useNavigate()
  const [running, setRunning] = useState(false)

  async function run() {
    if (!selectedAccountId || !selectedAccount) return
    setRunning(true)
    try {
      const result = await optimizeAccount(selectedAccountId)
      setLastRun({
        accountId: selectedAccountId,
        accountName: selectedAccount.accountName,
        scope: "Entire account",
        result,
        ranAt: new Date().toISOString(),
      })
      navigate("/optimization/results")
    } catch (e) {
      toast.error(apiErrorMessage(e, "Optimization failed"))
    } finally {
      setRunning(false)
    }
  }

  if (!accountsLoading && accounts.length === 0) {
    return (
      <EmptyState
        icon={Cloud}
        title="No cloud accounts connected"
        description="Connect an AWS account to run optimizations."
        action={
          <Button asChild>
            <Link to="/accounts">Connect AWS account</Link>
          </Button>
        }
      />
    )
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div className="text-center">
        <div className="mx-auto mb-4 flex size-14 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 text-white shadow-lg shadow-violet-900/30">
          <Wand2 className="size-7" />
        </div>
        <h2 className="text-2xl font-semibold tracking-tight">Optimization Center</h2>
        <p className="mt-2 text-muted-foreground">
          Run an AI-powered analysis across your entire AWS account to surface findings,
          recommendations and estimated monthly savings.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Run analysis</CardTitle>
          <CardDescription>
            Account: <span className="text-foreground">{selectedAccount?.accountName ?? "—"}</span>
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <Button
            onClick={run}
            disabled={running || !selectedAccountId}
            size="lg"
            className="w-full"
          >
            {running ? <Loader2 className="size-4 animate-spin" /> : <Wand2 className="size-4" />}
            Optimize Entire Account
          </Button>
          {running && (
            <p className="text-center text-sm text-muted-foreground">
              Analyzing resources and generating AI recommendations… this can take a moment.
            </p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
