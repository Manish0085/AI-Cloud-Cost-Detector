import { Link } from "react-router-dom"
import { Sparkles } from "lucide-react"

interface Props {
  title: string
  subtitle: string
  children: React.ReactNode
  footer: React.ReactNode
}

export default function AuthLayout({ title, subtitle, children, footer }: Props) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-background px-4">
      {/* Gradient + grid backdrop */}
      <div className="absolute inset-0 bg-grid [mask-image:radial-gradient(70%_60%_at_50%_40%,black,transparent)]" />
      <div className="absolute inset-0 bg-glow" />
      <div className="pointer-events-none absolute -left-32 top-1/3 size-96 rounded-full bg-violet-600/20 blur-3xl" />
      <div className="pointer-events-none absolute -right-32 bottom-1/4 size-96 rounded-full bg-indigo-600/20 blur-3xl" />

      <div className="relative w-full max-w-md">
        <Link to="/" className="mb-6 flex items-center justify-center gap-2.5">
          <div className="flex size-10 items-center justify-center rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 text-white shadow-lg shadow-violet-900/40">
            <Sparkles className="size-5" />
          </div>
          <span className="text-lg font-semibold tracking-tight">CostOptimizer</span>
        </Link>

        <div className="glass rounded-2xl border border-border/80 p-7 shadow-2xl shadow-black/40">
          <div className="mb-6 text-center">
            <h1 className="text-xl font-semibold tracking-tight">{title}</h1>
            <p className="mt-1 text-sm text-muted-foreground">{subtitle}</p>
          </div>
          {children}
        </div>

        <div className="mt-6 text-center text-sm text-muted-foreground">{footer}</div>
      </div>
    </div>
  )
}
