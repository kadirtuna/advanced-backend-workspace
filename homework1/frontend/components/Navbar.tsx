"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

export default function Navbar() {
  const pathname = usePathname();

  return (
    <header className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
      <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
        <Link href="/" className="flex items-center gap-3 group">
          <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-[#FF6000] shadow-md group-hover:scale-105 transition-transform">
            <span className="text-white font-extrabold text-lg leading-none tracking-tight">n11</span>
          </div>
          <div className="leading-tight">
            <p className="text-sm font-extrabold text-gray-900 tracking-tight">N11 Backend Bootcamp</p>
            <p className="text-[10px] text-gray-400 font-medium uppercase tracking-widest">Payment System</p>
          </div>
        </Link>

        <nav className="flex items-center gap-1">
          <Link
            href="/"
            className={`px-4 py-2 rounded-lg text-sm font-semibold transition-colors ${
              pathname === "/"
                ? "bg-orange-50 text-orange-600"
                : "text-gray-500 hover:text-gray-800 hover:bg-gray-50"
            }`}
          >
            Pay
          </Link>
          <Link
            href="/payments"
            className={`px-4 py-2 rounded-lg text-sm font-semibold transition-colors ${
              pathname === "/payments"
                ? "bg-orange-50 text-orange-600"
                : "text-gray-500 hover:text-gray-800 hover:bg-gray-50"
            }`}
          >
            History
          </Link>
        </nav>
      </div>
    </header>
  );
}
