import { Sparkles } from "lucide-react"

interface Props {
  text: string
}

// Visually highlighted block for AI-generated prose.
export default function AiRecommendation({ text }: Props) {
  return (
    <div className="relative overflow-hidden rounded-xl border border-violet-500/20 bg-gradient-to-br from-violet-500/10 via-indigo-500/5 to-transparent p-4">
      <div className="mb-2 flex items-center gap-2">
        <Sparkles className="size-4 text-violet-400" />
        <span className="text-xs font-semibold uppercase tracking-wide text-violet-300">
          AI Recommendation
        </span>
      </div>
      <p className="whitespace-pre-wrap text-sm leading-relaxed text-foreground/90">
        {text}
      </p>
    </div>
  )
}
