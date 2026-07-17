"use client";

import Link from "next/link";
import { useSession, signIn, signOut } from "next-auth/react";
import { useState } from "react";
import { Layers, LogOut, LayoutDashboard, Coins, ShoppingBag, ChevronDown } from "lucide-react";
import Logo from "./Logo";

export default function Navbar() {
  const { data: session } = useSession();
  const [dropdownOpen, setDropdownOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 bg-[#0a0a0a]/85 backdrop-blur-md border-b border-neutral-800 px-6 py-4 flex items-center justify-between">
      <div className="flex items-center space-x-8">
        <Link href="/" className="flex items-center">
          <Logo size={28} />
        </Link>

        <div className="hidden md:flex items-center space-x-6 text-sm font-medium text-neutral-400">
          <Link href="/pricing" className="hover:text-white transition">Pricing</Link>
          <Link href="/marketplace" className="hover:text-white transition">Marketplace</Link>
          {session && (
            <Link href="/dashboard" className="hover:text-white transition flex items-center space-x-1">
              <LayoutDashboard size={14} />
              <span>Dashboard</span>
            </Link>
          )}
        </div>
      </div>

      <div className="flex items-center space-x-4">
        {session ? (
          <div className="relative">
            <button
              onClick={() => setDropdownOpen(!dropdownOpen)}
              className="flex items-center space-x-2 bg-neutral-900 border border-neutral-800 px-3 py-1.5 rounded-full hover:bg-neutral-800 transition"
            >
              {session.user?.image ? (
                <img
                  src={session.user.image}
                  alt={session.user.name || "User Avatar"}
                  className="w-6 h-6 rounded-full"
                />
              ) : (
                <div className="w-6 h-6 rounded-full bg-neutral-800 flex items-center justify-center text-xs font-semibold">
                  {session.user?.name?.charAt(0) || "U"}
                </div>
              )}
              <span className="text-xs font-semibold text-neutral-300 hidden sm:inline">
                {session.user?.name || "Developer"}
              </span>
              <ChevronDown size={14} className="text-neutral-500" />
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-48 rounded-xl bg-neutral-900 border border-neutral-800 p-2 shadow-2xl">
                <Link
                  href="/dashboard"
                  onClick={() => setDropdownOpen(false)}
                  className="flex items-center space-x-2 w-full text-left px-3 py-2 rounded-lg text-sm text-neutral-300 hover:bg-neutral-800 hover:text-white transition"
                >
                  <LayoutDashboard size={16} />
                  <span>Dashboard</span>
                </Link>
                <Link
                  href="/pricing"
                  onClick={() => setDropdownOpen(false)}
                  className="flex items-center space-x-2 w-full text-left px-3 py-2 rounded-lg text-sm text-neutral-300 hover:bg-neutral-800 hover:text-white transition"
                >
                  <Coins size={16} />
                  <span>Subscription Upgrade</span>
                </Link>
                <Link
                  href="/marketplace"
                  onClick={() => setDropdownOpen(false)}
                  className="flex items-center space-x-2 w-full text-left px-3 py-2 rounded-lg text-sm text-neutral-300 hover:bg-neutral-800 hover:text-white transition"
                >
                  <ShoppingBag size={16} />
                  <span>Templates Market</span>
                </Link>
                <div className="h-px bg-neutral-800 my-1"></div>
                <button
                  onClick={() => {
                    signOut();
                    setDropdownOpen(false);
                  }}
                  className="flex items-center space-x-2 w-full text-left px-3 py-2 rounded-lg text-sm text-rose-400 hover:bg-rose-500/10 hover:text-rose-300 transition"
                >
                  <LogOut size={16} />
                  <span>Sign Out</span>
                </button>
              </div>
            )}
          </div>
        ) : (
          <button
            onClick={() => signIn("google")}
            className="bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm px-4 py-2 rounded-lg shadow-lg shadow-indigo-600/30 active:scale-95 transition"
          >
            Sign in with Google
          </button>
        )}
      </div>
    </nav>
  );
}
