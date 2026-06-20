import { useState } from "react"
import { Outlet } from "react-router-dom"
import { Sheet, SheetContent, SheetTitle } from "@/components/ui/sheet"
import SidebarContent from "./Sidebar"
import Topbar from "./Topbar"

export default function AppLayout() {
  const [mobileNav, setMobileNav] = useState(false)

  return (
    <div className="flex h-screen overflow-hidden bg-background">
      {/* Desktop sidebar: icon rail on md, full on lg */}
      <aside className="hidden w-16 shrink-0 border-r border-border md:block lg:w-64">
        <SidebarContent />
      </aside>

      {/* Mobile drawer */}
      <Sheet open={mobileNav} onOpenChange={setMobileNav}>
        <SheetContent side="left" className="w-64 p-0">
          <SheetTitle className="sr-only">Navigation</SheetTitle>
          <SidebarContent expanded onNavigate={() => setMobileNav(false)} />
        </SheetContent>
      </Sheet>

      <div className="flex min-w-0 flex-1 flex-col">
        <Topbar onOpenMobileNav={() => setMobileNav(true)} />
        <main className="flex-1 overflow-y-auto">
          <div className="mx-auto w-full max-w-6xl px-4 py-6 md:px-6 md:py-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
