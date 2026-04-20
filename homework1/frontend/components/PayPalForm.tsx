"use client";

interface Props {
  details: Record<string, string>;
  onChange: (key: string, value: string) => void;
}

export default function PayPalForm({ details, onChange }: Props) {
  return (
    <div className="space-y-3">
      <div>
        <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
          PayPal Email
        </label>
        <input
          type="email"
          placeholder="you@example.com"
          value={details.email ?? ""}
          onChange={(e) => onChange("email", e.target.value)}
          className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400"
        />
      </div>
      <p className="text-xs text-gray-400 flex items-center gap-1.5">
        <span>🔐</span>
        You will be redirected to PayPal to complete authorization securely.
      </p>
    </div>
  );
}
