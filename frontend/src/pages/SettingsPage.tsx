import { Link } from "react-router-dom"
import { Cloud, LogOut, Mail, Shield, User } from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { useAuth } from "@/context/AuthContext"
import { useAccountContext } from "@/context/AccountContext"

function Field({
  icon: Icon,
  label,
  value,
}: {
  icon: typeof User
  label: string
  value?: string
}) {
  return (
    <div className="flex items-center gap-3 py-3">
      <div className="flex size-9 items-center justify-center rounded-lg bg-secondary text-muted-foreground">
        <Icon className="size-4" />
      </div>
      <div>
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className="text-sm font-medium">{value || "—"}</p>
      </div>
    </div>
  )
}

export default function SettingsPage() {
  const { user, logout } = useAuth()
  const { accounts } = useAccountContext()

  const initials = (user?.name ?? user?.email ?? "U")
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("")
    .toUpperCase()

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      {/* Profile */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Profile</CardTitle>
          <CardDescription>Your account information.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="mb-2 flex items-center gap-4">
            <Avatar className="size-14 border border-border">
              <AvatarFallback className="bg-secondary text-base font-semibold">
                {initials}
              </AvatarFallback>
            </Avatar>
            <div>
              <p className="font-medium">{user?.name}</p>
              <p className="text-sm text-muted-foreground">{user?.email}</p>
            </div>
            {user?.role && (
              <Badge variant="secondary" className="ml-auto">
                {user.role}
              </Badge>
            )}
          </div>
          <Separator />
          <Field icon={User} label="Name" value={user?.name} />
          <Field icon={Mail} label="Email" value={user?.email} />
          <Field icon={Shield} label="Role" value={user?.role} />
        </CardContent>
      </Card>

      {/* Cloud accounts */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Cloud Accounts</CardTitle>
          <CardDescription>
            {accounts.length} account{accounts.length === 1 ? "" : "s"} connected.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex items-center justify-between">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Cloud className="size-4" />
            Manage your connected AWS accounts.
          </div>
          <Button asChild variant="outline" size="sm">
            <Link to="/accounts">Manage</Link>
          </Button>
        </CardContent>
      </Card>

      {/* Session */}
      <Card className="border-destructive/30">
        <CardHeader>
          <CardTitle className="text-base">Session</CardTitle>
          <CardDescription>Sign out of your account on this device.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button variant="destructive" onClick={logout}>
            <LogOut className="size-4" />
            Log out
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
