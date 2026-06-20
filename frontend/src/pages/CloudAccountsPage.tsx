import { useState, type FormEvent } from "react"
import { CheckCircle2, Cloud, KeyRound, Loader2, Plus } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import { Skeleton } from "@/components/ui/skeleton"
import { EmptyState, ErrorState } from "@/components/states"
import { connectAccount } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import { useAccountContext } from "@/context/AccountContext"

export default function CloudAccountsPage() {
  const { accounts, loading, error, refresh } = useAccountContext()
  const [accountName, setAccountName] = useState("")
  const [accessKey, setAccessKey] = useState("")
  const [secretKey, setSecretKey] = useState("")
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    try {
      await connectAccount({ accountName, provider: "AWS", accessKey, secretKey })
      toast.success("Cloud account connected")
      setAccountName("")
      setAccessKey("")
      setSecretKey("")
      await refresh()
    } catch (err) {
      toast.error(apiErrorMessage(err, "Failed to connect account"))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-5">
      {/* Connect form */}
      <div className="lg:col-span-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <Plus className="size-4 text-primary" />
              Connect AWS Account
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={onSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="accountName">Account Name</Label>
                <Input
                  id="accountName"
                  placeholder="Production AWS"
                  value={accountName}
                  onChange={(e) => setAccountName(e.target.value)}
                  required
                  minLength={3}
                  maxLength={50}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="accessKey">Access Key</Label>
                <Input
                  id="accessKey"
                  placeholder="AKIA…"
                  value={accessKey}
                  onChange={(e) => setAccessKey(e.target.value)}
                  required
                  autoComplete="off"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="secretKey">Secret Key</Label>
                <Input
                  id="secretKey"
                  type="password"
                  placeholder="••••••••••••"
                  value={secretKey}
                  onChange={(e) => setSecretKey(e.target.value)}
                  required
                  autoComplete="off"
                />
              </div>
              <div className="flex items-start gap-2 rounded-lg border border-border bg-secondary/40 p-3 text-xs text-muted-foreground">
                <KeyRound className="mt-0.5 size-3.5 shrink-0" />
                Credentials are validated against AWS and stored securely by the backend.
              </div>
              <Button type="submit" className="w-full" disabled={submitting}>
                {submitting && <Loader2 className="size-4 animate-spin" />}
                Connect Account
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>

      {/* Connected list */}
      <div className="lg:col-span-3">
        <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
          Connected Accounts {accounts.length > 0 && `(${accounts.length})`}
        </h2>
        {loading ? (
          <div className="space-y-3">
            <Skeleton className="h-[72px] w-full" />
            <Skeleton className="h-[72px] w-full" />
          </div>
        ) : error ? (
          <ErrorState description={error} onRetry={refresh} />
        ) : accounts.length === 0 ? (
          <EmptyState
            icon={Cloud}
            title="No accounts connected yet"
            description="Add your first AWS account using the form to start discovering resources."
          />
        ) : (
          <div className="space-y-3">
            {accounts.map((acc) => (
              <Card key={acc.id} className="flex flex-row items-center gap-4 p-4">
                <div className="flex size-11 items-center justify-center rounded-xl bg-secondary">
                  <Cloud className="size-5 text-muted-foreground" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium">{acc.accountName}</p>
                  <p className="truncate text-xs text-muted-foreground">{acc.id}</p>
                </div>
                <Badge variant="secondary">{acc.provider}</Badge>
                {acc.connected !== false && (
                  <span className="flex items-center gap-1 text-xs text-emerald-500">
                    <CheckCircle2 className="size-4" />
                    <span className="hidden sm:inline">Connected</span>
                  </span>
                )}
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
