import PaymentForm from "@/components/PaymentForm";

export default function Home() {
  return (
    <main className="py-10 px-4">
      {/* Hero */}
      <div className="max-w-5xl mx-auto mb-8">
        <div className="rounded-3xl bg-gradient-to-br from-[#FF6000] to-orange-400 px-8 py-8 flex flex-col sm:flex-row items-center justify-between gap-6 shadow-lg overflow-hidden relative">
          <div className="absolute -top-10 -right-10 w-48 h-48 bg-white/10 rounded-full" />
          <div className="absolute -bottom-8 -left-8 w-32 h-32 bg-white/10 rounded-full" />
          <div className="relative z-10">
            <div className="inline-flex items-center gap-2 bg-white/20 rounded-full px-3 py-1 mb-3">
              <span className="text-white text-xs font-bold">n11</span>
              <span className="text-orange-100 text-xs font-medium">Backend Bootcamp</span>
            </div>
            <h1 className="text-white text-3xl font-extrabold leading-tight">Complete Your Payment</h1>
            <p className="text-orange-100 text-sm mt-1.5">Secure checkout powered by Spring Boot &amp; Chain of Responsibility</p>
          </div>
          <div className="relative z-10 bg-white/20 rounded-2xl p-5 shrink-0 backdrop-blur-sm">
            <svg className="w-12 h-12 text-white" fill="none" stroke="currentColor" strokeWidth={1.5} viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
            </svg>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-5xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <PaymentForm />
        </div>

        <div className="space-y-4">
          {/* Security */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 className="font-bold text-gray-800 mb-4 text-xs uppercase tracking-widest">Why it&apos;s secure</h3>
            <ul className="space-y-3">
              {[
                { icon: "🔒", text: "256-bit SSL encryption" },
                { icon: "🛡️", text: "Fraud detection on every transaction" },
                { icon: "✅", text: "Input validation before processing" },
                { icon: "📋", text: "Full transaction audit trail" },
              ].map(({ icon, text }) => (
                <li key={text} className="flex items-center gap-2.5 text-sm text-gray-600">
                  <span className="text-base shrink-0">{icon}</span>
                  <span>{text}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Accepted methods */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 className="font-bold text-gray-800 mb-4 text-xs uppercase tracking-widest">Accepted Methods</h3>
            <div className="space-y-3">
              {[
                { icon: "💳", label: "Credit / Debit Card", sub: "Visa, Mastercard, Amex" },
                { icon: "🅿️", label: "PayPal", sub: "Balance or linked bank" },
                { icon: "🍎", label: "Apple Pay", sub: "Face ID / Touch ID" },
              ].map(({ icon, label, sub }) => (
                <div key={label} className="flex items-center gap-3">
                  <div className="w-9 h-9 bg-gray-100 rounded-xl flex items-center justify-center text-lg shrink-0">
                    {icon}
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-gray-700">{label}</p>
                    <p className="text-xs text-gray-400">{sub}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* About */}
          <div className="bg-gradient-to-br from-orange-50 to-orange-100 border border-orange-200 rounded-2xl p-5">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-7 h-7 bg-[#FF6000] rounded-lg flex items-center justify-center">
                <span className="text-white font-extrabold text-xs">n11</span>
              </div>
              <p className="text-xs font-bold text-orange-800 uppercase tracking-wide">About this project</p>
            </div>
            <p className="text-sm text-orange-900 leading-relaxed">
              Built with <strong>Java Spring Boot</strong>, <strong>SOLID principles</strong>,{" "}
              <strong>Chain of Responsibility</strong> pattern, and <strong>Java Reflection</strong> for dynamic
              payment provider discovery.
            </p>
            <div className="mt-4 pt-3 border-t border-orange-200">
              <p className="text-xs text-orange-600 font-semibold">N11 Backend Bootcamp — Homework 1</p>
              <p className="text-xs text-orange-700 mt-1 font-medium">Made by Kadir Tuna · Software Engineer</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
