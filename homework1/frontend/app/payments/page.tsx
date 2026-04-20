"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { fetchPaymentHistory, PaymentResponse } from "@/lib/api";

const STATUS: Record<string, { label: string; cls: string; dot: string }> = {
  SUCCESS: { label: "Success", cls: "bg-green-100 text-green-700",   dot: "bg-green-500"  },
  FAILED:  { label: "Failed",  cls: "bg-red-100 text-red-600",       dot: "bg-red-500"    },
  PENDING: { label: "Pending", cls: "bg-yellow-100 text-yellow-700", dot: "bg-yellow-400" },
};
const METHOD_ICONS: Record<string, string> = { CREDIT_CARD: "💳", PAYPAL: "🅿️", APPLE_PAY: "🍎" };
const SYMBOLS: Record<string, string> = { TRY: "₺", USD: "$", EUR: "€", GBP: "£" };

export default function PaymentHistoryPage() {
  const [payments, setPayments] = useState<PaymentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchPaymentHistory()
      .then(setPayments)
      .catch(() => setError("Could not load payment history."))
      .finally(() => setLoading(false));
  }, []);

  const successful = payments.filter((p) => p.status === "SUCCESS");
  const total = successful.reduce((s, p) => s + Number(p.amount), 0);

  return (
    <main className="py-10 px-4">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900">Transaction History</h1>
            <p className="text-sm text-gray-400 mt-0.5">All payments processed through the system</p>
          </div>
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 bg-[#FF6000] hover:bg-orange-600 text-white text-sm font-semibold px-4 py-2.5 rounded-xl transition-colors shadow-sm"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2.5} viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            New Payment
          </Link>
        </div>

        {/* Stats */}
        {!loading && !error && payments.length > 0 && (
          <div className="grid grid-cols-3 gap-3 mb-6">
            {[
              { label: "Total Transactions", value: payments.length,          color: "text-gray-800"   },
              { label: "Successful",          value: successful.length,        color: "text-green-600"  },
              { label: "Total Volume",         value: `₺${total.toFixed(2)}`, color: "text-[#FF6000]"  },
            ].map(({ label, value, color }) => (
              <div key={label} className="bg-white rounded-2xl border border-gray-100 shadow-sm px-4 py-5 text-center">
                <p className={`text-2xl font-extrabold ${color}`}>{value}</p>
                <p className="text-xs text-gray-400 mt-1 font-medium">{label}</p>
              </div>
            ))}
          </div>
        )}

        {/* Skeleton */}
        {loading && (
          <div className="space-y-3">
            {[1, 2, 3].map((i) => (
              <div key={i} className="bg-white rounded-2xl border border-gray-100 p-5 animate-pulse flex gap-4">
                <div className="w-12 h-12 bg-gray-200 rounded-xl shrink-0" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-1/3" />
                  <div className="h-3 bg-gray-100 rounded w-1/2" />
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-2xl p-5 text-sm text-red-700">{error}</div>
        )}

        {/* Empty */}
        {!loading && !error && payments.length === 0 && (
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm py-20 text-center">
            <div className="text-5xl mb-4">💳</div>
            <p className="font-bold text-gray-700 text-lg">No transactions yet</p>
            <p className="text-sm text-gray-400 mt-1 mb-6">Make your first payment to see it here.</p>
            <Link
              href="/"
              className="inline-flex bg-[#FF6000] hover:bg-orange-600 text-white text-sm font-semibold px-5 py-2.5 rounded-xl transition-colors"
            >
              Make a payment
            </Link>
          </div>
        )}

        {/* List */}
        <div className="space-y-3">
          {payments.map((p) => {
            const s = STATUS[p.status] ?? { label: p.status, cls: "bg-gray-100 text-gray-600", dot: "bg-gray-400" };
            const sym = SYMBOLS[p.currency] ?? p.currency;
            return (
              <div
                key={p.id}
                className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex items-center gap-4 hover:shadow-md transition-shadow"
              >
                <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center text-2xl shrink-0">
                  {METHOD_ICONS[p.paymentMethod] ?? "💰"}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="font-bold text-gray-900">
                      {sym}{Number(p.amount).toFixed(2)} {p.currency}
                    </p>
                    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-semibold ${s.cls}`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${s.dot}`} />
                      {s.label}
                    </span>
                  </div>
                  <p className="text-sm text-gray-500 truncate mt-0.5">
                    {p.paymentMethod.replace(/_/g, " ")}
                    {p.description ? ` · ${p.description}` : ""}
                  </p>
                  {p.transactionId && (
                    <p className="text-xs text-gray-300 font-mono truncate mt-0.5">{p.transactionId}</p>
                  )}
                </div>
                {p.createdAt && (
                  <p className="text-xs text-gray-400 shrink-0">
                    {new Date(p.createdAt).toLocaleString("tr-TR", {
                      day: "2-digit", month: "short", year: "numeric",
                      hour: "2-digit", minute: "2-digit",
                    })}
                  </p>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </main>
  );
}
