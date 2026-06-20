import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react"
import { listAccounts } from "@/api/cloud"
import { apiErrorMessage } from "@/api/client"
import type { CloudAccount } from "@/api/types"

const SELECTED_KEY = "acco_selected_account"

interface AccountContextValue {
  accounts: CloudAccount[]
  loading: boolean
  error: string | null
  refresh: () => Promise<void>
  selectedAccountId: string | undefined
  setSelectedAccountId: (id: string) => void
  selectedAccount: CloudAccount | undefined
}

const AccountContext = createContext<AccountContextValue | undefined>(undefined)

export function AccountProvider({ children }: { children: ReactNode }) {
  const [accounts, setAccounts] = useState<CloudAccount[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAccountId, setSelectedIdState] = useState<string | undefined>(
    () => localStorage.getItem(SELECTED_KEY) ?? undefined
  )

  const setSelectedAccountId = useCallback((id: string) => {
    setSelectedIdState(id)
    localStorage.setItem(SELECTED_KEY, id)
  }, [])

  const refresh = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const list = await listAccounts()
      setAccounts(list)
      // Auto-select the first account if none is chosen or the chosen one vanished.
      setSelectedIdState((current) => {
        const stillValid = current && list.some((a) => a.id === current)
        const next = stillValid ? current : list[0]?.id
        if (next) localStorage.setItem(SELECTED_KEY, next)
        return next
      })
    } catch (e) {
      setError(apiErrorMessage(e, "Failed to load cloud accounts"))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void refresh()
  }, [refresh])

  const value = useMemo<AccountContextValue>(
    () => ({
      accounts,
      loading,
      error,
      refresh,
      selectedAccountId,
      setSelectedAccountId,
      selectedAccount: accounts.find((a) => a.id === selectedAccountId),
    }),
    [accounts, loading, error, refresh, selectedAccountId, setSelectedAccountId]
  )

  return <AccountContext.Provider value={value}>{children}</AccountContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAccountContext(): AccountContextValue {
  const ctx = useContext(AccountContext)
  if (!ctx) throw new Error("useAccountContext must be used within AccountProvider")
  return ctx
}
