import { NavLink } from "react-router-dom"
import {
  Cloud,
  Compass,
  FileText,
  LayoutDashboard,
  Settings,
  Sparkles,
  Wand2,
} from "lucide-react"
import { cn } from "@/lib/utils"

export const NAV_ITEMS = [
  { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { to: "/accounts", label: "Cloud Accounts", icon: Cloud },
  { to: "/explorer", label: "Resource Explorer", icon: Compass },
  { to: "/optimization", label: "Optimization Center", icon: Wand2 },
  { to: "/reports", label: "Reports", icon: FileText },
  { to: "/settings", label: "Settings", icon: Settings },
]

interface Props {
  // When true, render labels always (mobile drawer / wide). When false, the
  // desktop rail hides labels until the md breakpoint via responsive classes.
  expanded?: boolean
  onNavigate?: () => void
}

export default function SidebarContent({ expanded = false, onNavigate }: Props) {
  return (
    <div className="flex h-full flex-col bg-sidebar">
      {/* Brand */}
      <div className="flex h-16 items-center gap-2.5 px-4">
        <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 text-white shadow-sm shadow-violet-900/40">
          <Sparkles className="size-5" />
        </div>
        <div className={cn(expanded ? "block" : "hidden md:block")}>
          <p className="text-sm font-semibold leading-tight tracking-tight">CostOptimizer</p>
          <p className="text-[11px] leading-tight text-muted-foreground">AI for AWS spend</p>
        </div>
      </div>

      <nav className="flex-1 space-y-1 overflow-y-auto px-3 py-3">
        <p
          className={cn(
            "px-3 pb-1 pt-2 text-[11px] font-medium uppercase tracking-wider text-muted-foreground",
            expanded ? "block" : "hidden md:block"
          )}
        >
          Platform
        </p>
        {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            onClick={onNavigate}
            className={({ isActive }) =>
              cn(
                "group flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                expanded ? "justify-start" : "justify-center md:justify-start",
                isActive
                  ? "bg-accent text-foreground"
                  : "text-muted-foreground hover:bg-accent/60 hover:text-foreground"
              )
            }
          >
            {({ isActive }) => (
              <>
                <Icon
                  className={cn(
                    "size-[18px] shrink-0",
                    isActive ? "text-primary" : "text-muted-foreground group-hover:text-foreground"
                  )}
                />
                <span className={cn(expanded ? "block" : "hidden md:block")}>{label}</span>
              </>
            )}
          </NavLink>
        ))}
      </nav>

      <div
        className={cn(
          "m-3 rounded-xl border border-border bg-card/50 p-3",
          expanded ? "block" : "hidden md:block"
        )}
      >
        <p className="text-xs font-medium">Free plan</p>
        <p className="mt-0.5 text-[11px] text-muted-foreground">
          Upgrade for forecasting & alerts.
        </p>
      </div>
    </div>
  )
}
