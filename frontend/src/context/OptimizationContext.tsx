import {
  createContext,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react"
import type { AccountOptimizationResponse } from "@/api/types"

export interface OptimizationRun {
  accountId: string
  accountName: string
  scope: string // "Entire account" or a resource type
  result: AccountOptimizationResponse
  ranAt: string
}

interface OptimizationContextValue {
  lastRun: OptimizationRun | null
  setLastRun: (run: OptimizationRun) => void
}

const OptimizationContext = createContext<OptimizationContextValue | undefined>(undefined)

export function OptimizationProvider({ children }: { children: ReactNode }) {
  const [lastRun, setLastRun] = useState<OptimizationRun | null>(null)
  const value = useMemo(() => ({ lastRun, setLastRun }), [lastRun])
  return (
    <OptimizationContext.Provider value={value}>
      {children}
    </OptimizationContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useOptimization(): OptimizationContextValue {
  const ctx = useContext(OptimizationContext)
  if (!ctx) throw new Error("useOptimization must be used within OptimizationProvider")
  return ctx
}
