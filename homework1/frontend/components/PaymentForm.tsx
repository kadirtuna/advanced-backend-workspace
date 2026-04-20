"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { fetchPaymentMethods, processPayment, PaymentMethodInfo, PaymentResponse } from "@/lib/api";
import CreditCardForm from "./CreditCardForm";
import PayPalForm from "./PayPalForm";
import ApplePayForm from "./ApplePayForm";

const METHOD_ICONS: Record<string, string> = {
  CREDIT_CARD: "💳",
  PAYPAL: "🅿️",
  APPLE_PAY: "🍎",
};
const CURRENCIES = ["TRY", "USD", "EUR", "GBP"];
const CURRENCY_SYMBOLS: Record<string, string> = { TRY: "₺", USD: "$", EUR: "€", GBP: "£" };

export default function PaymentForm() {
  const [methods, setMethods] = useState<PaymentMethodInfo[]>([]);
  const [selectedMethod, setSelectedMethod] = useState("");
  const [amount, setAmount] = useState("");
  const [currency, setCurrency] = useState("TRY");
  const [description, setDescription] = useState("");
  const [details, setDetails] = useState<Record<string, string>>({});
  const [result, setResult] = useState<PaymentResponse | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchPaymentMethods()
      .then((data) => {
        setMethods(data);
        if (data.length > 0) setSelectedMethod(data[0].name);
      })
      .catch(() => setError("Could not reach the backend. Is the server running?"));
  }, []);

  const handleDetailChange = (key: string, value: string) =>
    setDetails((prev) => ({ ...prev, [key]: value }));

  const handleMethodChange = (name: string) => {
    setSelectedMethod(name);
    setDetails({});
    setResult(null);
    setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const res = await processPayment({
        paymentMethod: selectedMethod,
        amount: parseFloat(amount),
        currency,
        description,
        paymentDetails: details,
      });
      setResult(res);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Payment failed.");
    } finally {
      setLoading(false);
    }
  };

  const renderMethodForm = () => {
    switch (selectedMethod) {
      case "CREDIT_CARD": return <CreditCardForm details={details} onChange={handleDetailChange} />;
      case "PAYPAL":      return <PayPalForm details={details} onChange={handleDetailChange} />;
      case "APPLE_PAY":   return <ApplePayForm details={details} onChange={handleDetailChange} />;
      default:            return null;
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <div className="px-6 pt-6 pb-5 border-b border-gray-100">
        <h2 className="text-lg font-bold text-gray-900">Payment Details</h2>
        <p className="text-sm text-gray-400 mt-0.5">All transactions are encrypted end-to-end.</p>
      </div>

      <form onSubmit={handleSubmit} className="p-6 space-y-7">
        {/* Step 1 — Method */}
        <div>
          <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-3">
            1 — Payment method
          </p>
          {methods.length === 0 && !error && (
            <div className="flex gap-3">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex-1 h-20 bg-gray-100 rounded-2xl animate-pulse" />
              ))}
            </div>
          )}
          <div className="flex gap-3">
            {methods.map((m) => (
              <button
                key={m.name}
                type="button"
                onClick={() => handleMethodChange(m.name)}
                className={`flex-1 py-4 px-3 rounded-2xl border-2 transition-all flex flex-col items-center gap-2 ${
                  selectedMethod === m.name
                    ? "border-orange-500 bg-orange-50 shadow-sm"
                    : "border-gray-200 text-gray-400 hover:border-gray-300 hover:bg-gray-50"
                }`}
              >
                <span className="text-2xl leading-none">{METHOD_ICONS[m.name] ?? "💰"}</span>
                <span className={`text-xs font-bold ${selectedMethod === m.name ? "text-orange-700" : "text-gray-400"}`}>
                  {m.displayName}
                </span>
              </button>
            ))}
          </div>
          {selectedMethod && (
            <p className="text-xs text-gray-400 mt-2.5">
              {methods.find((m) => m.name === selectedMethod)?.description}
            </p>
          )}
        </div>

        {/* Step 2 — Amount */}
        <div>
          <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-3">
            2 — Amount
          </p>
          <div className="flex gap-2">
            <div className="flex-1 relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 font-bold text-lg select-none">
                {CURRENCY_SYMBOLS[currency] ?? currency}
              </span>
              <input
                type="number"
                min="0.01"
                step="0.01"
                required
                placeholder="0.00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full border border-gray-200 rounded-xl pl-9 pr-4 py-3 text-gray-900 font-bold text-xl focus:outline-none focus:ring-2 focus:ring-orange-400"
              />
            </div>
            <select
              value={currency}
              onChange={(e) => setCurrency(e.target.value)}
              className="w-24 border border-gray-200 rounded-xl px-3 py-3 text-gray-700 font-semibold text-sm focus:outline-none focus:ring-2 focus:ring-orange-400 bg-white"
            >
              {CURRENCIES.map((c) => (
                <option key={c}>{c}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Step 3 — Details */}
        <div>
          <p className="text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-3">
            3 — Details
          </p>
          <div className="rounded-2xl border border-gray-100 bg-gray-50 p-5">
            {renderMethodForm()}
          </div>
        </div>

        {/* Description */}
        <div>
          <label className="block text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-2">
            Order note (optional)
          </label>
          <input
            type="text"
            placeholder="e.g. Order #12345"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
          />
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={loading || !selectedMethod || !amount}
          className="w-full bg-[#FF6000] hover:bg-orange-600 disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed text-white font-bold py-4 rounded-xl transition-colors flex items-center justify-center gap-2 text-base shadow-sm"
        >
          {loading ? (
            <>
              <svg className="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
              </svg>
              Processing…
            </>
          ) : (
            <>
              <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2.5} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
              Pay Now
            </>
          )}
        </button>

        {/* Success */}
        {result?.status === "SUCCESS" && (
          <div className="rounded-2xl bg-green-50 border border-green-200 p-5">
            <div className="flex items-center gap-2 mb-1">
              <svg className="w-5 h-5 text-green-500 shrink-0" fill="none" stroke="currentColor" strokeWidth={2.5} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p className="font-bold text-green-800">Payment Successful!</p>
            </div>
            <p className="text-sm text-green-700 ml-7">{result.message}</p>
            <div className="mt-3 pt-3 border-t border-green-200 flex justify-between items-center ml-7">
              <p className="text-xs text-gray-400 font-mono truncate">TxID: {result.transactionId}</p>
              <Link href="/payments" className="text-xs font-semibold text-green-700 hover:underline shrink-0 ml-2">
                View history →
              </Link>
            </div>
          </div>
        )}

        {/* Failed / Error */}
        {(result?.status === "FAILED" || error) && (
          <div className="rounded-2xl bg-red-50 border border-red-200 p-4 flex gap-3 items-start">
            <svg className="w-5 h-5 text-red-400 mt-0.5 shrink-0" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m9.303 3.376c.866 1.5-.217 3.374-1.948 3.374H4.645c-1.73 0-2.813-1.874-1.948-3.374L10.05 3.378c.866-1.5 3.032-1.5 3.898 0L21.303 16.126z" />
            </svg>
            <p className="text-sm text-red-700">{result?.message ?? error}</p>
          </div>
        )}
      </form>
    </div>
  );
}
