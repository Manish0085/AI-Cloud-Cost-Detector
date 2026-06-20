import { Link } from "react-router-dom"
import {
  ArrowRight,
  BarChart3,
  Bell,
  Boxes,
  Check,
  Cloud,
  Database,
  FileText,
  Gauge,
  Globe,
  HardDrive,
  LineChart,
  Plug,
  Search,
  Server,
  Sparkles,
  TrendingUp,
  Wand2,
  Zap,
  type LucideIcon,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"

const FEATURES: { icon: LucideIcon; title: string; desc: string }[] = [
  { icon: Search, title: "AWS Resource Discovery", desc: "Automatically inventory EC2, S3, RDS and EKS across your connected accounts." },
  { icon: Gauge, title: "CloudWatch Metrics Analysis", desc: "Pull CPU, network and disk metrics to spot idle and over-provisioned resources." },
  { icon: Sparkles, title: "AI Recommendations", desc: "Context-aware rightsizing and cleanup guidance generated for every resource." },
  { icon: Database, title: "RAG Knowledge Engine", desc: "Grounded in AWS best practices via Spring AI + PGVector retrieval." },
  { icon: FileText, title: "Optimization Reports", desc: "Shareable, timestamped reports with executive summaries and findings." },
  { icon: Boxes, title: "Multi-Resource Support", desc: "One workflow across compute, storage, database and Kubernetes." },
]

const STEPS = [
  { icon: Plug, title: "Connect AWS Account", desc: "Securely link your account with scoped credentials." },
  { icon: Search, title: "Discover Resources", desc: "We enumerate your live AWS inventory." },
  { icon: Gauge, title: "Analyze Metrics", desc: "CloudWatch signals reveal usage patterns." },
  { icon: Wand2, title: "Generate Recommendations", desc: "AI proposes concrete optimizations." },
  { icon: TrendingUp, title: "Optimize Costs", desc: "Act on findings and track savings." },
]

const SUPPORTED: { icon: LucideIcon; name: string; desc: string }[] = [
  { icon: Server, name: "EC2", desc: "Compute instances" },
  { icon: HardDrive, name: "S3", desc: "Object storage" },
  { icon: Database, name: "RDS", desc: "Managed databases" },
  { icon: Boxes, name: "EKS", desc: "Kubernetes clusters" },
]

const ROADMAP: { icon: LucideIcon; name: string }[] = [
  { icon: Zap, name: "Lambda Optimization" },
  { icon: Globe, name: "CloudFront Optimization" },
  { icon: LineChart, name: "Cost Forecasting" },
  { icon: Bell, name: "Budget Alerts" },
  { icon: Cloud, name: "Multi-Cloud Support" },
]

const PRICING = [
  {
    name: "Free",
    price: "$0",
    period: "/mo",
    desc: "For individuals exploring cloud cost optimization.",
    features: ["1 AWS account", "Resource discovery", "Manual optimization runs", "7-day report history"],
    cta: "Get Started",
    highlight: false,
  },
  {
    name: "Pro",
    price: "$49",
    period: "/mo",
    desc: "For teams actively reducing AWS spend.",
    features: ["Up to 10 accounts", "AI recommendations", "Unlimited reports", "RAG best-practice engine", "Export reports"],
    cta: "Start Pro trial",
    highlight: true,
  },
  {
    name: "Enterprise",
    price: "Custom",
    period: "",
    desc: "For organizations with advanced governance needs.",
    features: ["Unlimited accounts", "Cost forecasting", "Budget alerts", "SSO & audit logs", "Dedicated support"],
    cta: "Contact sales",
    highlight: false,
  },
]

function SectionHeading({ eyebrow, title, sub }: { eyebrow: string; title: string; sub?: string }) {
  return (
    <div className="mx-auto max-w-2xl text-center">
      <p className="text-xs font-semibold uppercase tracking-widest text-primary">{eyebrow}</p>
      <h2 className="mt-3 text-3xl font-semibold tracking-tight sm:text-4xl">{title}</h2>
      {sub && <p className="mt-3 text-muted-foreground">{sub}</p>}
    </div>
  )
}

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-background">
      {/* Nav */}
      <header className="sticky top-0 z-40 border-b border-border/60 bg-background/80 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 md:px-6">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="flex size-8 items-center justify-center rounded-lg bg-gradient-to-br from-indigo-500 to-violet-600 text-white">
              <Sparkles className="size-4.5" />
            </div>
            <span className="font-semibold tracking-tight">CostOptimizer</span>
          </Link>
          <nav className="hidden items-center gap-8 text-sm text-muted-foreground md:flex">
            <a href="#features" className="transition-colors hover:text-foreground">Features</a>
            <a href="#how" className="transition-colors hover:text-foreground">How it works</a>
            <a href="#pricing" className="transition-colors hover:text-foreground">Pricing</a>
          </nav>
          <div className="flex items-center gap-2">
            <Button asChild variant="ghost" size="sm">
              <Link to="/login">Login</Link>
            </Button>
            <Button asChild size="sm">
              <Link to="/register">Get Started</Link>
            </Button>
          </div>
        </div>
      </header>

      {/* Hero */}
      <section className="relative overflow-hidden border-b border-border">
        <div className="absolute inset-0 bg-grid [mask-image:radial-gradient(60%_60%_at_50%_30%,black,transparent)]" />
        <div className="absolute inset-0 bg-glow" />
        <div className="relative mx-auto max-w-6xl px-4 pb-20 pt-16 md:px-6 md:pt-24">
          <div className="mx-auto max-w-3xl text-center">
            <Badge variant="secondary" className="mb-5 gap-1.5 border border-border">
              <Sparkles className="size-3.5 text-primary" />
              AI-powered AWS cost optimization
            </Badge>
            <h1 className="text-balance text-4xl font-semibold tracking-tight sm:text-5xl md:text-6xl">
              Optimize AWS Costs With{" "}
              <span className="text-gradient">AI-Powered Insights</span>
            </h1>
            <p className="mx-auto mt-5 max-w-2xl text-balance text-lg text-muted-foreground">
              Discover resources, analyze CloudWatch metrics, retrieve AWS best practices using
              RAG, and generate AI-powered cost optimization recommendations.
            </p>
            <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
              <Button asChild size="lg" className="w-full sm:w-auto">
                <Link to="/register">
                  Get Started <ArrowRight className="size-4" />
                </Link>
              </Button>
              <Button asChild size="lg" variant="outline" className="w-full sm:w-auto">
                <a href="#features">View Demo</a>
              </Button>
            </div>
          </div>

          {/* Product visualization mock */}
          <div className="mx-auto mt-14 max-w-4xl">
            <div className="rounded-2xl border border-border bg-card/70 p-2 shadow-2xl shadow-black/40 backdrop-blur">
              <div className="rounded-xl border border-border bg-background/60 p-5">
                <div className="mb-4 flex items-center gap-2">
                  <span className="size-2.5 rounded-full bg-rose-500/70" />
                  <span className="size-2.5 rounded-full bg-amber-500/70" />
                  <span className="size-2.5 rounded-full bg-emerald-500/70" />
                  <span className="ml-3 text-xs text-muted-foreground">AI Cloud Cost Optimizer — Dashboard</span>
                </div>
                <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
                  {[
                    { l: "Accounts", v: "3" },
                    { l: "Resources", v: "42" },
                    { l: "Findings", v: "19" },
                    { l: "Est. savings", v: "$1,250" },
                  ].map((k) => (
                    <div key={k.l} className="rounded-lg border border-border bg-card p-3 text-left">
                      <p className="text-[11px] text-muted-foreground">{k.l}</p>
                      <p className="mt-1 text-xl font-semibold tabular-nums">{k.v}</p>
                    </div>
                  ))}
                </div>
                <div className="mt-3 grid grid-cols-1 gap-3 sm:grid-cols-3">
                  <div className="rounded-lg border border-border bg-card p-3 sm:col-span-2">
                    <p className="mb-3 text-xs text-muted-foreground">Findings distribution</p>
                    <div className="flex h-24 items-end gap-3">
                      {[60, 35, 80, 50].map((h, i) => (
                        <div key={i} className="flex-1 rounded-t-md bg-primary/70" style={{ height: `${h}%` }} />
                      ))}
                    </div>
                  </div>
                  <div className="flex items-center justify-center rounded-lg border border-border bg-card p-3">
                    <div className="size-20 rounded-full border-8 border-primary/70 border-r-chart-2 border-b-chart-3" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features */}
      <section id="features" className="border-b border-border py-20 md:py-24">
        <div className="mx-auto max-w-6xl px-4 md:px-6">
          <SectionHeading
            eyebrow="Capabilities"
            title="Everything you need to cut AWS spend"
            sub="A complete optimization pipeline — from discovery to AI-backed action."
          />
          <div className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {FEATURES.map((f) => (
              <div
                key={f.title}
                className="group rounded-xl border border-border bg-card p-6 transition-colors hover:border-primary/40"
              >
                <div className="flex size-11 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <f.icon className="size-5" />
                </div>
                <h3 className="mt-4 font-medium">{f.title}</h3>
                <p className="mt-1.5 text-sm text-muted-foreground">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How it works */}
      <section id="how" className="border-b border-border py-20 md:py-24">
        <div className="mx-auto max-w-6xl px-4 md:px-6">
          <SectionHeading eyebrow="Workflow" title="How it works" />
          <div className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {STEPS.map((s, i) => (
              <div key={s.title} className="relative rounded-xl border border-border bg-card p-5">
                <div className="flex items-center gap-3">
                  <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                    <s.icon className="size-4.5" />
                  </div>
                  <span className="text-xs font-semibold text-muted-foreground">
                    Step {i + 1}
                  </span>
                </div>
                <h3 className="mt-3 text-sm font-medium">{s.title}</h3>
                <p className="mt-1 text-xs text-muted-foreground">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Supported resources */}
      <section className="border-b border-border py-20 md:py-24">
        <div className="mx-auto max-w-6xl px-4 md:px-6">
          <SectionHeading eyebrow="Coverage" title="Supported AWS resources" />
          <div className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {SUPPORTED.map((r) => (
              <div key={r.name} className="rounded-xl border border-border bg-card p-6 text-center">
                <div className="mx-auto flex size-12 items-center justify-center rounded-xl bg-primary/10 text-primary">
                  <r.icon className="size-6" />
                </div>
                <h3 className="mt-4 font-semibold">{r.name}</h3>
                <p className="mt-1 text-sm text-muted-foreground">{r.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Roadmap */}
      <section className="border-b border-border py-20 md:py-24">
        <div className="mx-auto max-w-6xl px-4 md:px-6">
          <SectionHeading eyebrow="Coming soon" title="Future roadmap" />
          <div className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {ROADMAP.map((r) => (
              <div key={r.name} className="rounded-xl border border-dashed border-border bg-card/50 p-5">
                <div className="flex items-center justify-between">
                  <div className="flex size-9 items-center justify-center rounded-lg bg-secondary text-muted-foreground">
                    <r.icon className="size-4.5" />
                  </div>
                  <Badge variant="outline" className="text-[10px]">Soon</Badge>
                </div>
                <h3 className="mt-3 text-sm font-medium">{r.name}</h3>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing */}
      <section id="pricing" className="border-b border-border py-20 md:py-24">
        <div className="mx-auto max-w-6xl px-4 md:px-6">
          <SectionHeading eyebrow="Pricing" title="Simple, transparent pricing" />
          <div className="mt-12 grid gap-6 lg:grid-cols-3">
            {PRICING.map((p) => (
              <div
                key={p.name}
                className={
                  "relative flex flex-col rounded-2xl border bg-card p-6 " +
                  (p.highlight ? "border-primary shadow-lg shadow-primary/10" : "border-border")
                }
              >
                {p.highlight && (
                  <Badge className="absolute -top-3 left-6">Most popular</Badge>
                )}
                <h3 className="font-semibold">{p.name}</h3>
                <div className="mt-3 flex items-baseline gap-1">
                  <span className="text-4xl font-semibold tracking-tight">{p.price}</span>
                  <span className="text-sm text-muted-foreground">{p.period}</span>
                </div>
                <p className="mt-2 text-sm text-muted-foreground">{p.desc}</p>
                <ul className="mt-6 flex-1 space-y-3">
                  {p.features.map((f) => (
                    <li key={f} className="flex items-center gap-2.5 text-sm">
                      <Check className="size-4 shrink-0 text-primary" />
                      {f}
                    </li>
                  ))}
                </ul>
                <Button asChild className="mt-6 w-full" variant={p.highlight ? "default" : "outline"}>
                  <Link to="/register">{p.cta}</Link>
                </Button>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 md:py-24">
        <div className="mx-auto max-w-4xl px-4 text-center md:px-6">
          <BarChart3 className="mx-auto size-10 text-primary" />
          <h2 className="mt-4 text-3xl font-semibold tracking-tight sm:text-4xl">
            Start optimizing your AWS bill today
          </h2>
          <p className="mt-3 text-muted-foreground">
            Connect an account and get AI-powered recommendations in minutes.
          </p>
          <Button asChild size="lg" className="mt-6">
            <Link to="/register">
              Get Started <ArrowRight className="size-4" />
            </Link>
          </Button>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border py-10">
        <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-4 md:flex-row md:px-6">
          <div className="flex items-center gap-2.5">
            <div className="flex size-7 items-center justify-center rounded-lg bg-gradient-to-br from-indigo-500 to-violet-600 text-white">
              <Sparkles className="size-4" />
            </div>
            <span className="text-sm font-medium">CostOptimizer</span>
          </div>
          <p className="text-xs text-muted-foreground">
            © {new Date().getFullYear()} AI Cloud Cost Optimizer. All rights reserved.
          </p>
          <div className="flex items-center gap-5 text-xs text-muted-foreground">
            <a href="#features" className="hover:text-foreground">Features</a>
            <a href="#pricing" className="hover:text-foreground">Pricing</a>
            <Link to="/login" className="hover:text-foreground">Login</Link>
          </div>
        </div>
      </footer>
    </div>
  )
}
